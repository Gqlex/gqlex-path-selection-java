package com.intuit.gqlex.transformation.optimization;

import com.intuit.gqlex.transformation.GraphQLTransformer;
import com.intuit.gqlex.transformation.optimization.ASTCache;
import com.intuit.gqlex.transformation.optimization.RegexPatternPool;
import com.intuit.gqlex.transformation.optimization.PerformanceOptimizationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test to verify that all optimizations are truly generic
 * and work with any GraphQL query or mutation without hardcoding field names.
 */
@DisplayName("Generic Optimization Verification Tests")
public class GenericOptimizationTest {
    
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
    @DisplayName("Generic Field Names - No Hardcoded Assumptions")
    void testGenericFieldNamesNoHardcodedAssumptions() {
        // Test with completely different field names to ensure no hardcoded assumptions
        String[] fieldNames = {
            "product", "order", "invoice", "customer", "supplier", "employee",
            "vehicle", "building", "document", "transaction", "account", "payment",
            "item", "service", "contract", "policy", "claim", "report"
        };
        
        for (String fieldName : fieldNames) {
            String query = String.format("query { %s { id name description } }", fieldName);
            
            // Test AST caching
            var doc1 = astCache.getOrParse(query);
            assertNotNull(doc1, "Should parse query with field: " + fieldName);
            
            var doc2 = astCache.getOrParse(query);
            assertEquals(doc1, doc2, "Should use cached document for field: " + fieldName);
            
            // Test regex pattern pooling
            String modifiedQuery = regexPool.replaceFieldName(query, fieldName, fieldName + "Modified");
            assertTrue(modifiedQuery.contains(fieldName + "Modified"), 
                "Should replace field: " + fieldName);
            
            // Test transformation
            GraphQLTransformer transformer = new GraphQLTransformer(query);
            transformer.addField("//query/" + fieldName, "status");
            
            var result = transformer.transform();
            assertTrue(result.isSuccess(), "Transformation should succeed for field: " + fieldName);
            assertTrue(result.getQueryString().contains("status"), 
                "Should contain added field for: " + fieldName);
        }
    }
    
    @Test
    @DisplayName("Generic Query Types - All GraphQL Operations")
    void testGenericQueryTypesAllGraphQLOperations() {
        // Test with different GraphQL operation types
        String[] queries = {
            // Queries
            "query { user { id name } }",
            "query GetUser { user { id name } }",
            "query($id: ID!) { user(id: $id) { id name } }",
            
            // Mutations
            "mutation { createUser(input: {name: \"John\"}) { id name } }",
            "mutation CreateUser($input: CreateUserInput!) { createUser(input: $input) { id name } }",
            "mutation { updateUser(id: 1, input: {name: \"Jane\"}) { id name } }",
            
            // Subscriptions
            "subscription { userUpdated { id name } }",
            "subscription UserUpdates { userUpdated { id name } }",
            "subscription($userId: ID!) { userUpdated(userId: $userId) { id name } }"
        };
        
        for (String query : queries) {
            // Test AST caching
            var doc1 = astCache.getOrParse(query);
            assertNotNull(doc1, "Should parse query: " + query.substring(0, Math.min(50, query.length())));
            
            var doc2 = astCache.getOrParse(query);
            assertEquals(doc1, doc2, "Should use cached document");
            
            // Test transformation
            GraphQLTransformer transformer = new GraphQLTransformer(query);
            transformer.addField("//query/user", "email");
            transformer.addField("//mutation/createUser", "email");
            transformer.addField("//subscription/userUpdated", "email");
            
            var result = transformer.transform();
            assertTrue(result.isSuccess(), "Transformation should succeed");
        }
    }
    
    @Test
    @DisplayName("Generic Complex Structures - Nested Fields")
    void testGenericComplexStructuresNestedFields() {
        // Test with complex nested structures
        String[] complexQueries = {
            "query { company { departments { teams { members { projects { tasks { subtasks { assignee { profile { contact { email } } } } } } } } } } }",
            "mutation { createOrder(input: {items: [{product: {id: 1}, quantity: 2}]}) { id total items { product { name } quantity } } }",
            "subscription { orderStatusChanged { order { id status items { product { name } quantity } customer { name email } } } }"
        };
        
        for (String query : complexQueries) {
            // Test AST caching with complex queries
            var doc1 = astCache.getOrParse(query);
            assertNotNull(doc1, "Should parse complex query");
            
            var doc2 = astCache.getOrParse(query);
            assertEquals(doc1, doc2, "Should use cached complex document");
            
            // Test regex pattern pooling with complex structures - only replace if field exists
            if (query.contains("company")) {
                String modifiedQuery = regexPool.replaceFieldName(query, "company", "organization");
                assertNotEquals(query, modifiedQuery, "Should modify complex query");
            }
            
            // Test transformation with complex paths - only add fields to existing paths
            GraphQLTransformer transformer = new GraphQLTransformer(query);
            if (query.contains("company")) {
                transformer.addField("//query/company/departments/teams/members", "role");
            }
            if (query.contains("createOrder")) {
                transformer.addField("//mutation/createOrder", "status");
            }
            if (query.contains("orderStatusChanged")) {
                transformer.addField("//subscription/orderStatusChanged/order", "timestamp");
            }
            
            var result = transformer.transform();
            assertTrue(result.isSuccess(), "Complex transformation should succeed");
        }
    }
    
    @Test
    @DisplayName("Generic Arguments and Directives")
    void testGenericArgumentsAndDirectives() {
        // Test with various arguments and directives
        String[] queriesWithArgs = {
            "query { user(id: 1, includeProfile: true) { id name @include(if: $includeProfile) profile { bio } } }",
            "mutation { updateUser(id: 1, input: {name: \"John\", active: true}) { id name @skip(if: $skipName) } }",
            "subscription { userUpdated(userId: $userId, includeDetails: true) { id name @include(if: $includeDetails) details { lastLogin } } }"
        };
        
        for (String query : queriesWithArgs) {
            // Test AST caching
            var doc1 = astCache.getOrParse(query);
            assertNotNull(doc1, "Should parse query with arguments");
            
            // Test regex pattern pooling with arguments - only if the field and argument exist
            if (query.contains("user") && query.contains("id:")) {
                String modifiedQuery = regexPool.replaceArgument(query, "user", "id", "2");
                assertNotEquals(query, modifiedQuery, "Should modify argument");
            }
            
            // Test transformation with arguments
            GraphQLTransformer transformer = new GraphQLTransformer(query);
            transformer.addArgument("//query/user", "limit", 10);
            transformer.addArgument("//mutation/updateUser", "version", "v2");
            transformer.addArgument("//subscription/userUpdated", "timeout", 30);
            
            var result = transformer.transform();
            assertTrue(result.isSuccess(), "Transformation with arguments should succeed");
        }
    }
    
    @Test
    @DisplayName("Generic Fragment Operations")
    void testGenericFragmentOperations() {
        // Test with fragments
        String queryWithFragments = 
            "query {\n" +
            "  user {\n" +
            "    id\n" +
            "    name\n" +
            "    ...UserProfile\n" +
            "    ...UserDetails\n" +
            "  }\n" +
            "}\n" +
            "\n" +
            "fragment UserProfile on User {\n" +
            "  profile {\n" +
            "    bio\n" +
            "    avatar\n" +
            "  }\n" +
            "}\n" +
            "\n" +
            "fragment UserDetails on User {\n" +
            "  email\n" +
            "  phone\n" +
            "}";
        
        // Test AST caching with fragments
        var doc1 = astCache.getOrParse(queryWithFragments);
        assertNotNull(doc1, "Should parse query with fragments");
        
        var doc2 = astCache.getOrParse(queryWithFragments);
        assertEquals(doc1, doc2, "Should use cached document with fragments");
        
        // Test transformation with fragments
        GraphQLTransformer transformer = new GraphQLTransformer(queryWithFragments);
        transformer.addField("//query/user", "status");
        transformer.addField("//fragment/UserProfile/profile", "location");
        transformer.addField("//fragment/UserDetails", "address");
        
        var result = transformer.transform();
        assertTrue(result.isSuccess(), "Transformation with fragments should succeed");
    }
    
    @Test
    @DisplayName("Generic Performance Verification")
    void testGenericPerformanceVerification() {
        // Test performance with various query types
        String[] testQueries = {
            "query { simple { id } }",
            "query { complex { nested { deeply { nested { structure { with { many { levels { of { nesting } } } } } } } } } }",
            "mutation { createComplex(input: {data: {nested: {deeply: {nested: {structure: {with: {many: {levels: {of: {nesting: \"value\"}}}}}}}}}}) { id } }"
        };
        
        for (String query : testQueries) {
            long startTime = System.currentTimeMillis();
            
            // Perform multiple operations to test caching
            for (int i = 0; i < 10; i++) {
                var doc = astCache.getOrParse(query);
                assertNotNull(doc, "Should parse query");
                
                String printed = astCache.getOrPrint(doc);
                assertNotNull(printed, "Should print document");
                
                GraphQLTransformer transformer = new GraphQLTransformer(query);
                transformer.addField("//query/simple", "name");
                transformer.addField("//query/complex", "description");
                transformer.addField("//mutation/createComplex", "status");
                
                var result = transformer.transform();
                assertTrue(result.isSuccess(), "Transformation should succeed");
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // Verify reasonable performance (should be fast with optimizations)
            assertTrue(duration < 1000, "10 operations should complete in under 1 second");
        }
        
        // Verify optimizations were used
        ASTCache.CacheStats astStats = astCache.getStats();
        assertTrue(astStats.getParsedCacheSize() > 0, "Should have used AST cache");
        assertTrue(astStats.getPrintedCacheSize() > 0, "Should have used print cache");
        
        assertTrue(regexPool.getCacheSize() > 0, "Should have used regex pool");
    }
    
    @Test
    @DisplayName("Generic Memory Efficiency")
    void testGenericMemoryEfficiency() {
        // Test memory efficiency with many different queries
        for (int i = 0; i < 100; i++) {
            String query = String.format("query { entity%d { id name description%d } }", i, i);
            
            // Parse and transform
            var doc = astCache.getOrParse(query);
            assertNotNull(doc, "Should parse query " + i);
            
            GraphQLTransformer transformer = new GraphQLTransformer(query);
            transformer.addField("//query/entity" + i, "status" + i);
            
            var result = transformer.transform();
            assertTrue(result.isSuccess(), "Transformation should succeed for query " + i);
        }
        
        // Verify memory usage is reasonable
        ASTCache.CacheStats astStats = astCache.getStats();
        assertTrue(astStats.getParsedCacheSize() <= 1000, "AST cache should respect size limit");
        assertTrue(astStats.getPrintedCacheSize() <= 1000, "Print cache should respect size limit");
        
        assertTrue(regexPool.getCacheSize() <= 500, "Regex cache should respect size limit");
    }
    
    @Test
    @DisplayName("Generic Thread Safety")
    void testGenericThreadSafety() throws InterruptedException {
        // Test concurrent access to optimization systems
        Thread[] threads = new Thread[10];
        
        for (int i = 0; i < threads.length; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 20; j++) {
                    String query = String.format("query { thread%d_entity%d { id name } }", threadId, j);
                    
                    // Test AST caching
                    var doc = astCache.getOrParse(query);
                    assertNotNull(doc, "Should parse query in thread " + threadId);
                    
                    // Test regex pooling
                    String modified = regexPool.replaceFieldName(query, "thread" + threadId + "_entity" + j, "modified");
                    assertNotEquals(query, modified, "Should modify query in thread " + threadId);
                    
                    // Test transformation
                    GraphQLTransformer transformer = new GraphQLTransformer(query);
                    transformer.addField("//query/thread" + threadId + "_entity" + j, "status");
                    
                    var result = transformer.transform();
                    assertTrue(result.isSuccess(), "Transformation should succeed in thread " + threadId);
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
        assertTrue(stats.getPrintedCacheSize() > 0, "Should have cached prints after concurrent access");
    }
} 