package com.intuit.gqlex.linting.core;

/**
 * Represents the severity level of a linting issue.
 * 
 * <p>This enum defines the different levels of linting issues that can be reported
 * by the GraphQL linting system. The levels are ordered from least severe to most severe.</p>
 * 
 * <p>The linting system is completely generic and agnostic to any specific GraphQL schema,
 * working with any query, mutation, or subscription structure.</p>
 * 
 * @author gqlex
 * @version 2.0.1
 * @since 2.0.1
 */
public enum LintLevel {
    /**
     * Informational issues that provide helpful suggestions but don't indicate problems.
     * These are typically style suggestions or optimization hints.
     */
    INFO,
    
    /**
     * Warning issues that indicate potential problems or violations of best practices.
     * These should be addressed but don't prevent the query from working.
     */
    WARNING,
    
    /**
     * Error issues that indicate actual problems or violations of rules.
     * These should be fixed as they may cause issues or indicate poor practices.
     */
    ERROR;
    
    /**
     * Checks if this level is more severe than the given level.
     * 
     * @param other the level to compare against
     * @return true if this level is more severe than the other level
     */
    public boolean isMoreSevereThan(LintLevel other) {
        return this.ordinal() > other.ordinal();
    }
    
    /**
     * Checks if this level is less severe than the given level.
     * 
     * @param other the level to compare against
     * @return true if this level is less severe than the other level
     */
    public boolean isLessSevereThan(LintLevel other) {
        return this.ordinal() < other.ordinal();
    }
    
    /**
     * Checks if this level is at least as severe as the given level.
     * 
     * @param other the level to compare against
     * @return true if this level is at least as severe as the other level
     */
    public boolean isAtLeastAsSevereAs(LintLevel other) {
        return this.ordinal() >= other.ordinal();
    }
} 