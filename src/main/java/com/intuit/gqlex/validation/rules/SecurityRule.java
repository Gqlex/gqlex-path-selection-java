package com.intuit.gqlex.validation.rules;

import com.intuit.gqlex.validation.core.ValidationContext;
import com.intuit.gqlex.validation.core.ValidationResult;
import com.intuit.gqlex.validation.core.ValidationLevel;
import graphql.language.Document;
import graphql.language.Field;
import graphql.language.Argument;
import graphql.language.StringValue;
import graphql.language.IntValue;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Validates GraphQL queries for security issues.
 * This rule is completely generic and works with any GraphQL document structure.
 */
public class SecurityRule extends ValidationRule {
    
    // Generic security patterns - work with any field/argument names
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "(?i)(union|select|insert|update|delete|drop|create|alter|exec|execute|script|javascript|eval|expression)");
    
    private static final Pattern XSS_PATTERN = Pattern.compile(
        "(?i)(<script|javascript:|vbscript:|onload=|onerror=|onclick=|<iframe|<object|<embed)");
    
    private static final Pattern PATH_TRAVERSAL_PATTERN = Pattern.compile(
        "(?i)(\\.\\./|\\.\\.\\\\|~|/etc/|/var/|/tmp/|/home/|/root/)");
    
    /**
     * Creates a security validation rule.
     */
    public SecurityRule() {
        super("SECURITY", ValidationLevel.ERROR, 
            "Validates GraphQL queries for security vulnerabilities");
    }
    
    @Override
    public String getCategory() {
        return "SECURITY";
    }
    
    @Override
    public void validate(ValidationContext context, ValidationResult result) {
        Document document = context.getDocument();
        
        if (document == null) {
            return;
        }
        
        // Generic security validation - works with any GraphQL document
        validateIntrospectionAbuse(context, result);
        validateArgumentValues(context, result);
        validateFieldNames(context, result);
        validateQueryComplexity(context, result);
        validateSuspiciousPatterns(context, result);
    }
    
    /**
     * Validates for potential introspection abuse.
     * This method is generic and works with any introspection field names.
     * 
     * @param context the validation context
     * @param result the validation result
     */
    private void validateIntrospectionAbuse(ValidationContext context, ValidationResult result) {
        if (context.containsIntrospectionQueries()) {
            // Generic introspection validation - works with any introspection fields
            List<Field> introspectionFields = context.findFields(field -> 
                field.getName().startsWith("__"));
            
            for (Field field : introspectionFields) {
                // Check for potentially dangerous introspection queries
                if (field.getName().equals("__schema") || field.getName().equals("__type")) {
                    result.addError(getName(), 
                        "Introspection query detected. Ensure proper access controls are in place.", field);
                }
            }
        }
    }
    
    /**
     * Validates argument values for security issues.
     * This method is generic and works with any argument names.
     * 
     * @param context the validation context
     * @param result the validation result
     */
    private void validateArgumentValues(ValidationContext context, ValidationResult result) {
        // Generic argument validation - works with any argument names
        context.traverseNodes(node -> {
            if (node instanceof Argument) {
                Argument argument = (Argument) node;
                validateArgumentValue(argument, result);
            }
        });
    }
    
    /**
     * Validates a single argument value for security issues.
     * This method is generic and works with any argument names.
     * 
     * @param argument the argument to validate
     * @param result the validation result
     */
    private void validateArgumentValue(Argument argument, ValidationResult result) {
        if (argument.getValue() instanceof StringValue) {
            StringValue stringValue = (StringValue) argument.getValue();
            String value = stringValue.getValue();
            
            // Generic security validation - no hardcoded field/argument names
            validateStringValue(value, argument, result);
        }
    }
    
    /**
     * Validates string values for security patterns.
     * This method is generic and works with any string values.
     * 
     * @param value the string value to validate
     * @param node the node where the value was found
     * @param result the validation result
     */
    private void validateStringValue(String value, graphql.language.Node node, ValidationResult result) {
        if (value == null || value.isEmpty()) {
            return;
        }
        
        // Generic SQL injection detection
        if (SQL_INJECTION_PATTERN.matcher(value).find()) {
            result.addError(getName(), 
                "Potential SQL injection detected in argument value", node);
        }
        
        // Generic XSS detection
        if (XSS_PATTERN.matcher(value).find()) {
            result.addError(getName(), 
                "Potential XSS attack detected in argument value", node);
        }
        
        // Generic path traversal detection
        if (PATH_TRAVERSAL_PATTERN.matcher(value).find()) {
            result.addError(getName(), 
                "Potential path traversal attack detected in argument value", node);
        }
    }
    
    /**
     * Validates field names for security issues.
     * This method is generic and works with any field names.
     * 
     * @param context the validation context
     * @param result the validation result
     */
    private void validateFieldNames(ValidationContext context, ValidationResult result) {
        // Generic field name validation - works with any field names
        context.traverseNodes(node -> {
            if (node instanceof Field) {
                Field field = (Field) node;
                validateFieldName(field, result);
            }
        });
    }
    
    /**
     * Validates a single field name for security issues.
     * This method is generic and works with any field names.
     * 
     * @param field the field to validate
     * @param result the validation result
     */
    private void validateFieldName(Field field, ValidationResult result) {
        String fieldName = field.getName();
        
        if (fieldName == null || fieldName.isEmpty()) {
            return;
        }
        
        // Generic field name validation - no hardcoded field names
        if (fieldName.contains("'") || fieldName.contains("\"") || fieldName.contains(";")) {
            result.addError(getName(), 
                "Field name contains potentially dangerous characters", field);
        }
        
        // Check for suspicious field name patterns
        if (fieldName.toLowerCase().contains("admin") || 
            fieldName.toLowerCase().contains("system") ||
            fieldName.toLowerCase().contains("config")) {
            result.addWarning(getName(), 
                "Field name suggests administrative access. Ensure proper authorization.", field);
        }
    }
    
    /**
     * Validates query complexity for potential DoS attacks.
     * This method is generic and works with any GraphQL structure.
     * 
     * @param context the validation context
     * @param result the validation result
     */
    private void validateQueryComplexity(ValidationContext context, ValidationResult result) {
        int depth = context.calculateMaxDepth();
        int fields = context.calculateFieldCount();
        int arguments = context.calculateArgumentCount();
        
        // Generic complexity validation - works with any query structure
        int complexity = depth * fields * arguments;
        int maxComplexity = 1000; // Configurable threshold
        
        if (complexity > maxComplexity) {
            result.addError(getName(), 
                String.format("Query complexity (%d) exceeds security threshold (%d). Potential DoS attack.", 
                    complexity, maxComplexity));
        }
        
        // Check for extremely deep queries
        if (depth > 20) {
            result.addError(getName(), 
                "Query depth exceeds security limit. Potential DoS attack.");
        }
        
        // Check for extremely large queries
        if (fields > 200) {
            result.addError(getName(), 
                "Query field count exceeds security limit. Potential DoS attack.");
        }
    }
    
    /**
     * Validates for suspicious patterns in the query.
     * This method is generic and works with any GraphQL structure.
     * 
     * @param context the validation context
     * @param result the validation result
     */
    private void validateSuspiciousPatterns(ValidationContext context, ValidationResult result) {
        // Generic pattern validation - works with any query structure
        String queryString = context.getDocument().toString().toLowerCase();
        
        // Check for suspicious patterns
        if (queryString.contains("union") || queryString.contains("select")) {
            result.addWarning(getName(), 
                "Query contains SQL-like keywords. Review for potential injection attempts.");
        }
        
        if (queryString.contains("script") || queryString.contains("javascript")) {
            result.addWarning(getName(), 
                "Query contains script-related keywords. Review for potential XSS attempts.");
        }
        
        if (queryString.contains("admin") || queryString.contains("root")) {
            result.addWarning(getName(), 
                "Query contains administrative keywords. Ensure proper authorization.");
        }
    }
} 