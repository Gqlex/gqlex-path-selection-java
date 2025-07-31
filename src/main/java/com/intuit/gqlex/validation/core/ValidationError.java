package com.intuit.gqlex.validation.core;

import graphql.language.Node;

/**
 * Represents a single validation error or issue.
 * This class is completely generic and works with any GraphQL validation scenario.
 */
public class ValidationError {
    private final String ruleName;
    private final String message;
    private final ValidationLevel level;
    private final Node node;
    private final String path;
    private final int line;
    private final int column;

    /**
     * Creates a validation error with all details.
     * 
     * @param ruleName the name of the validation rule that generated this error
     * @param message the error message (generic, no hardcoded field names)
     * @param level the severity level of the error
     * @param node the GraphQL node where the error occurred (can be null)
     */
    public ValidationError(String ruleName, String message, ValidationLevel level, Node node) {
        this.ruleName = ruleName;
        this.message = message;
        this.level = level;
        this.node = node;
        
        // Extract location information from the node if available
        if (node != null) {
            this.path = extractPath(node);
            this.line = node.getSourceLocation() != null ? node.getSourceLocation().getLine() : -1;
            this.column = node.getSourceLocation() != null ? node.getSourceLocation().getColumn() : -1;
        } else {
            this.path = null;
            this.line = -1;
            this.column = -1;
        }
    }

    /**
     * Creates a validation error with basic information.
     * 
     * @param ruleName the name of the validation rule
     * @param message the error message
     * @param level the severity level
     */
    public ValidationError(String ruleName, String message, ValidationLevel level) {
        this(ruleName, message, level, null);
    }

    /**
     * Creates a generic validation error for system-level issues.
     * 
     * @param ruleName the name of the validation rule
     * @param message the error message
     * @return a validation error with ERROR level
     */
    public static ValidationError generic(String ruleName, String message) {
        return new ValidationError(ruleName, message, ValidationLevel.ERROR);
    }

    /**
     * Creates a syntax validation error.
     * 
     * @param ruleName the name of the validation rule
     * @param message the error message
     * @return a validation error with ERROR level
     */
    public static ValidationError syntax(String ruleName, String message) {
        return new ValidationError(ruleName, message, ValidationLevel.ERROR);
    }

    /**
     * Creates a security validation error.
     * 
     * @param ruleName the name of the validation rule
     * @param message the error message
     * @param node the node where the security issue occurred
     * @return a validation error with ERROR level
     */
    public static ValidationError security(String ruleName, String message, Node node) {
        return new ValidationError(ruleName, message, ValidationLevel.ERROR, node);
    }

    /**
     * Creates a performance validation warning.
     * 
     * @param ruleName the name of the validation rule
     * @param message the warning message
     * @param node the node where the performance issue occurred
     * @return a validation error with WARNING level
     */
    public static ValidationError performance(String ruleName, String message, Node node) {
        return new ValidationError(ruleName, message, ValidationLevel.WARNING, node);
    }

    /**
     * Creates an informational validation message.
     * 
     * @param ruleName the name of the validation rule
     * @param message the informational message
     * @param node the node where the suggestion applies
     * @return a validation error with INFO level
     */
    public static ValidationError info(String ruleName, String message, Node node) {
        return new ValidationError(ruleName, message, ValidationLevel.INFO, node);
    }

    /**
     * Extracts a generic path representation from a GraphQL node.
     * This method is generic and works with any node type.
     * 
     * @param node the GraphQL node
     * @return a string representation of the node's path
     */
    private String extractPath(Node node) {
        if (node == null) {
            return null;
        }
        
        // Generic path extraction - works with any node type
        StringBuilder path = new StringBuilder();
        
        // Add node type for context
        path.append(node.getClass().getSimpleName());
        
        // Add source location if available
        if (node.getSourceLocation() != null) {
            path.append(" at line ").append(node.getSourceLocation().getLine());
        }
        
        return path.toString();
    }

    // Getters
    public String getRuleName() { return ruleName; }
    public String getMessage() { return message; }
    public ValidationLevel getLevel() { return level; }
    public Node getNode() { return node; }
    public String getPath() { return path; }
    public int getLine() { return line; }
    public int getColumn() { return column; }

    /**
     * Returns a formatted string representation of the validation error.
     * The format is generic and works with any validation scenario.
     * 
     * @return formatted error string
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(level).append("] ");
        sb.append(ruleName).append(": ");
        sb.append(message);
        
        if (path != null) {
            sb.append(" (").append(path).append(")");
        }
        
        if (line > 0) {
            sb.append(" at line ").append(line);
            if (column > 0) {
                sb.append(":").append(column);
            }
        }
        
        return sb.toString();
    }

    /**
     * Checks if this error is at ERROR level.
     * 
     * @return true if this is an error
     */
    public boolean isError() {
        return level == ValidationLevel.ERROR;
    }

    /**
     * Checks if this error is at WARNING level.
     * 
     * @return true if this is a warning
     */
    public boolean isWarning() {
        return level == ValidationLevel.WARNING;
    }

    /**
     * Checks if this error is at INFO level.
     * 
     * @return true if this is an info message
     */
    public boolean isInfo() {
        return level == ValidationLevel.INFO;
    }
} 