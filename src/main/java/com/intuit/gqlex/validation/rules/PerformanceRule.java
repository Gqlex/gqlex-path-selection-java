package com.intuit.gqlex.validation.rules;

import com.intuit.gqlex.validation.core.ValidationContext;
import com.intuit.gqlex.validation.core.ValidationResult;
import com.intuit.gqlex.validation.core.ValidationLevel;
import graphql.language.Document;
import graphql.language.Field;

/**
 * Validates GraphQL queries for performance issues.
 * This rule is completely generic and works with any GraphQL document structure.
 */
public class PerformanceRule extends ValidationRule {
    
    private static final int DEFAULT_MAX_DEPTH = 10;
    private static final int DEFAULT_MAX_FIELDS = 100;
    private static final int DEFAULT_MAX_ARGUMENTS = 20;
    
    private final int maxDepth;
    private final int maxFields;
    private final int maxArguments;
    
    /**
     * Creates a performance validation rule with default limits.
     */
    public PerformanceRule() {
        this(DEFAULT_MAX_DEPTH, DEFAULT_MAX_FIELDS, DEFAULT_MAX_ARGUMENTS);
    }
    
    /**
     * Creates a performance validation rule with custom limits.
     * 
     * @param maxDepth maximum allowed query depth
     * @param maxFields maximum allowed number of fields
     * @param maxArguments maximum allowed number of arguments
     */
    public PerformanceRule(int maxDepth, int maxFields, int maxArguments) {
        super("PERFORMANCE", ValidationLevel.WARNING, 
            "Validates GraphQL queries for performance issues");
        this.maxDepth = maxDepth;
        this.maxFields = maxFields;
        this.maxArguments = maxArguments;
    }
    
    @Override
    public String getCategory() {
        return "PERFORMANCE";
    }
    
    @Override
    public void validate(ValidationContext context, ValidationResult result) {
        Document document = context.getDocument();
        
        if (document == null) {
            return;
        }
        
        // Generic performance validation - works with any GraphQL document
        validateQueryDepth(context, result);
        validateFieldCount(context, result);
        validateArgumentCount(context, result);
        validateIntrospectionQueries(context, result);
        validateComplexity(context, result);
    }
    
    /**
     * Validates query depth to prevent overly nested queries.
     * This method is generic and works with any GraphQL structure.
     * 
     * @param context the validation context
     * @param result the validation result
     */
    private void validateQueryDepth(ValidationContext context, ValidationResult result) {
        int actualDepth = context.calculateMaxDepth();
        
        if (actualDepth > maxDepth) {
            result.addWarning(getName(), 
                String.format("Query depth (%d) exceeds recommended limit (%d). Consider reducing nesting.", 
                    actualDepth, maxDepth));
        }
    }
    
    /**
     * Validates field count to prevent overly large queries.
     * This method is generic and works with any field names.
     * 
     * @param context the validation context
     * @param result the validation result
     */
    private void validateFieldCount(ValidationContext context, ValidationResult result) {
        int actualFields = context.calculateFieldCount();
        
        if (actualFields > maxFields) {
            result.addWarning(getName(), 
                String.format("Query contains %d fields, exceeding recommended limit (%d). Consider splitting into smaller queries.", 
                    actualFields, maxFields));
        }
    }
    
    /**
     * Validates argument count to prevent overly complex queries.
     * This method is generic and works with any argument names.
     * 
     * @param context the validation context
     * @param result the validation result
     */
    private void validateArgumentCount(ValidationContext context, ValidationResult result) {
        int actualArguments = context.calculateArgumentCount();
        
        if (actualArguments > maxArguments) {
            result.addWarning(getName(), 
                String.format("Query contains %d arguments, exceeding recommended limit (%d). Consider simplifying query parameters.", 
                    actualArguments, maxArguments));
        }
    }
    
    /**
     * Validates for introspection queries which can be performance intensive.
     * This method is generic and works with any introspection field names.
     * 
     * @param context the validation context
     * @param result the validation result
     */
    private void validateIntrospectionQueries(ValidationContext context, ValidationResult result) {
        if (context.containsIntrospectionQueries()) {
            result.addWarning(getName(), 
                "Query contains introspection fields (starting with '__'). These can be performance intensive and should be used sparingly.");
        }
    }
    
    /**
     * Validates overall query complexity.
     * This method is generic and works with any GraphQL structure.
     * 
     * @param context the validation context
     * @param result the validation result
     */
    private void validateComplexity(ValidationContext context, ValidationResult result) {
        int depth = context.calculateMaxDepth();
        int fields = context.calculateFieldCount();
        int arguments = context.calculateArgumentCount();
        
        // Generic complexity calculation - works with any query structure
        int complexity = depth * fields + arguments;
        int maxComplexity = maxDepth * maxFields + maxArguments;
        
        if (complexity > maxComplexity) {
            result.addWarning(getName(), 
                String.format("Query complexity score (%d) exceeds recommended limit (%d). Consider optimizing query structure.", 
                    complexity, maxComplexity));
        }
        
        // Provide optimization suggestions
        if (depth > maxDepth / 2) {
            result.addInfo(getName(), 
                "Consider using fragments to reduce query depth and improve readability.");
        }
        
        if (fields > maxFields / 2) {
            result.addInfo(getName(), 
                "Consider splitting large queries into smaller, focused queries for better performance.");
        }
    }
    
    /**
     * Gets the maximum allowed query depth.
     * 
     * @return the maximum depth
     */
    public int getMaxDepth() {
        return maxDepth;
    }
    
    /**
     * Gets the maximum allowed number of fields.
     * 
     * @return the maximum field count
     */
    public int getMaxFields() {
        return maxFields;
    }
    
    /**
     * Gets the maximum allowed number of arguments.
     * 
     * @return the maximum argument count
     */
    public int getMaxArguments() {
        return maxArguments;
    }
} 