package com.intuit.gqlex.transformation.optimization;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Generic object pooling system for reducing garbage collection pressure.
 * Works with any Java type, not just GraphQL-specific ones.
 */
public class ObjectPool<T> {
    
    private final Queue<T> pool = new ConcurrentLinkedQueue<>();
    private final Supplier<T> factory;
    private final Consumer<T> resetter; // Generic reset function
    
    // Maximum pool size to prevent memory leaks
    private static final int MAX_POOL_SIZE = 100;
    
    /**
     * Create an object pool with factory and reset functions.
     * Generic: Works with any Java type, not field-specific logic
     */
    public ObjectPool(Supplier<T> factory, Consumer<T> resetter) {
        this.factory = factory;
        this.resetter = resetter;
    }
    
    /**
     * Create an object pool with factory only (no reset function).
     */
    public ObjectPool(Supplier<T> factory) {
        this(factory, obj -> {
            // Default reset: do nothing
        });
    }
    
    /**
     * Borrow an object from the pool.
     * Generic: Uses generic reset function, not field-specific logic
     */
    public T borrow() {
        T obj = pool.poll();
        if (obj != null) {
            resetter.accept(obj); // Generic reset - no field-specific logic
            return obj;
        }
        return factory.get();
    }
    
    /**
     * Release an object back to the pool.
     */
    public void release(T obj) {
        if (obj != null && pool.size() < MAX_POOL_SIZE) {
            pool.offer(obj);
        }
    }
    
    /**
     * Get the current pool size.
     */
    public int size() {
        return pool.size();
    }
    
    /**
     * Clear the pool. Useful for testing or memory management.
     */
    public void clear() {
        pool.clear();
    }
    
    /**
     * Check if the pool is empty.
     */
    public boolean isEmpty() {
        return pool.isEmpty();
    }
    
    /**
     * Pre-populate the pool with objects.
     */
    public void prePopulate(int count) {
        for (int i = 0; i < count && i < MAX_POOL_SIZE; i++) {
            pool.offer(factory.get());
        }
    }
    
    /**
     * Get pool statistics for monitoring.
     */
    public PoolStats getStats() {
        return new PoolStats(pool.size(), MAX_POOL_SIZE);
    }
    
    /**
     * Pool statistics for monitoring performance.
     */
    public static class PoolStats {
        private final int currentSize;
        private final int maxSize;
        
        public PoolStats(int currentSize, int maxSize) {
            this.currentSize = currentSize;
            this.maxSize = maxSize;
        }
        
        public int getCurrentSize() {
            return currentSize;
        }
        
        public int getMaxSize() {
            return maxSize;
        }
        
        public double getUtilizationRate() {
            return maxSize > 0 ? (double) currentSize / maxSize : 0.0;
        }
        
        @Override
        public String toString() {
            return String.format("ObjectPool{current=%d, max=%d, utilization=%.2f%%}", 
                currentSize, maxSize, getUtilizationRate() * 100);
        }
    }
} 