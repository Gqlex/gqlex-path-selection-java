package com.intuit.gqlex.validation.core;

/**
 * Represents the severity level of a validation issue.
 * This enum is generic and works with any GraphQL validation scenario.
 */
public enum ValidationLevel {
    /**
     * Error level - indicates a critical issue that prevents the query from executing.
     * Generic examples: missing required fields, invalid syntax, security violations.
     */
    ERROR,
    
    /**
     * Warning level - indicates a potential issue that should be addressed.
     * Generic examples: deprecated fields, performance concerns, best practice violations.
     */
    WARNING,
    
    /**
     * Info level - indicates informational messages and suggestions.
     * Generic examples: optimization suggestions, style recommendations.
     */
    INFO
} 