package com.intuit.gqlex.gqlxpath.lazy;

import com.intuit.gqlex.common.GqlNodeContext;
import com.intuit.gqlex.gqlxpath.selector.SelectorFacade;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Quick performance demonstration of lazy loading vs traditional gqlXPath processing.
 * 
 * This class provides a simple way to see the performance benefits of lazy loading
 * without the complexity of full test infrastructure.
 * 
 * @author gqlex
 * @version 2.0.1
 * @since 2.0.1
 */
public class QuickPerformanceDemo {
    
    public static void main(String[] args) {
        QuickPerformanceDemo demo = new QuickPerformanceDemo();
        demo.runPerformanceDemo();
    }
    
    public void runPerformanceDemo() {
        System.out.println("🚀 gqlXPath Lazy Loading Performance Demo");
        System.out.println("==========================================");
        
        // Test 1: Simple Query
        testSimpleQuery();
        
        // Test 2: Complex Query
        testComplexQuery();
        
        // Test 3: Large Document
        testLargeDocument();
        
        System.out.println("\n✅ Performance Demo Complete!");
    }
    
    private void testSimpleQuery() {
        System.out.println("\n📊 Test 1: Simple Query Performance");
        System.out.println("-----------------------------------");
        
        String query = 
            "query GetUser {\n" +
            "  user(id: \"123\") {\n" +
            "    id\n" +
            "    name\n" +
            "    email\n" +
            "  }\n" +
            "}\n";
        
        String xpath = "//user";
        
        comparePerformance(query, xpath, "simple_query");
    }
    
    private void testComplexQuery() {
        System.out.println("\n📊 Test 2: Complex Query Performance");
        System.out.println("-------------------------------------");
        
        String query = 
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
        
        String xpath = "//user//posts//comments";
        
        comparePerformance(query, xpath, "complex_query");
    }
    
    private void testLargeDocument() {
        System.out.println("\n📊 Test 3: Large Document Performance");
        System.out.println("--------------------------------------");
        
        // Create a large document
        StringBuilder largeQuery = new StringBuilder();
        largeQuery.append("query LargeQuery {\n");
        
        // Add multiple root level queries
        for (int i = 1; i <= 5; i++) {
            largeQuery.append("  user").append(i).append("(id: \"").append(i).append("\") {\n");
            largeQuery.append("    id\n");
            largeQuery.append("    name\n");
            largeQuery.append("    email\n");
            largeQuery.append("    profile {\n");
            largeQuery.append("      bio\n");
            largeQuery.append("      avatar\n");
            largeQuery.append("    }\n");
            largeQuery.append("    posts {\n");
            
            // Add multiple posts per user
            for (int j = 1; j <= 3; j++) {
                largeQuery.append("      post").append(j).append(" {\n");
                largeQuery.append("        id\n");
                largeQuery.append("        title\n");
                largeQuery.append("        content\n");
                largeQuery.append("        comments {\n");
                
                // Add multiple comments per post
                for (int k = 1; k <= 2; k++) {
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
        
        comparePerformance(largeQuery.toString(), xpath, "large_document");
    }
    
    private void comparePerformance(String query, String xpath, String testName) {
        LazyXPathProcessor lazyProcessor = new LazyXPathProcessor();
        SelectorFacade traditionalProcessor = new SelectorFacade();
        
        // Save query to temporary file
        String documentId = testName + "_" + System.currentTimeMillis();
        try {
            Files.write(Paths.get(documentId), query.getBytes());
        } catch (IOException e) {
            System.err.println("Failed to write test file: " + e.getMessage());
            return;
        }
        
        try {
            // Measure traditional processing
            long traditionalStart = System.nanoTime();
            List<GqlNodeContext> traditionalResult = traditionalProcessor.selectMany(query, xpath);
            long traditionalEnd = System.nanoTime();
            long traditionalTime = TimeUnit.NANOSECONDS.toMillis(traditionalEnd - traditionalStart);
            
            // Measure lazy processing
            long lazyStart = System.nanoTime();
            LazyXPathProcessor.LazyXPathResult lazyResult = lazyProcessor.processXPath(documentId, xpath);
            long lazyEnd = System.nanoTime();
            long lazyTime = TimeUnit.NANOSECONDS.toMillis(lazyEnd - lazyStart);
            
            // Calculate performance metrics
            double speedupFactor = (double) traditionalTime / lazyTime;
            double improvementPercentage = ((double) (traditionalTime - lazyTime) / traditionalTime) * 100;
            
            // Display results
            System.out.println("Document Size: ~" + query.length() + " characters");
            System.out.println("XPath: " + xpath);
            System.out.println("Traditional Time: " + traditionalTime + "ms");
            System.out.println("Lazy Time: " + lazyTime + "ms");
            System.out.println("Speedup: " + String.format("%.2fx", speedupFactor));
            System.out.println("Improvement: " + String.format("%.1f%%", improvementPercentage));
            System.out.println("Traditional Results: " + traditionalResult.size());
            System.out.println("Lazy Results: " + (lazyResult.isSuccess() ? lazyResult.getResult().size() : "ERROR"));
            System.out.println("Results Match: " + (lazyResult.isSuccess() && 
                traditionalResult.size() == lazyResult.getResult().size()));
            
            // Performance assessment
            if (speedupFactor > 1.5) {
                System.out.println("🎉 EXCELLENT: Significant performance improvement!");
            } else if (speedupFactor > 1.1) {
                System.out.println("✅ GOOD: Noticeable performance improvement");
            } else if (speedupFactor > 1.0) {
                System.out.println("👍 DECENT: Slight performance improvement");
            } else {
                System.out.println("⚠️  NEEDS OPTIMIZATION: Traditional method is faster");
            }
            
        } catch (Exception e) {
            System.err.println("Error during performance test: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Clean up
            try {
                Files.deleteIfExists(Paths.get(documentId));
            } catch (IOException e) {
                // Ignore cleanup errors
            }
        }
    }
} 