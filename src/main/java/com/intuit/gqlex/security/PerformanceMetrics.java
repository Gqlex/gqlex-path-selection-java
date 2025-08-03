package com.intuit.gqlex.security;

/**
 * Performance metrics for GraphQL operations.
 * Contains information about query complexity, execution time, and resource usage.
 */
public class PerformanceMetrics {
    
    private int depth;
    private int fieldCount;
    private int argumentCount;
    private int complexity;
    private long executionTimeMs;
    private long memoryUsageBytes;
    private int cacheHits;
    private int cacheMisses;
    
    /**
     * Creates performance metrics with basic information.
     */
    public PerformanceMetrics() {
    }
    
    /**
     * Creates performance metrics with all information.
     */
    public PerformanceMetrics(int depth, int fieldCount, int argumentCount, int complexity,
                             long executionTimeMs, long memoryUsageBytes, int cacheHits, int cacheMisses) {
        this.depth = depth;
        this.fieldCount = fieldCount;
        this.argumentCount = argumentCount;
        this.complexity = complexity;
        this.executionTimeMs = executionTimeMs;
        this.memoryUsageBytes = memoryUsageBytes;
        this.cacheHits = cacheHits;
        this.cacheMisses = cacheMisses;
    }
    
    // Getters and setters
    
    public int getDepth() {
        return depth;
    }
    
    public void setDepth(int depth) {
        this.depth = depth;
    }
    
    public int getFieldCount() {
        return fieldCount;
    }
    
    public void setFieldCount(int fieldCount) {
        this.fieldCount = fieldCount;
    }
    
    public int getArgumentCount() {
        return argumentCount;
    }
    
    public void setArgumentCount(int argumentCount) {
        this.argumentCount = argumentCount;
    }
    
    public int getComplexity() {
        return complexity;
    }
    
    public void setComplexity(int complexity) {
        this.complexity = complexity;
    }
    
    public long getExecutionTimeMs() {
        return executionTimeMs;
    }
    
    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }
    
    public long getMemoryUsageBytes() {
        return memoryUsageBytes;
    }
    
    public void setMemoryUsageBytes(long memoryUsageBytes) {
        this.memoryUsageBytes = memoryUsageBytes;
    }
    
    public int getCacheHits() {
        return cacheHits;
    }
    
    public void setCacheHits(int cacheHits) {
        this.cacheHits = cacheHits;
    }
    
    public int getCacheMisses() {
        return cacheMisses;
    }
    
    public void setCacheMisses(int cacheMisses) {
        this.cacheMisses = cacheMisses;
    }
    
    /**
     * Gets the cache hit ratio.
     * 
     * @return cache hit ratio as a percentage
     */
    public double getCacheHitRatio() {
        int totalRequests = cacheHits + cacheMisses;
        return totalRequests > 0 ? (double) cacheHits / totalRequests * 100 : 0.0;
    }
    
    /**
     * Gets the memory usage in MB.
     * 
     * @return memory usage in MB
     */
    public double getMemoryUsageMB() {
        return memoryUsageBytes / (1024.0 * 1024.0);
    }
    
    /**
     * Gets the execution time in seconds.
     * 
     * @return execution time in seconds
     */
    public double getExecutionTimeSeconds() {
        return executionTimeMs / 1000.0;
    }
    
    @Override
    public String toString() {
        return String.format("PerformanceMetrics{depth=%d, fieldCount=%d, complexity=%d, executionTimeMs=%d}",
            depth, fieldCount, complexity, executionTimeMs);
    }
} 