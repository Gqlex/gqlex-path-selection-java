package com.intuit.gqlex.security;

/**
 * Statistics for audit logging.
 * Contains information about logged events and their counts.
 */
public class LogStatistics {
    
    private long totalQueriesLogged;
    private long totalErrorsLogged;
    private long totalWarningsLogged;
    private int recentLogCount;
    
    /**
     * Creates empty log statistics.
     */
    public LogStatistics() {
    }
    
    /**
     * Creates log statistics with all information.
     */
    public LogStatistics(long totalQueriesLogged, long totalErrorsLogged, 
                        long totalWarningsLogged, int recentLogCount) {
        this.totalQueriesLogged = totalQueriesLogged;
        this.totalErrorsLogged = totalErrorsLogged;
        this.totalWarningsLogged = totalWarningsLogged;
        this.recentLogCount = recentLogCount;
    }
    
    // Getters and setters
    
    public long getTotalQueriesLogged() {
        return totalQueriesLogged;
    }
    
    public void setTotalQueriesLogged(long totalQueriesLogged) {
        this.totalQueriesLogged = totalQueriesLogged;
    }
    
    public long getTotalErrorsLogged() {
        return totalErrorsLogged;
    }
    
    public void setTotalErrorsLogged(long totalErrorsLogged) {
        this.totalErrorsLogged = totalErrorsLogged;
    }
    
    public long getTotalWarningsLogged() {
        return totalWarningsLogged;
    }
    
    public void setTotalWarningsLogged(long totalWarningsLogged) {
        this.totalWarningsLogged = totalWarningsLogged;
    }
    
    public int getRecentLogCount() {
        return recentLogCount;
    }
    
    public void setRecentLogCount(int recentLogCount) {
        this.recentLogCount = recentLogCount;
    }
    
    /**
     * Gets the error rate as a percentage.
     * 
     * @return error rate as a percentage
     */
    public double getErrorRate() {
        return totalQueriesLogged > 0 ? (double) totalErrorsLogged / totalQueriesLogged * 100 : 0.0;
    }
    
    /**
     * Gets the warning rate as a percentage.
     * 
     * @return warning rate as a percentage
     */
    public double getWarningRate() {
        return totalQueriesLogged > 0 ? (double) totalWarningsLogged / totalQueriesLogged * 100 : 0.0;
    }
    
    @Override
    public String toString() {
        return String.format("LogStatistics{totalQueries=%d, totalErrors=%d, totalWarnings=%d, recentLogs=%d}",
            totalQueriesLogged, totalErrorsLogged, totalWarningsLogged, recentLogCount);
    }
} 