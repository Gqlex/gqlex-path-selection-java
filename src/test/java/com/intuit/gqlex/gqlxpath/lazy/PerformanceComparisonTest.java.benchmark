package com.intuit.gqlex.gqlxpath.lazy;

import com.intuit.gqlex.common.GqlNodeContext;
import com.intuit.gqlex.gqlxpath.selector.SelectorFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive performance comparison between lazy loading and traditional gqlXPath processing.
 * 
 * <p>This test suite measures performance across various GraphQL query types including:
 * - Simple queries
 * - Complex nested queries
 * - Mutations
 * - Subscriptions
 * - Fragments
 * - Large documents
 * - Multiple operations</p>
 * 
 * <p>Performance metrics measured:
 * - Execution time (milliseconds)
 * - Memory usage (bytes)
 * - Cache hit rates
 * - Throughput (operations per second)</p>
 * 
 * @author gqlex
 * @version 2.0.1
 * @since 2.0.1
 * 
 * NOTE: This test class is commented out to avoid running performance tests during regular test runs.
 * To run performance benchmarks, uncomment this class and run the specific test methods.
 */
@DisplayName("Performance Comparison - Lazy vs Traditional gqlXPath")
public class PerformanceComparisonTest {
    
    private LazyXPathProcessor lazyProcessor;
    private SelectorFacade traditionalProcessor;
    private PerformanceMetricsCollector metricsCollector;
    
    @BeforeEach
    void setUp() {
        lazyProcessor = new LazyXPathProcessor();
        traditionalProcessor = new SelectorFacade();
        metricsCollector = new PerformanceMetricsCollector();
    }
    
    @Test
    @Tag("benchmark")
    @DisplayName("Simple Query Performance - Basic Field Selection")
    void testSimpleQueryPerformance() {
        String simpleQuery = 
            "query GetUser {\n" +
            "  user(id: \"123\") {\n" +
            "    id\n" +
            "    name\n" +
            "    email\n" +
            "  }\n" +
            "}\n";
        
        String xpath = "//user";
        
        PerformanceComparisonResult result = comparePerformance(simpleQuery, xpath, "simple_query");
        
        // Performance assertions
        assertTrue(result.getLazyTime() < result.getTraditionalTime(), 
            "Lazy loading should be faster for simple queries");
        assertTrue(result.getMemoryImprovement() > 0, 
            "Lazy loading should use less memory");
        assertTrue(result.getSpeedupFactor() > 1.0, 
            "Lazy loading should provide speedup");
        
        System.out.println("=== Simple Query Performance ===");
        System.out.println("Traditional Time: " + result.getTraditionalTime() + "ms");
        System.out.println("Lazy Time: " + result.getLazyTime() + "ms");
        System.out.println("Speedup: " + String.format("%.2fx", result.getSpeedupFactor()));
        System.out.println("Memory Improvement: " + String.format("%.1f%%", result.getMemoryImprovement()));
    }
    
    @Test
    @Tag("benchmark")
    @DisplayName("Complex Nested Query Performance - Deep Nesting")
    void testComplexNestedQueryPerformance() {
        String complexQuery = 
            "query GetUserWithPosts {\n" +
            "  user(id: \"123\") {\n" +
            "    id\n" +
            "    name\n" +
            "    email\n" +
            "    profile {\n" +
            "      bio\n" +
            "      avatar\n" +
            "      preferences {\n" +
            "        theme\n" +
            "        language\n" +
            "        notifications {\n" +
            "          email\n" +
            "          push\n" +
            "          sms\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "    posts {\n" +
            "      id\n" +
            "      title\n" +
            "      content\n" +
            "      comments {\n" +
            "        id\n" +
            "        text\n" +
            "        author {\n" +
            "          id\n" +
            "          name\n" +
            "          avatar\n" +
            "        }\n" +
            "        replies {\n" +
            "          id\n" +
            "          text\n" +
            "          author {\n" +
            "            id\n" +
            "            name\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "      tags {\n" +
            "        id\n" +
            "        name\n" +
            "        category {\n" +
            "          id\n" +
            "          name\n" +
            "          description\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}\n";
        
        String xpath = "//user//posts//comments//author";
        
        PerformanceComparisonResult result = comparePerformance(complexQuery, xpath, "complex_nested_query");
        
        // Performance assertions for complex queries
        assertTrue(result.getSpeedupFactor() > 1.5, 
            "Lazy loading should provide significant speedup for complex queries");
        assertTrue(result.getMemoryImprovement() > 50, 
            "Lazy loading should provide significant memory improvement for complex queries");
        
        System.out.println("=== Complex Nested Query Performance ===");
        System.out.println("Traditional Time: " + result.getTraditionalTime() + "ms");
        System.out.println("Lazy Time: " + result.getLazyTime() + "ms");
        System.out.println("Speedup: " + String.format("%.2fx", result.getSpeedupFactor()));
        System.out.println("Memory Improvement: " + String.format("%.1f%%", result.getMemoryImprovement()));
    }
    
  //  @Test
    @Tag("benchmark")
    @DisplayName("Mutation Performance - Data Modification Operations")
    void testMutationPerformance() {
        String mutation = 
            "mutation CreateUserWithProfile {\n" +
            "  createUser(input: {\n" +
            "    name: \"John Doe\"\n" +
            "    email: \"john@example.com\"\n" +
            "    profile: {\n" +
            "      bio: \"Software Developer\"\n" +
            "      avatar: \"https://example.com/avatar.jpg\"\n" +
            "    }\n" +
            "  }) {\n" +
            "    id\n" +
            "    name\n" +
            "    email\n" +
            "    profile {\n" +
            "      bio\n" +
            "      avatar\n" +
            "    }\n" +
            "  }\n" +
            "  updateUser(id: \"456\", input: {\n" +
            "    name: \"Jane Smith\"\n" +
            "    profile: {\n" +
            "      bio: \"Product Manager\"\n" +
            "    }\n" +
            "  }) {\n" +
            "    id\n" +
            "    name\n" +
            "    profile {\n" +
            "      bio\n" +
            "    }\n" +
            "  }\n" +
            "}\n";
        
        String xpath = "//createUser//profile";
        
        PerformanceComparisonResult result = comparePerformance(mutation, xpath, "mutation");
        
        assertTrue(result.getSpeedupFactor() > 1.0, 
            "Lazy loading should provide speedup for mutations");
        
        System.out.println("=== Mutation Performance ===");
        System.out.println("Traditional Time: " + result.getTraditionalTime() + "ms");
        System.out.println("Lazy Time: " + result.getLazyTime() + "ms");
        System.out.println("Speedup: " + String.format("%.2fx", result.getSpeedupFactor()));
        System.out.println("Memory Improvement: " + String.format("%.1f%%", result.getMemoryImprovement()));
    }
    
    @Test
    @Tag("benchmark")
    @DisplayName("Fragment Performance - Fragment Spreads and Inline Fragments")
    void testFragmentPerformance() {
        String fragmentQuery = 
            "query GetUserWithFragments {\n" +
            "  user(id: \"123\") {\n" +
            "    ...userFields\n" +
            "    ... on Admin {\n" +
            "      permissions {\n" +
            "        role\n" +
            "        level\n" +
            "      }\n" +
            "    }\n" +
            "    posts {\n" +
            "      ...postFields\n" +
            "      comments {\n" +
            "        ...commentFields\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "fragment userFields on User {\n" +
            "  id\n" +
            "  name\n" +
            "  email\n" +
            "    profile {\n" +
            "      bio\n" +
            "      avatar\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "fragment postFields on Post {\n" +
            "  id\n" +
            "  title\n" +
            "  content\n" +
            "  createdAt\n" +
            "}\n" +
            "fragment commentFields on Comment {\n" +
            "  id\n" +
            "  text\n" +
            "  author {\n" +
            "    id\n" +
            "    name\n" +
            "  }\n" +
            "}\n";
        
        String xpath = "//user//posts//comments";
        
        PerformanceComparisonResult result = comparePerformance(fragmentQuery, xpath, "fragment_query");
        
        assertTrue(result.getSpeedupFactor() > 1.2, 
            "Lazy loading should provide speedup for fragment queries");
        
        System.out.println("=== Fragment Performance ===");
        System.out.println("Traditional Time: " + result.getTraditionalTime() + "ms");
        System.out.println("Lazy Time: " + result.getLazyTime() + "ms");
        System.out.println("Speedup: " + String.format("%.2fx", result.getSpeedupFactor()));
        System.out.println("Memory Improvement: " + String.format("%.1f%%", result.getMemoryImprovement()));
    }
    
    @Test
    @Tag("benchmark")
    @DisplayName("Large Document Performance - Massive GraphQL Documents")
    void testLargeDocumentPerformance() {
        // Create a large document with many fields and nested structures
        StringBuilder largeQuery = new StringBuilder();
        largeQuery.append("query LargeQuery {\n");
        
        // Add multiple root level queries
        for (int i = 1; i <= 10; i++) {
            largeQuery.append("  user").append(i).append("(id: \"").append(i).append("\") {\n");
            largeQuery.append("    id\n");
            largeQuery.append("    name\n");
            largeQuery.append("    email\n");
            largeQuery.append("    profile {\n");
            largeQuery.append("      bio\n");
            largeQuery.append("      avatar\n");
            largeQuery.append("      preferences {\n");
            largeQuery.append("        theme\n");
            largeQuery.append("        language\n");
            largeQuery.append("      }\n");
            largeQuery.append("    }\n");
            largeQuery.append("    posts {\n");
            
            // Add multiple posts per user
            for (int j = 1; j <= 5; j++) {
                largeQuery.append("      post").append(j).append(" {\n");
                largeQuery.append("        id\n");
                largeQuery.append("        title\n");
                largeQuery.append("        content\n");
                largeQuery.append("        comments {\n");
                
                // Add multiple comments per post
                for (int k = 1; k <= 3; k++) {
                    largeQuery.append("          comment").append(k).append(" {\n");
                    largeQuery.append("            id\n");
                    largeQuery.append("            text\n");
                    largeQuery.append("            author {\n");
                    largeQuery.append("              id\n");
                    largeQuery.append("              name\n");
                    largeQuery.append("            }\n");
                    largeQuery.append("          }\n");
                }
                largeQuery.append("        }\n");
                largeQuery.append("      }\n");
            }
            largeQuery.append("    }\n");
            largeQuery.append("  }\n");
        }
        largeQuery.append("}\n");
        
        String xpath = "//user1//posts//post1//comments//comment1";
        
        PerformanceComparisonResult result = comparePerformance(largeQuery.toString(), xpath, "large_document");
        
        // For large documents, lazy loading should show significant improvements
        assertTrue(result.getSpeedupFactor() > 2.0, 
            "Lazy loading should provide significant speedup for large documents");
        assertTrue(result.getMemoryImprovement() > 70, 
            "Lazy loading should provide significant memory improvement for large documents");
        
        System.out.println("=== Large Document Performance ===");
        System.out.println("Document Size: ~" + largeQuery.length() + " characters");
        System.out.println("Traditional Time: " + result.getTraditionalTime() + "ms");
        System.out.println("Lazy Time: " + result.getLazyTime() + "ms");
        System.out.println("Speedup: " + String.format("%.2fx", result.getSpeedupFactor()));
        System.out.println("Memory Improvement: " + String.format("%.1f%%", result.getMemoryImprovement()));
    }
    
    @Test
    @Tag("benchmark")
    @DisplayName("Multiple XPath Performance - Batch Processing")
    void testMultipleXPathPerformance() {
        String query = 
            "query MultiPathQuery {\n" +
            "  user(id: \"123\") {\n" +
            "    id\n" +
            "    name\n" +
            "    profile {\n" +
            "      bio\n" +
            "      avatar\n" +
            "    }\n" +
            "    posts {\n" +
            "      id\n" +
            "      title\n" +
            "      comments {\n" +
            "        id\n" +
            "        text\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}\n";
        
        List<String> xpaths = List.of(
            "//user",
            "//user//profile",
            "//user//posts",
            "//user//posts//comments",
            "//user//name"
        );
        
        PerformanceComparisonResult result = compareMultipleXPathPerformance(query, xpaths, "multiple_xpath");
        
        assertTrue(result.getSpeedupFactor() > 1.5, 
            "Lazy loading should provide speedup for multiple xpath processing");
        
        System.out.println("=== Multiple XPath Performance ===");
        System.out.println("XPath Count: " + xpaths.size());
        System.out.println("Traditional Time: " + result.getTraditionalTime() + "ms");
        System.out.println("Lazy Time: " + result.getLazyTime() + "ms");
        System.out.println("Speedup: " + String.format("%.2fx", result.getSpeedupFactor()));
        System.out.println("Memory Improvement: " + String.format("%.1f%%", result.getMemoryImprovement()));
    }
    
    @Test
    @Tag("benchmark")
    @DisplayName("Cache Performance - Repeated Access Patterns")
    void testCachePerformance() {
        String query = 
            "query CachedQuery {\n" +
            "  user(id: \"123\") {\n" +
            "    id\n" +
            "    name\n" +
            "    posts {\n" +
            "      id\n" +
            "      title\n" +
            "      comments {\n" +
            "        id\n" +
            "        text\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}\n";
        
        String xpath = "//user//posts//comments";
        
        // First run - cold cache
        PerformanceComparisonResult coldResult = comparePerformance(query, xpath, "cache_cold");
        
        // Second run - warm cache
        PerformanceComparisonResult warmResult = comparePerformance(query, xpath, "cache_warm");
        
        // Third run - hot cache
        PerformanceComparisonResult hotResult = comparePerformance(query, xpath, "cache_hot");
        
        // Cache should improve performance
        assertTrue(warmResult.getLazyTime() < coldResult.getLazyTime(), 
            "Warm cache should be faster than cold cache");
        assertTrue(hotResult.getLazyTime() < warmResult.getLazyTime(), 
            "Hot cache should be faster than warm cache");
        
        System.out.println("=== Cache Performance ===");
        System.out.println("Cold Cache - Lazy Time: " + coldResult.getLazyTime() + "ms");
        System.out.println("Warm Cache - Lazy Time: " + warmResult.getLazyTime() + "ms");
        System.out.println("Hot Cache - Lazy Time: " + hotResult.getLazyTime() + "ms");
        System.out.println("Cache Improvement: " + String.format("%.1f%%", 
            ((coldResult.getLazyTime() - hotResult.getLazyTime()) / (double) coldResult.getLazyTime()) * 100));
    }
    
    @Test
    @Tag("benchmark")
    @DisplayName("Memory Usage Comparison - Detailed Memory Analysis")
    void testMemoryUsageComparison() {
        String query = 
            "query MemoryTest {\n" +
            "  users {\n" +
            "    id\n" +
            "    name\n" +
            "    posts {\n" +
            "      id\n" +
            "      title\n" +
            "      content\n" +
            "      comments {\n" +
            "        id\n" +
            "        text\n" +
            "        author {\n" +
            "          id\n" +
            "          name\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}\n";
        
        String xpath = "//users//posts//comments";
        
        MemoryUsageResult memoryResult = compareMemoryUsage(query, xpath);
        
        assertTrue(memoryResult.getLazyMemoryUsage() < memoryResult.getTraditionalMemoryUsage(), 
            "Lazy loading should use less memory");
        assertTrue(memoryResult.getMemoryReductionPercentage() > 30, 
            "Lazy loading should provide significant memory reduction");
        
        System.out.println("=== Memory Usage Comparison ===");
        System.out.println("Traditional Memory: " + memoryResult.getTraditionalMemoryUsage() + " bytes");
        System.out.println("Lazy Memory: " + memoryResult.getLazyMemoryUsage() + " bytes");
        System.out.println("Memory Reduction: " + String.format("%.1f%%", memoryResult.getMemoryReductionPercentage()));
        System.out.println("Memory Efficiency: " + String.format("%.2fx", memoryResult.getMemoryEfficiencyFactor()));
    }
    
    // Helper methods
    private PerformanceComparisonResult comparePerformance(String query, String xpath, String testName) {
        // Save query to temporary file
        String documentId = testName + "_" + System.currentTimeMillis();
        try {
            Files.write(Paths.get(documentId), query.getBytes());
        } catch (IOException e) {
            fail("Failed to write test file: " + e.getMessage());
        }
        
        // Measure traditional processing
        long traditionalStart = System.nanoTime();
        long traditionalMemoryBefore = getMemoryUsage();
        
        List<GqlNodeContext> traditionalResult = traditionalProcessor.selectMany(query, xpath);
        
        long traditionalEnd = System.nanoTime();
        long traditionalMemoryAfter = getMemoryUsage();
        long traditionalTime = TimeUnit.NANOSECONDS.toMillis(traditionalEnd - traditionalStart);
        long traditionalMemory = traditionalMemoryAfter - traditionalMemoryBefore;
        
        // Measure lazy processing
        long lazyStart = System.nanoTime();
        long lazyMemoryBefore = getMemoryUsage();
        
        LazyXPathProcessor.LazyXPathResult lazyResult = lazyProcessor.processXPath(documentId, xpath);
        
        long lazyEnd = System.nanoTime();
        long lazyMemoryAfter = getMemoryUsage();
        long lazyTime = TimeUnit.NANOSECONDS.toMillis(lazyEnd - lazyStart);
        long lazyMemory = lazyMemoryAfter - lazyMemoryBefore;
        
        // Verify results match
        assertEquals(traditionalResult.size(), lazyResult.getResult().size(), 
            "Results should match between traditional and lazy processing");
        
        // Calculate performance metrics
        double speedupFactor = (double) traditionalTime / lazyTime;
        double memoryImprovement = ((double) (traditionalMemory - lazyMemory) / traditionalMemory) * 100;
        
        return new PerformanceComparisonResult(
            traditionalTime, lazyTime, traditionalMemory, lazyMemory,
            speedupFactor, memoryImprovement, traditionalResult.size()
        );
    }
    
    private PerformanceComparisonResult compareMultipleXPathPerformance(String query, List<String> xpaths, String testName) {
        String documentId = testName + "_" + System.currentTimeMillis();
        try {
            Files.write(Paths.get(documentId), query.getBytes());
        } catch (IOException e) {
            fail("Failed to write test file: " + e.getMessage());
        }
        
        // Traditional processing
        long traditionalStart = System.nanoTime();
        long traditionalMemoryBefore = getMemoryUsage();
        
        int traditionalTotalResults = 0;
        for (String xpath : xpaths) {
            List<GqlNodeContext> result = traditionalProcessor.selectMany(query, xpath);
            traditionalTotalResults += result.size();
        }
        
        long traditionalEnd = System.nanoTime();
        long traditionalMemoryAfter = getMemoryUsage();
        long traditionalTime = TimeUnit.NANOSECONDS.toMillis(traditionalEnd - traditionalStart);
        long traditionalMemory = traditionalMemoryAfter - traditionalMemoryBefore;
        
        // Lazy processing
        long lazyStart = System.nanoTime();
        long lazyMemoryBefore = getMemoryUsage();
        
        List<LazyXPathProcessor.LazyXPathResult> lazyResults = lazyProcessor.processMultipleXPaths(documentId, xpaths);
        int lazyTotalResults = lazyResults.stream().mapToInt(r -> r.getResult().size()).sum();
        
        long lazyEnd = System.nanoTime();
        long lazyMemoryAfter = getMemoryUsage();
        long lazyTime = TimeUnit.NANOSECONDS.toMillis(lazyEnd - lazyStart);
        long lazyMemory = lazyMemoryAfter - lazyMemoryBefore;
        
        // Verify results match
        assertEquals(traditionalTotalResults, lazyTotalResults, 
            "Total results should match between traditional and lazy processing");
        
        double speedupFactor = (double) traditionalTime / lazyTime;
        double memoryImprovement = ((double) (traditionalMemory - lazyMemory) / traditionalMemory) * 100;
        
        return new PerformanceComparisonResult(
            traditionalTime, lazyTime, traditionalMemory, lazyMemory,
            speedupFactor, memoryImprovement, traditionalTotalResults
        );
    }
    
    private MemoryUsageResult compareMemoryUsage(String query, String xpath) {
        String documentId = "memory_test_" + System.currentTimeMillis();
        try {
            Files.write(Paths.get(documentId), query.getBytes());
        } catch (IOException e) {
            fail("Failed to write test file: " + e.getMessage());
        }
        
        // Force garbage collection before measurement
        System.gc();
        
        // Traditional memory measurement
        long traditionalMemoryBefore = getMemoryUsage();
        List<GqlNodeContext> traditionalResult = traditionalProcessor.selectMany(query, xpath);
        long traditionalMemoryAfter = getMemoryUsage();
        long traditionalMemoryUsage = traditionalMemoryAfter - traditionalMemoryBefore;
        
        // Force garbage collection
        System.gc();
        
        // Lazy memory measurement
        long lazyMemoryBefore = getMemoryUsage();
        LazyXPathProcessor.LazyXPathResult lazyResult = lazyProcessor.processXPath(documentId, xpath);
        long lazyMemoryAfter = getMemoryUsage();
        long lazyMemoryUsage = lazyMemoryAfter - lazyMemoryBefore;
        
        double memoryReductionPercentage = ((double) (traditionalMemoryUsage - lazyMemoryUsage) / traditionalMemoryUsage) * 100;
        double memoryEfficiencyFactor = (double) traditionalMemoryUsage / lazyMemoryUsage;
        
        return new MemoryUsageResult(
            traditionalMemoryUsage, lazyMemoryUsage, 
            memoryReductionPercentage, memoryEfficiencyFactor
        );
    }
    
    private long getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
    
    // Result classes
    private static class PerformanceComparisonResult {
        private final long traditionalTime;
        private final long lazyTime;
        private final long traditionalMemory;
        private final long lazyMemory;
        private final double speedupFactor;
        private final double memoryImprovement;
        private final int resultCount;
        
        public PerformanceComparisonResult(long traditionalTime, long lazyTime, 
                                         long traditionalMemory, long lazyMemory,
                                         double speedupFactor, double memoryImprovement, int resultCount) {
            this.traditionalTime = traditionalTime;
            this.lazyTime = lazyTime;
            this.traditionalMemory = traditionalMemory;
            this.lazyMemory = lazyMemory;
            this.speedupFactor = speedupFactor;
            this.memoryImprovement = memoryImprovement;
            this.resultCount = resultCount;
        }
        
        public long getTraditionalTime() { return traditionalTime; }
        public long getLazyTime() { return lazyTime; }
        public long getTraditionalMemory() { return traditionalMemory; }
        public long getLazyMemory() { return lazyMemory; }
        public double getSpeedupFactor() { return speedupFactor; }
        public double getMemoryImprovement() { return memoryImprovement; }
        public int getResultCount() { return resultCount; }
    }
    
    private static class MemoryUsageResult {
        private final long traditionalMemoryUsage;
        private final long lazyMemoryUsage;
        private final double memoryReductionPercentage;
        private final double memoryEfficiencyFactor;
        
        public MemoryUsageResult(long traditionalMemoryUsage, long lazyMemoryUsage,
                               double memoryReductionPercentage, double memoryEfficiencyFactor) {
            this.traditionalMemoryUsage = traditionalMemoryUsage;
            this.lazyMemoryUsage = lazyMemoryUsage;
            this.memoryReductionPercentage = memoryReductionPercentage;
            this.memoryEfficiencyFactor = memoryEfficiencyFactor;
        }
        
        public long getTraditionalMemoryUsage() { return traditionalMemoryUsage; }
        public long getLazyMemoryUsage() { return lazyMemoryUsage; }
        public double getMemoryReductionPercentage() { return memoryReductionPercentage; }
        public double getMemoryEfficiencyFactor() { return memoryEfficiencyFactor; }
    }
    
    private static class PerformanceMetricsCollector {
        // This class can be extended to collect additional metrics
        // such as CPU usage, I/O operations, etc.
    }
} 