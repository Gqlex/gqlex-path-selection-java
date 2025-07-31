package com.intuit.gqlex.validation.core;

import com.intuit.gqlex.validation.rules.StructuralRule;
import com.intuit.gqlex.validation.rules.ValidationRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for the GraphQL validation system.
 * These tests verify that the validation system is completely generic
 * and works with any GraphQL query or mutation.
 */
@DisplayName("GraphQL Validation System Tests")
public class GraphQLValidatorTest {
    
    private GraphQLValidator validator;
    
    @BeforeEach
    void setUp() {
        validator = new GraphQLValidator();
        validator.addRule(new StructuralRule());
    }
    
    @Test
    @DisplayName("Generic Field Names - Any Field Names Work")
    void testGenericFieldNamesAnyFieldNamesWork() {
        // Test with various field names to ensure no hardcoded assumptions
        String[] fieldNames = {
            "user", "product", "order", "invoice", "customer", "supplier",
            "employee", "vehicle", "building", "document", "transaction",
            "account", "payment", "item", "service", "contract", "policy"
        };
        
        for (String fieldName : fieldNames) {
            String query = String.format("query { %s { id name } }", fieldName);
            
            ValidationResult result = validator.validate(query);
            
            // Generic validation - should work with any field name
            assertTrue(result.isValid(), 
                "Validation should succeed for field: " + fieldName);
            assertEquals(0, result.getErrorCount(), 
                "No errors expected for field: " + fieldName);
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
            ValidationResult result = validator.validate(query);
            
            // Generic validation - should work with any operation type
            assertTrue(result.isValid(), 
                "Validation should succeed for query: " + query.substring(0, Math.min(50, query.length())));
            assertEquals(0, result.getErrorCount(), 
                "No errors expected for operation type");
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
            ValidationResult result = validator.validate(query);
            
            // Generic validation - should work with any complex structure
            assertTrue(result.isValid(), 
                "Validation should succeed for complex query");
            assertEquals(0, result.getErrorCount(), 
                "No errors expected for complex structure");
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
            ValidationResult result = validator.validate(query);
            
            // Generic validation - should work with any arguments/directives
            assertTrue(result.isValid(), 
                "Validation should succeed for query with arguments");
            assertEquals(0, result.getErrorCount(), 
                "No errors expected for arguments/directives");
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
        
        ValidationResult result = validator.validate(queryWithFragments);
        
        // Generic validation - should work with any fragment names
        assertTrue(result.isValid(), 
            "Validation should succeed for query with fragments");
        assertEquals(0, result.getErrorCount(), 
            "No errors expected for fragments");
    }
    
    @Test
    @DisplayName("Generic Error Detection - Invalid Queries")
    void testGenericErrorDetectionInvalidQueries() {
        // Test with invalid queries to ensure errors are detected
        String[] invalidQueries = {
            "", // Empty query
            "query { }", // Empty selection set
            "query { user { } }", // Empty field selection
            "query { user(id: ) { id } }", // Missing argument value
            "fragment UserProfile on User { }", // Fragment without operation
        };
        
        for (String query : invalidQueries) {
            ValidationResult result = validator.validate(query);
            
            // Generic error detection - should detect structural issues
            assertFalse(result.isValid(), 
                "Validation should fail for invalid query: " + query);
            assertTrue(result.getErrorCount() > 0, 
                "Should have errors for invalid query: " + query);
        }
    }
    
    @Test
    @DisplayName("Generic Rule Management")
    void testGenericRuleManagement() {
        // Test rule management functionality
        assertEquals(1, validator.getRuleCount(), 
            "Should have one rule initially");
        
        // Add another rule
        ValidationRule customRule = new ValidationRule("CUSTOM", "Custom validation rule") {
            @Override
            public void validate(ValidationContext context, ValidationResult result) {
                // Custom validation logic
            }
        };
        
        validator.addRule(customRule);
        assertEquals(2, validator.getRuleCount(), 
            "Should have two rules after adding");
        
        // Remove rule
        validator.removeRule("CUSTOM");
        assertEquals(1, validator.getRuleCount(), 
            "Should have one rule after removing");
        
        // Clear rules
        validator.clearRules();
        assertEquals(0, validator.getRuleCount(), 
            "Should have no rules after clearing");
    }
    
    @Test
    @DisplayName("Generic Validation Context")
    void testGenericValidationContext() {
        String query = "query { user { id name } }";
        
        ValidationResult result = validator.validate(query);
        
        // Generic context validation
        assertNotNull(result, "Validation result should not be null");
        assertTrue(result.isValid(), "Query should be valid");
        assertEquals(0, result.getErrorCount(), "No errors expected");
        assertEquals(0, result.getWarningCount(), "No warnings expected");
        assertEquals(0, result.getInfoCount(), "No info expected");
    }
    
    @Test
    @DisplayName("Generic Performance - Multiple Validations")
    void testGenericPerformanceMultipleValidations() {
        // Test performance with multiple validations
        String[] queries = {
            "query { user { id name } }",
            "query { product { id name price } }",
            "query { order { id items { product { name } } } }",
            "mutation { createUser(input: {name: \"John\"}) { id } }",
            "subscription { userUpdated { id name } }"
        };
        
        long startTime = System.currentTimeMillis();
        
        for (String query : queries) {
            for (int i = 0; i < 10; i++) {
                ValidationResult result = validator.validate(query);
                assertNotNull(result, "Validation result should not be null");
            }
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Generic performance validation - should be fast
        assertTrue(duration < 1000, 
            "50 validations should complete in under 1 second, took: " + duration + "ms");
    }
    
    @Test
    @DisplayName("Generic Error Messages - No Hardcoded Field Names")
    void testGenericErrorMessagesNoHardcodedFieldNames() {
        // Test that error messages don't contain hardcoded field names
        String invalidQuery = "query { user(id: ) { id } }"; // Missing argument value
        
        ValidationResult result = validator.validate(invalidQuery);
        
        assertFalse(result.isValid(), "Invalid query should fail validation");
        assertTrue(result.getErrorCount() > 0, "Should have errors");
        
        // Check that error messages are generic
        for (ValidationError error : result.getErrors()) {
            String message = error.getMessage().toLowerCase();
            
            // Generic error message validation - no hardcoded field names
            assertFalse(message.contains("user"), 
                "Error message should not contain hardcoded field name 'user': " + message);
            assertFalse(message.contains("hero"), 
                "Error message should not contain hardcoded field name 'hero': " + message);
            assertFalse(message.contains("product"), 
                "Error message should not contain hardcoded field name 'product': " + message);
        }
    }
    
    @Test
    @DisplayName("Generic Validation Result Methods")
    void testGenericValidationResultMethods() {
        String query = "query { user { id name } }";
        ValidationResult result = validator.validate(query);
        
        // Test all validation result methods
        assertTrue(result.isValid(), "Should be valid");
        assertFalse(result.hasWarnings(), "Should have no warnings");
        assertFalse(result.hasInfo(), "Should have no info");
        assertFalse(result.hasIssues(), "Should have no issues");
        
        assertEquals(0, result.getErrorCount(), "Should have 0 errors");
        assertEquals(0, result.getWarningCount(), "Should have 0 warnings");
        assertEquals(0, result.getInfoCount(), "Should have 0 info");
        assertEquals(0, result.getTotalIssueCount(), "Should have 0 total issues");
        
        assertNotNull(result.getErrors(), "Errors list should not be null");
        assertNotNull(result.getWarnings(), "Warnings list should not be null");
        assertNotNull(result.getInfo(), "Info list should not be null");
        assertNotNull(result.getAllIssues(), "All issues list should not be null");
        assertNotNull(result.getAllIssuesSorted(), "Sorted issues list should not be null");
        
        assertNotNull(result.getSummary(), "Summary should not be null");
        assertNotNull(result.toString(), "ToString should not be null");
    }
} 