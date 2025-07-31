package com.intuit.gqlex.validation.core;

import graphql.language.Document;
import graphql.parser.Parser;
import com.intuit.gqlex.validation.rules.ValidationRule;
import com.intuit.gqlex.validation.schema.SchemaProvider;
import com.intuit.gqlex.transformation.optimization.ASTCache;
import com.intuit.gqlex.transformation.optimization.PerformanceOptimizationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Main orchestrator for GraphQL validation operations.
 * This class is completely generic and works with any GraphQL validation scenario.
 */
public class GraphQLValidator {
    
    private final List<ValidationRule> rules = new CopyOnWriteArrayList<>();
    private final SchemaProvider schemaProvider;
    private final Parser parser;
    private final ASTCache astCache;
    
    /**
     * Creates a GraphQL validator with optional schema provider.
     * 
     * @param schemaProvider the schema provider (can be null for schema-independent validation)
     */
    public GraphQLValidator(SchemaProvider schemaProvider) {
        this.schemaProvider = schemaProvider;
        this.parser = new Parser();
        this.astCache = PerformanceOptimizationManager.getInstance().getAstCache();
    }
    
    /**
     * Creates a GraphQL validator without schema provider.
     */
    public GraphQLValidator() {
        this(null);
    }
    
    /**
     * Validates a GraphQL document using all registered rules.
     * This method is generic and works with any GraphQL document structure.
     * 
     * @param document the GraphQL document to validate
     * @return validation result containing all issues found
     */
    public ValidationResult validate(Document document) {
        ValidationContext context = new ValidationContext(document, schemaProvider);
        ValidationResult result = new ValidationResult();
        
        // Apply all rules generically - no field-specific logic
        for (ValidationRule rule : rules) {
            try {
                if (rule.isEnabled(context)) {
                    rule.validate(context, result);
                }
            } catch (Exception e) {
                // Generic error handling - no field-specific logic
                result.addError(ValidationError.generic(rule.getName(), 
                    "Validation rule failed: " + e.getMessage()));
            }
        }
        
        return result;
    }
    
    /**
     * Validates a GraphQL query string.
     * This method is generic and works with any GraphQL query structure.
     * 
     * @param queryString the GraphQL query string to validate
     * @return validation result containing all issues found
     */
    public ValidationResult validate(String queryString) {
        try {
            // Use AST cache for performance optimization
            Document document = astCache.getOrParse(queryString);
            return validate(document);
        } catch (Exception e) {
            // Generic parsing error handling
            ValidationResult result = new ValidationResult();
            result.addError(ValidationError.syntax("PARSE_ERROR", 
                "Failed to parse GraphQL document: " + e.getMessage()));
            return result;
        }
    }
    
    /**
     * Validates a GraphQL document with specific rules.
     * This method is generic and works with any validation rules.
     * 
     * @param document the GraphQL document to validate
     * @param specificRules the specific rules to apply
     * @return validation result containing all issues found
     */
    public ValidationResult validate(Document document, List<ValidationRule> specificRules) {
        ValidationContext context = new ValidationContext(document, schemaProvider);
        ValidationResult result = new ValidationResult();
        
        // Apply specific rules generically
        for (ValidationRule rule : specificRules) {
            try {
                if (rule.isEnabled(context)) {
                    rule.validate(context, result);
                }
            } catch (Exception e) {
                // Generic error handling
                result.addError(ValidationError.generic(rule.getName(), 
                    "Validation rule failed: " + e.getMessage()));
            }
        }
        
        return result;
    }
    
    /**
     * Validates a GraphQL query string with specific rules.
     * This method is generic and works with any GraphQL query structure.
     * 
     * @param queryString the GraphQL query string to validate
     * @param specificRules the specific rules to apply
     * @return validation result containing all issues found
     */
    public ValidationResult validate(String queryString, List<ValidationRule> specificRules) {
        try {
            Document document = astCache.getOrParse(queryString);
            return validate(document, specificRules);
        } catch (Exception e) {
            // Generic parsing error handling
            ValidationResult result = new ValidationResult();
            result.addError(ValidationError.syntax("PARSE_ERROR", 
                "Failed to parse GraphQL document: " + e.getMessage()));
            return result;
        }
    }
    
    /**
     * Validates a GraphQL document with a single rule.
     * This method is generic and works with any validation rule.
     * 
     * @param document the GraphQL document to validate
     * @param rule the specific rule to apply
     * @return validation result containing all issues found
     */
    public ValidationResult validate(Document document, ValidationRule rule) {
        ValidationContext context = new ValidationContext(document, schemaProvider);
        ValidationResult result = new ValidationResult();
        
        try {
            if (rule.isEnabled(context)) {
                rule.validate(context, result);
            }
        } catch (Exception e) {
            // Generic error handling
            result.addError(ValidationError.generic(rule.getName(), 
                "Validation rule failed: " + e.getMessage()));
        }
        
        return result;
    }
    
    /**
     * Validates a GraphQL query string with a single rule.
     * This method is generic and works with any GraphQL query structure.
     * 
     * @param queryString the GraphQL query string to validate
     * @param rule the specific rule to apply
     * @return validation result containing all issues found
     */
    public ValidationResult validate(String queryString, ValidationRule rule) {
        try {
            Document document = astCache.getOrParse(queryString);
            return validate(document, rule);
        } catch (Exception e) {
            // Generic parsing error handling
            ValidationResult result = new ValidationResult();
            result.addError(ValidationError.syntax("PARSE_ERROR", 
                "Failed to parse GraphQL document: " + e.getMessage()));
            return result;
        }
    }
    
    /**
     * Adds a validation rule to the validator.
     * This method is generic and works with any validation rule.
     * 
     * @param rule the validation rule to add
     * @return this validator for method chaining
     */
    public GraphQLValidator addRule(ValidationRule rule) {
        if (rule != null) {
            rules.add(rule);
        }
        return this;
    }
    
    /**
     * Adds multiple validation rules to the validator.
     * This method is generic and works with any validation rules.
     * 
     * @param rules the validation rules to add
     * @return this validator for method chaining
     */
    public GraphQLValidator addRules(List<ValidationRule> rules) {
        if (rules != null) {
            for (ValidationRule rule : rules) {
                addRule(rule);
            }
        }
        return this;
    }
    
    /**
     * Removes a validation rule from the validator.
     * 
     * @param ruleName the name of the rule to remove
     * @return this validator for method chaining
     */
    public GraphQLValidator removeRule(String ruleName) {
        rules.removeIf(rule -> rule.getName().equals(ruleName));
        return this;
    }
    
    /**
     * Removes all validation rules from the validator.
     * 
     * @return this validator for method chaining
     */
    public GraphQLValidator clearRules() {
        rules.clear();
        return this;
    }
    
    /**
     * Gets all registered validation rules.
     * 
     * @return list of all validation rules
     */
    public List<ValidationRule> getRules() {
        return new ArrayList<>(rules);
    }
    
    /**
     * Gets validation rules by category.
     * This method is generic and works with any rule categories.
     * 
     * @param category the category to filter by
     * @return list of validation rules in the specified category
     */
    public List<ValidationRule> getRulesByCategory(String category) {
        return rules.stream()
            .filter(rule -> rule.getCategory().equals(category))
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Gets validation rules by level.
     * This method is generic and works with any validation levels.
     * 
     * @param level the level to filter by
     * @return list of validation rules at the specified level
     */
    public List<ValidationRule> getRulesByLevel(ValidationLevel level) {
        return rules.stream()
            .filter(rule -> rule.getLevel() == level)
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Checks if the validator has any rules.
     * 
     * @return true if there are rules registered
     */
    public boolean hasRules() {
        return !rules.isEmpty();
    }
    
    /**
     * Gets the number of registered rules.
     * 
     * @return the number of rules
     */
    public int getRuleCount() {
        return rules.size();
    }
    
    /**
     * Gets the schema provider.
     * 
     * @return the schema provider (can be null)
     */
    public SchemaProvider getSchemaProvider() {
        return schemaProvider;
    }
    
    /**
     * Checks if a schema provider is available.
     * 
     * @return true if schema provider is available
     */
    public boolean hasSchemaProvider() {
        return schemaProvider != null;
    }
    
    /**
     * Creates a new validator with the same configuration but different schema provider.
     * 
     * @param newSchemaProvider the new schema provider
     * @return new validator instance
     */
    public GraphQLValidator withSchemaProvider(SchemaProvider newSchemaProvider) {
        GraphQLValidator newValidator = new GraphQLValidator(newSchemaProvider);
        newValidator.addRules(getRules());
        return newValidator;
    }
    
    /**
     * Returns a string representation of the validator.
     * 
     * @return string representation
     */
    @Override
    public String toString() {
        return String.format("GraphQLValidator{rules=%d, hasSchema=%s}", 
            getRuleCount(), hasSchemaProvider());
    }
} 