package com.intuit.gqlex.linting.core;

import com.intuit.gqlex.linting.rules.LintRule;
import com.intuit.gqlex.linting.config.LintConfig;
import graphql.language.*;
import graphql.parser.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for the GraphQLLinter class covering all GraphQL types and scenarios.
 * 
 * <p>These tests ensure that the linting system works correctly with any GraphQL schema,
 * including queries, mutations, subscriptions, fragments, directives, and all node types.</p>
 * 
 * @author gqlex
 * @version 2.0.1
 * @since 2.0.1
 */
@DisplayName("GraphQLLinter Tests - All GraphQL Types")
public class GraphQLLinterTest {
    
    private GraphQLLinter linter;
    private Parser parser;

    @BeforeEach
    void setUp() {
        linter = new GraphQLLinter();
        parser = new Parser();
    }

    @Test
    @DisplayName("Generic Linting - Any Field Names Work")
    void testGenericLintingAnyFieldNamesWork() {
        // Test with various field names to ensure no hardcoded assumptions
        String[] fieldNames = {
            "user", "product", "order", "invoice", "customer", "supplier",
            "employee", "vehicle", "building", "document", "transaction",
            "account", "payment", "shipping", "billing", "inventory",
            "sales", "marketing", "finance", "hr", "it", "operations"
        };
        
        // Add a simple test rule
        linter.addRule(new TestLintRule());
        
        for (String fieldName : fieldNames) {
            String query = String.format("query { %s { id name } }", fieldName);
            LintResult result = linter.lint(query);
            
            // Generic linting - should work with any field name
            assertNotNull(result, "Linting should complete for field: " + fieldName);
            assertTrue(result.hasIssues(), "Should have issues for field: " + fieldName);
        }
    }

    @Test
    @DisplayName("Query Operations - All Query Types")
    void testQueryOperationsAllQueryTypes() {
        // Add test rule
        linter.addRule(new TestLintRule());
        
        // Test simple query
        String simpleQuery = "query { user { id name } }";
        LintResult result = linter.lint(simpleQuery);
        assertNotNull(result);
        assertTrue(result.hasIssues());
        
        // Test query with variables
        String queryWithVars = "query GetUser($id: ID!) { user(id: $id) { id name } }";
        result = linter.lint(queryWithVars);
        assertNotNull(result);
        assertTrue(result.hasIssues());
        
        // Test query with directives
        String queryWithDirectives = "query @include(if: $includeUser) { user { id name } }";
        result = linter.lint(queryWithDirectives);
        assertNotNull(result);
        assertTrue(result.hasIssues());
    }

    @Test
    @DisplayName("Mutation Operations - All Mutation Types")
    void testMutationOperationsAllMutationTypes() {
        // Add test rule
        linter.addRule(new TestLintRule());
        
        // Test simple mutation
        String simpleMutation = "mutation { createUser(input: {name: \"John\"}) { id name } }";
        LintResult result = linter.lint(simpleMutation);
        assertNotNull(result);
        assertTrue(result.hasIssues());
        
        // Test mutation with variables
        String mutationWithVars = "mutation CreateUser($input: UserInput!) { createUser(input: $input) { id name } }";
        result = linter.lint(mutationWithVars);
        assertNotNull(result);
        assertTrue(result.hasIssues());
        
        // Test mutation with multiple operations
        String multiMutation = "mutation { createUser(input: {name: \"John\"}) { id } updateUser(id: \"1\", input: {name: \"Jane\"}) { id } }";
        result = linter.lint(multiMutation);
        assertNotNull(result);
        assertTrue(result.hasIssues());
    }

    @Test
    @DisplayName("Subscription Operations - All Subscription Types")
    void testSubscriptionOperationsAllSubscriptionTypes() {
        // Add test rule
        linter.addRule(new TestLintRule());
        
        // Test simple subscription
        String simpleSubscription = "subscription { userUpdated { id name } }";
        LintResult result = linter.lint(simpleSubscription);
        assertNotNull(result);
        assertTrue(result.hasIssues());
        
        // Test subscription with variables
        String subscriptionWithVars = "subscription UserUpdates($userId: ID!) { userUpdated(userId: $userId) { id name } }";
        result = linter.lint(subscriptionWithVars);
        assertNotNull(result);
        assertTrue(result.hasIssues());
        
        // Test subscription with filters
        String subscriptionWithFilters = "subscription { userUpdated(filter: {active: true}) { id name status } }";
        result = linter.lint(subscriptionWithFilters);
        assertNotNull(result);
        assertTrue(result.hasIssues());
    }

    @Test
    @DisplayName("Fragment Definitions - All Fragment Types")
    void testFragmentDefinitionsAllFragmentTypes() {
        // Add test rule
        linter.addRule(new TestLintRule());
        
        // Test simple fragment
        String fragmentQuery = "query { user { ...userFields } } fragment userFields on User { id name }";
        LintResult result = linter.lint(fragmentQuery);
        assertNotNull(result);
        assertTrue(result.hasIssues());
        
        // Test fragment with directives
        String fragmentWithDirectives = "fragment userFields on User @include(if: $includeUser) { id name }";
        result = linter.lint(fragmentWithDirectives);
        assertNotNull(result);
        assertTrue(result.hasIssues());
        
        // Test inline fragment
        String inlineFragmentQuery = "query { user { ... on User { id name } } }";
        result = linter.lint(inlineFragmentQuery);
        assertNotNull(result);
        assertTrue(result.hasIssues());
    }

    @Test
    @DisplayName("Directives - All Directive Types")
    void testDirectivesAllDirectiveTypes() {
        // Add test rule
        linter.addRule(new TestLintRule());
        
        // Test field directive
        String fieldDirectiveQuery = "query { user @include(if: $includeUser) { id name } }";
        LintResult result = linter.lint(fieldDirectiveQuery);
        assertNotNull(result);
        assertTrue(result.hasIssues());
        
        // Test fragment directive
        String fragmentDirectiveQuery = "fragment userFields on User @include(if: $includeUser) { id name }";
        result = linter.lint(fragmentDirectiveQuery);
        assertNotNull(result);
        assertTrue(result.hasIssues());
        
        // Test operation directive
        String operationDirectiveQuery = "query @deprecated(reason: \"Use new query\") { user { id } }";
        result = linter.lint(operationDirectiveQuery);
        assertNotNull(result);
        assertTrue(result.hasIssues());
    }

    @Test
    @DisplayName("Arguments - All Argument Types")
    void testArgumentsAllArgumentTypes() {
        // Add test rule
        linter.addRule(new TestLintRule());
        
        // Test string argument
        String stringArgQuery = "query { user(id: \"123\") { id name } }";
        LintResult result = linter.lint(stringArgQuery);
        assertNotNull(result);
        assertTrue(result.hasIssues());
        
        // Test int argument
        String intArgQuery = "query { users(limit: 10) { id name } }";
        result = linter.lint(intArgQuery);
        assertNotNull(result);
        assertTrue(result.hasIssues());
        
        // Test boolean argument
        String boolArgQuery = "query { user(includeProfile: true) { id name } }";
        result = linter.lint(boolArgQuery);
        assertNotNull(result);
        assertTrue(result.hasIssues());
        
        // Test variable argument
        String varArgQuery = "query GetUser($id: ID!) { user(id: $id) { id name } }";
        result = linter.lint(varArgQuery);
        assertNotNull(result);
        assertTrue(result.hasIssues());
    }

    @Test
    @DisplayName("Variables - All Variable Types")
    void testVariablesAllVariableTypes() {
        // Add test rule
        linter.addRule(new TestLintRule());
        
        // Test simple variable
        String simpleVarQuery = "query GetUser($id: ID!) { user(id: $id) { id name } }";
        LintResult result = linter.lint(simpleVarQuery);
        assertNotNull(result);
        assertTrue(result.hasIssues());
        
        // Test variable with default value
        String varWithDefaultQuery = "query GetUsers($limit: Int = 10) { users(limit: $limit) { id name } }";
        result = linter.lint(varWithDefaultQuery);
        assertNotNull(result);
        assertTrue(result.hasIssues());
        
        // Test variable with non-null type
        String nonNullVarQuery = "query GetUser($id: ID!, $includeProfile: Boolean!) { user(id: $id) { id name } }";
        result = linter.lint(nonNullVarQuery);
        assertNotNull(result);
        assertTrue(result.hasIssues());
    }

    @Test
    @DisplayName("Selection Sets - All Selection Types")
    void testSelectionSetsAllSelectionTypes() {
        // Add test rule
        linter.addRule(new TestLintRule());
        
        // Test simple selection set
        String simpleSelectionQuery = "query { user { id name email } }";
        LintResult result = linter.lint(simpleSelectionQuery);
        assertNotNull(result);
        assertTrue(result.hasIssues());
        
        // Test nested selection set
        String nestedSelectionQuery = "query { user { id name profile { bio avatar } } }";
        result = linter.lint(nestedSelectionQuery);
        assertNotNull(result);
        assertTrue(result.hasIssues());
        
        // Test selection set with fragments
        String fragmentSelectionQuery = "query { user { ...userFields profile { ...profileFields } } }";
        result = linter.lint(fragmentSelectionQuery);
        assertNotNull(result);
        assertTrue(result.hasIssues());
    }

    @Test
    @DisplayName("Complex Nested Structures - Deep Nesting")
    void testComplexNestedStructuresDeepNesting() {
        // Add test rule
        linter.addRule(new TestLintRule());
        
        // Test deeply nested query
        String deepNestedQuery = "query { " +
            "user { " +
            "  id name " +
            "  posts { " +
            "    id title " +
            "    comments { " +
            "      id text " +
            "      author { " +
            "        id name " +
            "        profile { " +
            "          bio avatar " +
            "        } " +
            "      } " +
            "    } " +
            "  } " +
            "} " +
            "}";
        
        LintResult result = linter.lint(deepNestedQuery);
        assertNotNull(result);
        assertTrue(result.hasIssues());
    }

    @Test
    @DisplayName("Multiple Operations - Mixed Operation Types")
    void testMultipleOperationsMixedOperationTypes() {
        // Add test rule
        linter.addRule(new TestLintRule());
        
        // Test document with multiple operations
        String multiOpQuery = "query GetUser($id: ID!) { user(id: $id) { id name } } " +
            "mutation CreateUser($input: UserInput!) { createUser(input: $input) { id name } } " +
            "subscription UserUpdates { userUpdated { id name } }";
        
        LintResult result = linter.lint(multiOpQuery);
        assertNotNull(result);
        assertTrue(result.hasIssues());
    }

    @Test
    @DisplayName("Rule Management - Add, Remove, Clear")
    void testRuleManagementAddRemoveClear() {
        // Test adding rules
        TestLintRule rule1 = new TestLintRule();
        TestLintRule rule2 = new TestLintRule();
        
        linter.addRule(rule1);
        assertEquals(1, linter.getRuleCount());
        assertTrue(linter.hasRules());
        
        linter.addRule(rule2);
        assertEquals(2, linter.getRuleCount());
        
        // Test removing rules
        linter.removeRule(rule1);
        assertEquals(1, linter.getRuleCount());
        
        linter.removeRule("TEST_RULE");
        assertEquals(0, linter.getRuleCount());
        assertFalse(linter.hasRules());
        
        // Test clearing rules
        linter.addRule(rule1);
        linter.addRule(rule2);
        assertEquals(2, linter.getRuleCount());
        
        linter.clearRules();
        assertEquals(0, linter.getRuleCount());
        assertFalse(linter.hasRules());
    }

    @Test
    @DisplayName("Configuration - Custom Configuration")
    void testConfigurationCustomConfiguration() {
        // Test with custom configuration
        LintConfig config = new LintConfig();
        config.setValue("maxLineLength", 120);
        config.setValue("maxDepth", 10);
        
        GraphQLLinter customLinter = new GraphQLLinter(config);
        customLinter.addRule(new TestLintRule());
        
        String query = "query { user { id name } }";
        LintResult result = customLinter.lint(query);
        
        assertNotNull(result);
        assertTrue(result.hasIssues());
        
        // Verify configuration is accessible
        LintConfig resultConfig = customLinter.getConfig();
        assertEquals(120, resultConfig.getValue("maxLineLength", Integer.class));
        assertEquals(10, resultConfig.getValue("maxDepth", Integer.class));
    }

    @Test
    @DisplayName("Error Handling - Invalid Queries")
    void testErrorHandlingInvalidQueries() {
        // Test null query
        LintResult result = linter.lint((String) null);
        assertNotNull(result);
        assertTrue(result.hasErrors());
        
        // Test empty query
        result = linter.lint("");
        assertNotNull(result);
        assertTrue(result.hasErrors());
        
        // Test invalid syntax
        result = linter.lint("invalid graphql syntax {");
        assertNotNull(result);
        assertTrue(result.hasErrors());
        
        // Test null document
        result = linter.lint((Document) null);
        assertNotNull(result);
        assertFalse(result.hasIssues());
    }

    @Test
    @DisplayName("Rule Categories - Category Management")
    void testRuleCategoriesCategoryManagement() {
        // Add rules with different categories
        TestLintRule styleRule = new TestLintRule("STYLE_RULE", "Style rule");
        TestLintRule perfRule = new TestLintRule("PERF_RULE", "Performance rule");
        
        linter.addRule(styleRule);
        linter.addRule(perfRule);
        
        // Test getting rules by category
        List<LintRule> styleRules = linter.getRulesByCategory("STYLE");
        assertEquals(1, styleRules.size());
        assertEquals("STYLE_RULE", styleRules.get(0).getName());
        
        List<LintRule> perfRules = linter.getRulesByCategory("PERFORMANCE");
        assertEquals(1, perfRules.size());
        assertEquals("PERF_RULE", perfRules.get(0).getName());
        
        // Test getting rules by non-existent category
        List<LintRule> nonExistentRules = linter.getRulesByCategory("NON_EXISTENT");
        assertEquals(0, nonExistentRules.size());
    }

    @Test
    @DisplayName("Linter Copying - Deep and Shallow Copy")
    void testLinterCopyingDeepAndShallowCopy() {
        // Add rules and configuration
        linter.addRule(new TestLintRule());
        LintConfig config = new LintConfig();
        config.setValue("testValue", "test");
        GraphQLLinter configuredLinter = new GraphQLLinter(config);
        configuredLinter.addRule(new TestLintRule());
        
        // Test shallow copy
        GraphQLLinter shallowCopy = configuredLinter.copy();
        assertEquals(0, shallowCopy.getRuleCount()); // No rules copied
        assertNotNull(shallowCopy.getConfig()); // Config copied
        
        // Test deep copy
        GraphQLLinter deepCopy = configuredLinter.deepCopy();
        assertEquals(1, deepCopy.getRuleCount()); // Rules copied
        assertNotNull(deepCopy.getConfig()); // Config copied
        
        // Test that copies are independent
        shallowCopy.addRule(new TestLintRule());
        assertEquals(1, shallowCopy.getRuleCount());
        assertEquals(1, configuredLinter.getRuleCount()); // Original unchanged
    }

    @Test
    @DisplayName("Rule Finding - Get Rule by Name")
    void testRuleFindingGetRuleByName() {
        TestLintRule testRule = new TestLintRule("TEST_RULE", "Test rule");
        linter.addRule(testRule);
        
        // Test finding existing rule
        LintRule foundRule = linter.getRule("TEST_RULE");
        assertNotNull(foundRule);
        assertEquals("TEST_RULE", foundRule.getName());
        
        // Test finding non-existent rule
        LintRule nonExistentRule = linter.getRule("NON_EXISTENT");
        assertNull(nonExistentRule);
        
        // Test checking if rule exists
        assertTrue(linter.hasRule("TEST_RULE"));
        assertFalse(linter.hasRule("NON_EXISTENT"));
    }

    /**
     * Simple test linting rule for testing purposes.
     */
    private static class TestLintRule extends LintRule {
        public TestLintRule() {
            super("TEST_RULE", "Test linting rule");
        }
        
        public TestLintRule(String name, String description) {
            super(name, description);
        }
        
        @Override
        public void lint(LintContext context, LintResult result) {
            // Add a test issue for every field found
            context.traverseNodes(node -> {
                if (node instanceof Field) {
                    Field field = (Field) node;
                    result.addInfo(getName(), 
                        String.format("Test info for field: %s", field.getName()), 
                        field);
                }
            });
        }
        
        @Override
        public String getCategory() {
            return getName().startsWith("STYLE") ? "STYLE" : "PERFORMANCE";
        }
    }
} 