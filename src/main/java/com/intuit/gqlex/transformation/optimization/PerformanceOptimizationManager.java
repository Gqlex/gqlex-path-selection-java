package com.intuit.gqlex.transformation.optimization;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Performance optimization manager that coordinates all optimization systems.
 * Provides a unified interface for accessing optimized operations.
 */
public class PerformanceOptimizationManager {
    
    private static final PerformanceOptimizationManager INSTANCE = new PerformanceOptimizationManager();
    
    // Optimization systems
    private final ASTCache astCache;
    private final RegexPatternPool regexPool;
    private final Map<Class<?>, ObjectPool<?>> objectPools;
    
    // Performance monitoring
    private final Map<String, Long> operationTimings = new ConcurrentHashMap<>();
    private final Map<String, Integer> operationCounts = new ConcurrentHashMap<>();
    
    private PerformanceOptimizationManager() {
        this.astCache = ASTCache.getInstance();
        this.regexPool = RegexPatternPool.getInstance();
        this.objectPools = new ConcurrentHashMap<>();
        
        // Initialize common object pools
        initializeObjectPools();
    }
    
    public static PerformanceOptimizationManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * Initialize common object pools for GraphQL operations.
     * Generic: Works with any data types, not field-specific ones
     */
    private void initializeObjectPools() {
        // StringBuilder pool for string operations
        objectPools.put(StringBuilder.class, 
            new ObjectPool<StringBuilder>(StringBuilder::new, sb -> sb.setLength(0)));
        
        // ArrayList pool for node collections
        objectPools.put(java.util.ArrayList.class, 
            new ObjectPool<java.util.ArrayList>(java.util.ArrayList::new, List::clear));
        
        // HashMap pool for variable collections
        objectPools.put(java.util.HashMap.class, 
            new ObjectPool<java.util.HashMap>(java.util.HashMap::new, Map::clear));
    }
    
    /**
     * Get the AST cache instance.
     */
    public ASTCache getAstCache() {
        return astCache;
    }
    
    /**
     * Get the regex pattern pool instance.
     */
    public RegexPatternPool getRegexPool() {
        return regexPool;
    }
    
    /**
     * Get an object pool for a specific type.
     * Generic: Works with any Java type
     */
    @SuppressWarnings("unchecked")
    public <T> ObjectPool<T> getObjectPool(Class<T> type) {
        return (ObjectPool<T>) objectPools.get(type);
    }
    
    /**
     * Create a new object pool for a specific type.
     * Generic: Works with any Java type and reset function
     */
    public <T> ObjectPool<T> createObjectPool(Class<T> type, java.util.function.Supplier<T> factory, java.util.function.Consumer<T> resetter) {
        ObjectPool<T> pool = new ObjectPool<>(factory, resetter);
        objectPools.put(type, pool);
        return pool;
    }
    
    /**
     * Record operation timing for performance monitoring.
     * Generic: Works with any operation type
     */
    public void recordOperationTiming(String operationName, long durationMs) {
        operationTimings.merge(operationName, durationMs, Long::sum);
        operationCounts.merge(operationName, 1, Integer::sum);
    }
    
    /**
     * Get average timing for an operation.
     */
    public double getAverageTiming(String operationName) {
        Long totalTime = operationTimings.get(operationName);
        Integer count = operationCounts.get(operationName);
        
        if (totalTime != null && count != null && count > 0) {
            return (double) totalTime / count;
        }
        return 0.0;
    }
    
    /**
     * Get operation count.
     */
    public int getOperationCount(String operationName) {
        return operationCounts.getOrDefault(operationName, 0);
    }
    
    /**
     * Get performance statistics.
     */
    public PerformanceStats getPerformanceStats() {
        return new PerformanceStats(
            astCache.getStats(),
            regexPool.getCacheSize(),
            objectPools.size(),
            new ConcurrentHashMap<>(operationTimings),
            new ConcurrentHashMap<>(operationCounts)
        );
    }
    
    /**
     * Clear all caches and pools. Useful for testing or memory management.
     */
    public void clearAll() {
        astCache.clearAll();
        regexPool.clearAll();
        objectPools.values().forEach(ObjectPool::clear);
        operationTimings.clear();
        operationCounts.clear();
    }
    
    /**
     * Evict old entries from all caches and pools.
     */
    public void evictOldEntries() {
        astCache.evictOldEntries();
        regexPool.evictOldEntries();
        // Object pools don't need eviction as they have size limits
    }
    
    /**
     * Check if optimizations are enabled.
     */
    public boolean isOptimizationsEnabled() {
        return true; // Always enabled for now, could be made configurable
    }
    
    /**
     * Performance statistics for monitoring.
     */
    public static class PerformanceStats {
        private final ASTCache.CacheStats astCacheStats;
        private final int regexCacheSize;
        private final int objectPoolCount;
        private final Map<String, Long> operationTimings;
        private final Map<String, Integer> operationCounts;
        
        public PerformanceStats(ASTCache.CacheStats astCacheStats, int regexCacheSize, 
                              int objectPoolCount, Map<String, Long> operationTimings, 
                              Map<String, Integer> operationCounts) {
            this.astCacheStats = astCacheStats;
            this.regexCacheSize = regexCacheSize;
            this.objectPoolCount = objectPoolCount;
            this.operationTimings = operationTimings;
            this.operationCounts = operationCounts;
        }
        
        public ASTCache.CacheStats getAstCacheStats() {
            return astCacheStats;
        }
        
        public int getRegexCacheSize() {
            return regexCacheSize;
        }
        
        public int getObjectPoolCount() {
            return objectPoolCount;
        }
        
        public Map<String, Long> getOperationTimings() {
            return operationTimings;
        }
        
        public Map<String, Integer> getOperationCounts() {
            return operationCounts;
        }
        
        @Override
        public String toString() {
            return String.format("PerformanceStats{astCache=%s, regexCache=%d, objectPools=%d, operations=%d}", 
                astCacheStats, regexCacheSize, objectPoolCount, operationCounts.size());
        }
    }
} 