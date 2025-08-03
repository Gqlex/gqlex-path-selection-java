package com.intuit.gqlex.gqlxpath.lazy;

import com.intuit.gqlex.common.GqlNodeContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the LazyXPathProcessor
 */
public class LazyXPathProcessorTest {
    
    private LazyXPathProcessor processor;
    private Path tempDir;
    
    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        this.processor = new LazyXPathProcessor();
        this.tempDir = tempDir;
    }
    
    @Test
    void testBasicXPathProcessing() throws IOException {
        // Create a test GraphQL document
        String graphqlContent = 
            "query GetUser {\n" +
            "  user(id: \"123\") {\n" +
            "    id\n" +
            "    name\n" +
            "    email\n" +
            "    profile {\n" +
            "      avatar\n" +
            "      bio\n" +
            "    }\n" +
            "  }\n" +
            "}\n";
        
        Path documentPath = tempDir.resolve("test.graphql");
        Files.write(documentPath, graphqlContent.getBytes());
        
        // Test basic xpath processing
        LazyXPathProcessor.LazyXPathResult result = processor.processXPath(
            documentPath.toString(), "//user"
        );
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getResult());
        assertTrue(result.getDuration() > 0);
        assertNotNull(result.getAnalysis());
        assertNotNull(result.getSection());
    }
    
    @Test
    void testFieldXPathProcessing() throws IOException {
        String graphqlContent = 
            "query GetUser {\n" +
            "  user(id: \"123\") {\n" +
            "    id\n" +
            "    name\n" +
            "    email\n" +
            "    profile {\n" +
            "      avatar\n" +
            "      bio\n" +
            "    }\n" +
            "  }\n" +
            "}\n";
        
        Path documentPath = tempDir.resolve("test.graphql");
        Files.write(documentPath, graphqlContent.getBytes());
        
        // Test field xpath
        LazyXPathProcessor.LazyXPathResult result = processor.processXPath(
            documentPath.toString(), "//user/name"
        );
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getResult());
        assertTrue(result.getDuration() > 0);
    }
    
    @Test
    void testFragmentXPathProcessing() throws IOException {
        String graphqlContent = 
            "query GetUser {\n" +
            "  user(id: \"123\") {\n" +
            "    id\n" +
            "    name\n" +
            "    email\n" +
            "    ...UserProfile\n" +
            "  }\n" +
            "}\n" +
            "\n" +
            "fragment UserProfile on User {\n" +
            "  profile {\n" +
            "    avatar\n" +
            "    bio\n" +
            "  }\n" +
            "}\n";
        
        Path documentPath = tempDir.resolve("test.graphql");
        Files.write(documentPath, graphqlContent.getBytes());
        
        // Test fragment xpath
        LazyXPathProcessor.LazyXPathResult result = processor.processXPath(
            documentPath.toString(), "//fragment"
        );
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getResult());
    }
    
    @Test
    void testMultipleXPathProcessing() throws IOException {
        String graphqlContent = 
            "query GetUser {\n" +
            "  user(id: \"123\") {\n" +
            "    id\n" +
            "    name\n" +
            "    email\n" +
            "    profile {\n" +
            "      avatar\n" +
            "      bio\n" +
            "    }\n" +
            "  }\n" +
            "}\n";
        
        Path documentPath = tempDir.resolve("test.graphql");
        Files.write(documentPath, graphqlContent.getBytes());
        
        List<String> xpaths = List.of("//user", "//user/name", "//user/profile");
        
        List<LazyXPathProcessor.LazyXPathResult> results = processor.processMultipleXPaths(
            documentPath.toString(), xpaths
        );
        
        assertEquals(3, results.size());
        
        for (LazyXPathProcessor.LazyXPathResult result : results) {
            assertTrue(result.isSuccess());
            assertNotNull(result.getResult());
            assertTrue(result.getDuration() > 0);
        }
    }
    
    @Test
    void testPerformanceComparison() throws IOException {
        String graphqlContent = 
            "query GetUser {\n" +
            "  user(id: \"123\") {\n" +
            "    id\n" +
            "    name\n" +
            "    email\n" +
            "    profile {\n" +
            "      avatar\n" +
            "      bio\n" +
            "      location {\n" +
            "        city\n" +
            "        country\n" +
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
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}\n";
        
        Path documentPath = tempDir.resolve("test.graphql");
        Files.write(documentPath, graphqlContent.getBytes());
        
        // Compare performance
        LazyXPathProcessor.PerformanceComparison comparison = processor.compareWithTraditional(
            documentPath.toString(), "//user"
        );
        
        assertTrue(comparison.getTraditionalTime() > 0);
        assertTrue(comparison.getLazyTime() > 0);
        assertNotNull(comparison.getTraditionalResult());
        assertNotNull(comparison.getLazyResult());
        
        // Results should match
        assertTrue(comparison.resultsMatch());
        
        // Log performance improvement
        System.out.println("Performance Comparison:");
        System.out.println("Traditional Time: " + comparison.getTraditionalTime() + "ms");
        System.out.println("Lazy Time: " + comparison.getLazyTime() + "ms");
        System.out.println("Improvement: " + comparison.getImprovementPercentage() + "%");
        System.out.println("Lazy is faster: " + comparison.isLazyFaster());
    }
    
    @Test
    void testPerformanceStats() throws IOException {
        String graphqlContent = 
            "query GetUser {\n" +
            "  user(id: \"123\") {\n" +
            "    id\n" +
            "    name\n" +
            "    email\n" +
            "  }\n" +
            "}\n";
        
        Path documentPath = tempDir.resolve("test.graphql");
        Files.write(documentPath, graphqlContent.getBytes());
        
        // Process multiple queries to generate stats
        processor.processXPath(documentPath.toString(), "//user");
        processor.processXPath(documentPath.toString(), "//user/name");
        processor.processXPath(documentPath.toString(), "//user/email");
        
        var stats = processor.getPerformanceStats();
        
        assertNotNull(stats);
        assertTrue((Double) stats.get("averageTime") > 0);
        assertTrue((Double) stats.get("minTime") > 0);
        assertTrue((Double) stats.get("maxTime") > 0);
        assertEquals(3, stats.get("totalQueries"));
        assertNotNull(stats.get("cacheStats"));
        
        System.out.println("Performance Stats: " + stats);
    }
    
    @Test
    void testCacheFunctionality() throws IOException {
        String graphqlContent = 
            "query GetUser {\n" +
            "  user(id: \"123\") {\n" +
            "    id\n" +
            "    name\n" +
            "    email\n" +
            "  }\n" +
            "}\n";
        
        Path documentPath = tempDir.resolve("test.graphql");
        Files.write(documentPath, graphqlContent.getBytes());
        
        // First call - should load from file
        long start1 = System.currentTimeMillis();
        LazyXPathProcessor.LazyXPathResult result1 = processor.processXPath(
            documentPath.toString(), "//user"
        );
        long time1 = System.currentTimeMillis() - start1;
        
        // Second call - should use cache
        long start2 = System.currentTimeMillis();
        LazyXPathProcessor.LazyXPathResult result2 = processor.processXPath(
            documentPath.toString(), "//user"
        );
        long time2 = System.currentTimeMillis() - start2;
        
        assertTrue(result1.isSuccess());
        assertTrue(result2.isSuccess());
        
        // Cached call should be faster (or at least not slower due to cache overhead)
        System.out.println("First call time: " + time1 + "ms");
        System.out.println("Cached call time: " + time2 + "ms");
        
        // Clear cache and test
        processor.clearDocumentCache(documentPath.toString());
        
        long start3 = System.currentTimeMillis();
        LazyXPathProcessor.LazyXPathResult result3 = processor.processXPath(
            documentPath.toString(), "//user"
        );
        long time3 = System.currentTimeMillis() - start3;
        
        assertTrue(result3.isSuccess());
        System.out.println("After cache clear time: " + time3 + "ms");
    }
    
    @Test
    void testErrorHandling() throws IOException {
        // Test with non-existent file
        LazyXPathProcessor.LazyXPathResult result = processor.processXPath(
            "non-existent-file.graphql", "//user"
        );
        
        assertFalse(result.isSuccess());
        assertTrue(result.hasError());
        assertNotNull(result.getError());
        assertTrue(result.getDuration() > 0);
    }
    
    @Test
    void testLargeDocumentPerformance() throws IOException {
        // Create a large GraphQL document
        StringBuilder largeContent = new StringBuilder();
        largeContent.append("query LargeQuery {\n");
        
        for (int i = 0; i < 100; i++) {
            largeContent.append("  user").append(i).append("(id: \"").append(i).append("\") {\n");
            largeContent.append("    id\n");
            largeContent.append("    name\n");
            largeContent.append("    email\n");
            largeContent.append("    profile {\n");
            largeContent.append("      avatar\n");
            largeContent.append("      bio\n");
            largeContent.append("    }\n");
            largeContent.append("  }\n");
        }
        largeContent.append("}\n");
        
        Path documentPath = tempDir.resolve("large.graphql");
        Files.write(documentPath, largeContent.toString().getBytes());
        
        // Test performance with large document
        LazyXPathProcessor.PerformanceComparison comparison = processor.compareWithTraditional(
            documentPath.toString(), "//user0"
        );
        
        assertTrue(comparison.getTraditionalTime() > 0);
        assertTrue(comparison.getLazyTime() > 0);
        assertTrue(comparison.resultsMatch());
        
        System.out.println("Large Document Performance:");
        System.out.println("Traditional Time: " + comparison.getTraditionalTime() + "ms");
        System.out.println("Lazy Time: " + comparison.getLazyTime() + "ms");
        System.out.println("Improvement: " + comparison.getImprovementPercentage() + "%");
    }
    
    @Test
    void testXPathAnalysis() throws IOException {
        String graphqlContent = 
            "query GetUser {\n" +
            "  user(id: \"123\") {\n" +
            "    id\n" +
            "    name\n" +
            "    email\n" +
            "  }\n" +
            "}\n";
        
        Path documentPath = tempDir.resolve("test.graphql");
        Files.write(documentPath, graphqlContent.getBytes());
        
        LazyXPathProcessor.LazyXPathResult result = processor.processXPath(
            documentPath.toString(), "//user/name"
        );
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getAnalysis());
        
        XPathAnalysis analysis = result.getAnalysis();
        assertTrue(analysis.requiresFieldResolution());
        assertTrue(analysis.getDepth() > 0);
        assertFalse(analysis.getRequiredSections().isEmpty());
        
        System.out.println("XPath Analysis: " + analysis);
    }
} 