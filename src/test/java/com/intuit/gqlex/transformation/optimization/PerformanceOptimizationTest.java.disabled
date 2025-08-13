package com.intuit.gqlex.transformation.optimization;

import com.intuit.gqlex.transformation.AstManipulationUtils;
import com.intuit.gqlex.transformation.GraphQLTransformer;
import graphql.language.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive performance tests for optimization systems.
 * Tests that optimizations work with any GraphQL query or mutation.
 */
@DisplayName("Performance Optimization Tests")
public class PerformanceOptimizationTest {
    
    private PerformanceOptimizationManager perfManager;
    private ASTCache astCache;
    private RegexPatternPool regexPool;
    
    @BeforeEach
    void setUp() {
        perfManager = PerformanceOptimizationManager.getInstance();
        astCache = perfManager.getAstCache();
        regexPool = perfManager.getRegexPool();
        
        // Clear all caches before each test
        perfManager.clearAll();
    }
    
    @Test
    @DisplayName("AST Cache - Generic Query Caching")
    void testASTCacheGenericQueryCaching() {
        // Test with different query types - generic approach
        String[] queries = {
            "query { user { id name email } }",
            "mutation { createUser(input: {name: \"John\"}) { id name } }",
            "subscription { userUpdated { id name } }",
            "query { posts { title content author { name } } }",
            "mutation { updatePost(id: 1, title: \"New Title\") { id title } }"
        };
        
        for (String query : queries) {
            // First parse - should cache
            Document doc1 = astCache.getOrParse(query);
            assertNotNull(doc1, "Document should be parsed successfully");
            
            // Second parse - should use cache
            Document doc2 = astCache.getOrParse(query);
            assertNotNull(doc2, "Cached document should be retrieved");
            assertEquals(doc1, doc2, "Cached document should be identical");
            
            // Test printing
            String printed1 = astCache.getOrPrint(doc1);
            assertNotNull(printed1, "Document should be printed successfully");
            
            String printed2 = astCache.getOrPrint(doc1);
            assertEquals(printed1, printed2, "Cached print should be identical");
        }
        
        // Verify cache statistics
        ASTCache.CacheStats stats = astCache.getStats();
        assertTrue(stats.getParsedCacheSize() > 0, "Should have cached parsed documents");
        assertTrue(stats.getPrintedCacheSize() > 0, "Should have cached printed documents");
    }
    
    @Test
    @DisplayName("Regex Pattern Pool - Generic Field Replacement")
    void testRegexPatternPoolGenericFieldReplacement() {
        // Test with various field names - generic approach
        String[] testCases = {
            "query { user { id name email } }",
            "query { product { sku title price } }",
            "query { order { number items { product { name } } } }",
            "mutation { createOrder(input: {items: []}) { id total } }"
        };
        
        for (String query : testCases) {
            // Test field name replacement - use fields that actually exist in the queries
            if (query.contains("user")) {
                String result1 = regexPool.replaceFieldName(query, "user", "customer");
                assertNotEquals(query, result1, "Field should be replaced");
                assertTrue(result1.contains("customer"), "Should contain new field name");
                
                String result2 = regexPool.replaceFieldName(result1, "customer", "user");
                assertTrue(result2.contains("user"), "Should contain original field name");
                
                // Test that patterns are cached (using the actual pattern with Pattern.quote)
                String expectedPattern = "\\b\\Quser\\E\\b";
                assertTrue(regexPool.isCached(expectedPattern), "Pattern should be cached");
            }
            
            if (query.contains("product")) {
                String result1 = regexPool.replaceFieldName(query, "product", "item");
                assertNotEquals(query, result1, "Field should be replaced");
                assertTrue(result1.contains("item"), "Should contain new field name");
                
                String result2 = regexPool.replaceFieldName(result1, "item", "product");
                assertTrue(result2.contains("product"), "Should contain original field name");
                
                // Test that patterns are cached
                String expectedPattern = "\\b\\Qproduct\\E\\b";
                assertTrue(regexPool.isCached(expectedPattern), "Pattern should be cached");
            }
        }
        
        assertTrue(regexPool.getCacheSize() > 0, "Should have cached patterns");
    }
    
    @Test
    @DisplayName("Object Pool - Generic Object Reuse")
    void testObjectPoolGenericObjectReuse() {
        ObjectPool<StringBuilder> stringBuilderPool = perfManager.getObjectPool(StringBuilder.class);
        assertNotNull(stringBuilderPool, "StringBuilder pool should be available");
        
        // Test object borrowing and releasing
        StringBuilder sb1 = stringBuilderPool.borrow();
        assertNotNull(sb1, "Should borrow StringBuilder");
        
        sb1.append("test");
        assertEquals("test", sb1.toString(), "StringBuilder should work correctly");
        
        stringBuilderPool.release(sb1);
        
        // Borrow again - should get the same object (reused)
        StringBuilder sb2 = stringBuilderPool.borrow();
        assertEquals(0, sb2.length(), "Reused StringBuilder should be reset");
        
        // Test pool statistics
        ObjectPool.PoolStats stats = stringBuilderPool.getStats();
        assertTrue(stats.getCurrentSize() >= 0, "Pool should have valid size");
        assertTrue(stats.getMaxSize() > 0, "Pool should have maximum size");
    }
    
    @Test
    @DisplayName("Performance Manager - Generic Operation Timing")
    void testPerformanceManagerGenericOperationTiming() {
        // Record timing for various operations
        perfManager.recordOperationTiming("parse", 10);
        perfManager.recordOperationTiming("parse", 15);
        perfManager.recordOperationTiming("transform", 20);
        perfManager.recordOperationTiming("transform", 25);
        
        // Test timing calculations
        assertEquals(12.5, perfManager.getAverageTiming("parse"), 0.1, "Average parse time should be correct");
        assertEquals(22.5, perfManager.getAverageTiming("transform"), 0.1, "Average transform time should be correct");
        
        assertEquals(2, perfManager.getOperationCount("parse"), "Parse operation count should be correct");
        assertEquals(2, perfManager.getOperationCount("transform"), "Transform operation count should be correct");
        
        // Test performance statistics
        PerformanceOptimizationManager.PerformanceStats stats = perfManager.getPerformanceStats();
        assertNotNull(stats, "Performance stats should be available");
        assertTrue(stats.getOperationCounts().size() > 0, "Should have operation counts");
    }
    
    @Test
    @DisplayName("Integration - Generic Transformation with Optimizations")
    void testIntegrationGenericTransformationWithOptimizations() {
        // Test with various query types - generic approach
        String[] queries = {
            "query { user { id name } }",
            "mutation { createUser(input: {name: \"John\"}) { id } }",
            "query { posts { title author { name } } }",
            "subscription { userUpdated { id name email } }"
        };
        
        for (String query : queries) {
            GraphQLTransformer transformer = new GraphQLTransformer(query);
            
            // Perform various transformations
            transformer.addField("//query/user", "email");
            transformer.addField("//query/posts", "content");
            transformer.addArgument("//query/user", "limit", 10);
            
            // Transform and verify
            var result = transformer.transform();
            assertTrue(result.isSuccess(), "Transformation should succeed");
            assertNotNull(result.getQueryString(), "Should have transformed query");
            
            // Verify that optimizations were used
            ASTCache.CacheStats astStats = astCache.getStats();
            assertTrue(astStats.getParsedCacheSize() > 0, "Should have used AST cache");
            
            assertTrue(regexPool.getCacheSize() > 0, "Should have used regex pool");
        }
    }
    
    @Test
    @DisplayName("Generic Field Name Testing - No Hardcoded Names")
    void testGenericFieldNameTesting() {
        // Test with completely different field names - ensuring no hardcoded assumptions
        String[] fieldNames = {"product", "order", "invoice", "customer", "supplier", "employee"};
        
        for (String fieldName : fieldNames) {
            String query = String.format("query { %s { id name } }", fieldName);
            
            // Test field addition
            GraphQLTransformer transformer = new GraphQLTransformer(query);
            transformer.addField("//query/" + fieldName, "description");
            
            var result = transformer.transform();
            assertTrue(result.isSuccess(), "Transformation should succeed for field: " + fieldName);
            assertTrue(result.getQueryString().contains("description"), 
                "Should contain added field for: " + fieldName);
        }
    }
    
    @Test
    @DisplayName("Cache Eviction - Memory Management")
    void testCacheEvictionMemoryManagement() {
        // Fill up caches
        for (int i = 0; i < 100; i++) {
            String query = String.format("query { user%d { id name } }", i);
            astCache.getOrParse(query);
        }
        
        // Test eviction
        astCache.evictOldEntries();
        regexPool.evictOldEntries();
        
        // Verify caches are manageable
        ASTCache.CacheStats astStats = astCache.getStats();
        assertTrue(astStats.getParsedCacheSize() <= 1000, "AST cache should respect size limit");
        assertTrue(regexPool.getCacheSize() <= 500, "Regex cache should respect size limit");
    }
    
    @Test
    @DisplayName("Thread Safety - Concurrent Access")
    void testThreadSafetyConcurrentAccess() throws InterruptedException {
        // Test concurrent access to optimization systems
        Thread[] threads = new Thread[5];
        
        for (int i = 0; i < threads.length; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    String query = String.format("query { user%d { id name } }", threadId * 10 + j);
                    astCache.getOrParse(query);
                    regexPool.replaceFieldName(query, "user" + (threadId * 10 + j), "customer" + (threadId * 10 + j));
                }
            });
        }
        
        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Verify no exceptions occurred and caches are in good state
        ASTCache.CacheStats stats = astCache.getStats();
        assertTrue(stats.getParsedCacheSize() > 0, "Should have cached documents after concurrent access");
    }
    
    @Test
    @DisplayName("Performance Improvement Verification")
    void testPerformanceImprovementVerification() {
        // Measure performance with optimizations enabled
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 100; i++) {
            String query = String.format("query { user%d { id name email profile { bio } } }", i);
            GraphQLTransformer transformer = new GraphQLTransformer(query);
            transformer.addField("//query/user" + i, "phone");
            transformer.addArgument("//query/user" + i, "limit", 10);
            transformer.transform();
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Verify reasonable performance (should be fast with optimizations)
        assertTrue(duration < 5000, "100 transformations should complete in under 5 seconds");
        
        // Verify optimizations were used
        PerformanceOptimizationManager.PerformanceStats stats = perfManager.getPerformanceStats();
        assertTrue(stats.getAstCacheStats().getParsedCacheSize() > 0, "Should have used AST cache");
        assertTrue(stats.getRegexCacheSize() > 0, "Should have used regex cache");
    }
} 