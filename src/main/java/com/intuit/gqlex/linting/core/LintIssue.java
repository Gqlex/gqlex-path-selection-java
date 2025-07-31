package com.intuit.gqlex.linting.core;

import graphql.language.Node;

/**
 * Represents a single linting issue found during GraphQL document analysis.
 * 
 * <p>This class encapsulates all information about a linting issue including
 * the rule that generated it, the message, severity level, and the specific
 * GraphQL node that caused the issue.</p>
 * 
 * <p>The linting system is completely generic and agnostic to any specific GraphQL schema,
 * working with any query, mutation, or subscription structure.</p>
 * 
 * @author gqlex
 * @version 2.0.1
 * @since 2.0.1
 */
public class LintIssue {
    private final String ruleName;
    private final String message;
    private final LintLevel level;
    private final Node node;
    private final String path;
    private final int line;
    private final int column;

    /**
     * Creates a new linting issue.
     * 
     * @param ruleName the name of the rule that generated this issue
     * @param message the descriptive message about the issue
     * @param level the severity level of the issue
     * @param node the GraphQL node that caused the issue
     */
    public LintIssue(String ruleName, String message, LintLevel level, Node node) {
        this.ruleName = ruleName;
        this.message = message;
        this.level = level;
        this.node = node;
        
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
     * Creates a generic linting issue without a specific node.
     * 
     * @param ruleName the name of the rule that generated this issue
     * @param message the descriptive message about the issue
     * @param level the severity level of the issue
     */
    public LintIssue(String ruleName, String message, LintLevel level) {
        this(ruleName, message, level, null);
    }

    /**
     * Creates a syntax error linting issue.
     * 
     * @param ruleName the name of the rule that generated this issue
     * @param message the descriptive message about the syntax error
     * @return a new LintIssue with ERROR level
     */
    public static LintIssue syntax(String ruleName, String message) {
        return new LintIssue(ruleName, message, LintLevel.ERROR);
    }

    /**
     * Creates a generic linting issue.
     * 
     * @param ruleName the name of the rule that generated this issue
     * @param message the descriptive message about the issue
     * @return a new LintIssue with WARNING level
     */
    public static LintIssue generic(String ruleName, String message) {
        return new LintIssue(ruleName, message, LintLevel.WARNING);
    }

    /**
     * Gets the name of the rule that generated this issue.
     * 
     * @return the rule name
     */
    public String getRuleName() {
        return ruleName;
    }

    /**
     * Gets the descriptive message about this issue.
     * 
     * @return the issue message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the severity level of this issue.
     * 
     * @return the lint level
     */
    public LintLevel getLevel() {
        return level;
    }

    /**
     * Gets the GraphQL node that caused this issue.
     * 
     * @return the GraphQL node, or null if not associated with a specific node
     */
    public Node getNode() {
        return node;
    }

    /**
     * Gets the path to the node that caused this issue.
     * 
     * @return the node path, or null if not available
     */
    public String getPath() {
        return path;
    }

    /**
     * Gets the line number where this issue occurred.
     * 
     * @return the line number, or -1 if not available
     */
    public int getLine() {
        return line;
    }

    /**
     * Gets the column number where this issue occurred.
     * 
     * @return the column number, or -1 if not available
     */
    public int getColumn() {
        return column;
    }

    /**
     * Checks if this issue is an error.
     * 
     * @return true if the level is ERROR
     */
    public boolean isError() {
        return level == LintLevel.ERROR;
    }

    /**
     * Checks if this issue is a warning.
     * 
     * @return true if the level is WARNING
     */
    public boolean isWarning() {
        return level == LintLevel.WARNING;
    }

    /**
     * Checks if this issue is informational.
     * 
     * @return true if the level is INFO
     */
    public boolean isInfo() {
        return level == LintLevel.INFO;
    }

    /**
     * Extracts a path representation from a GraphQL node.
     * 
     * @param node the GraphQL node
     * @return a string representation of the node path
     */
    private String extractPath(Node node) {
        if (node == null) {
            return null;
        }
        
        // Generic path extraction - works with any GraphQL node type
        StringBuilder path = new StringBuilder();
        Node current = node;
        
        while (current != null) {
            if (path.length() > 0) {
                path.insert(0, "/");
            }
            
            String nodeName = getNodeName(current);
            path.insert(0, nodeName);
            
            current = getParentNode(current);
        }
        
        return path.toString();
    }

    /**
     * Gets a name representation for a GraphQL node.
     * 
     * @param node the GraphQL node
     * @return a string name for the node
     */
    private String getNodeName(Node node) {
        if (node == null) {
            return "unknown";
        }
        
        // Generic node naming - works with any GraphQL node type
        String className = node.getClass().getSimpleName();
        
        // Remove "Impl" suffix if present
        if (className.endsWith("Impl")) {
            className = className.substring(0, className.length() - 4);
        }
        
        return className.toLowerCase();
    }

    /**
     * Gets the parent node of a GraphQL node.
     * 
     * @param node the GraphQL node
     * @return the parent node, or null if not available
     */
    private Node getParentNode(Node node) {
        // This is a simplified implementation
        // In a real implementation, you would traverse the AST to find the parent
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(level.name()).append(": ");
        sb.append(ruleName).append(" - ");
        sb.append(message);
        
        if (line > 0) {
            sb.append(" (line ").append(line);
            if (column > 0) {
                sb.append(", column ").append(column);
            }
            sb.append(")");
        }
        
        if (path != null) {
            sb.append(" [").append(path).append("]");
        }
        
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        LintIssue that = (LintIssue) obj;
        
        if (line != that.line) return false;
        if (column != that.column) return false;
        if (!ruleName.equals(that.ruleName)) return false;
        if (!message.equals(that.message)) return false;
        if (level != that.level) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        return node != null ? node.equals(that.node) : that.node == null;
    }

    @Override
    public int hashCode() {
        int result = ruleName.hashCode();
        result = 31 * result + message.hashCode();
        result = 31 * result + level.hashCode();
        result = 31 * result + (node != null ? node.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + line;
        result = 31 * result + column;
        return result;
    }
} 