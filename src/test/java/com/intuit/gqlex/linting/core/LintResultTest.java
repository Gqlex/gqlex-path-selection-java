package com.intuit.gqlex.linting.core;

import graphql.language.*;
import graphql.parser.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for the LintResult class covering all GraphQL types and scenarios.
 * 
 * <p>These tests ensure that the linting system works correctly with any GraphQL schema,
 * including queries, mutations, subscriptions, fragments, directives, and all node types.</p>
 * 
 * @author gqlex
 * @version 2.0.1
 * @since 2.0.1
 */
@DisplayName("LintResult Tests - All GraphQL Types")
public class LintResultTest {
    
    private LintResult lintResult;
    private Parser parser;

    @BeforeEach
    void setUp() {
        lintResult = new LintResult();
        parser = new Parser();
    }

    @Test
    @DisplayName("Generic Field Names - Any Field Names Work")
    void testGenericFieldNamesAnyFieldNamesWork() {
        // Test with various field names to ensure no hardcoded assumptions
        String[] fieldNames = {
            "user", "product", "order", "invoice", "customer", "supplier",
            "employee", "vehicle", "building", "document", "transaction",
            "account", "payment", "shipping", "billing", "inventory",
            "sales", "marketing", "finance", "hr", "it", "operations"
        };
        
        for (String fieldName : fieldNames) {
            String query = String.format("query { %s { id name } }", fieldName);
            Document document = parser.parseDocument(query);
            Field field = findFieldByName(document, fieldName);
            
            lintResult.addError("TEST_RULE", "Test error for " + fieldName, field);
            lintResult.addWarning("TEST_RULE", "Test warning for " + fieldName, field);
            lintResult.addInfo("TEST_RULE", "Test info for " + fieldName, field);
            
            // Verify issues were added correctly
            assertTrue(lintResult.hasErrors(), "Should have errors for field: " + fieldName);
            assertTrue(lintResult.hasWarnings(), "Should have warnings for field: " + fieldName);
            assertTrue(lintResult.hasInfo(), "Should have info for field: " + fieldName);
            
            // Verify issue counts
            assertEquals(1, lintResult.getErrorCount(), "Should have 1 error for field: " + fieldName);
            assertEquals(1, lintResult.getWarningCount(), "Should have 1 warning for field: " + fieldName);
            assertEquals(1, lintResult.getInfoCount(), "Should have 1 info for field: " + fieldName);
            
            // Clear for next iteration
            lintResult.clear();
        }
    }

    @Test
    @DisplayName("Query Operations - All Query Types")
    void testQueryOperationsAllQueryTypes() {
        // Test simple query
        String simpleQuery = "query { user { id name } }";
        Document document = parser.parseDocument(simpleQuery);
        OperationDefinition operation = findOperation(document);
        
        lintResult.addError("QUERY_RULE", "Query error", operation);
        assertTrue(lintResult.hasErrors());
        assertEquals(1, lintResult.getErrorCount());
        
        // Test query with variables
        String queryWithVars = "query GetUser($id: ID!) { user(id: $id) { id name } }";
        document = parser.parseDocument(queryWithVars);
        operation = findOperation(document);
        
        lintResult.clear();
        lintResult.addWarning("QUERY_RULE", "Query with variables warning", operation);
        assertTrue(lintResult.hasWarnings());
        
        // Test query with directives
        String queryWithDirectives = "query @include(if: $includeUser) { user { id name } }";
        document = parser.parseDocument(queryWithDirectives);
        operation = findOperation(document);
        
        lintResult.clear();
        lintResult.addInfo("QUERY_RULE", "Query with directives info", operation);
        assertTrue(lintResult.hasInfo());
        assertEquals(1, lintResult.getInfoCount());
    }

    @Test
    @DisplayName("Mutation Operations - All Mutation Types")
    void testMutationOperationsAllMutationTypes() {
        // Test simple mutation
        String simpleMutation = "mutation { createUser(input: {name: \"John\"}) { id name } }";
        Document document = parser.parseDocument(simpleMutation);
        OperationDefinition operation = findOperation(document);
        
        lintResult.addError("MUTATION_RULE", "Mutation error", operation);
        assertTrue(lintResult.hasErrors());
        
        // Test mutation with variables
        String mutationWithVars = "mutation CreateUser($input: UserInput!) { createUser(input: $input) { id name } }";
        document = parser.parseDocument(mutationWithVars);
        operation = findOperation(document);
        
        lintResult.clear();
        lintResult.addWarning("MUTATION_RULE", "Mutation with variables warning", operation);
        assertTrue(lintResult.hasWarnings());
        
        // Test mutation with multiple operations
        String multiMutation = "mutation { createUser(input: {name: \"John\"}) { id } updateUser(id: \"1\", input: {name: \"Jane\"}) { id } }";
        document = parser.parseDocument(multiMutation);
        operation = findOperation(document);
        
        lintResult.clear();
        lintResult.addInfo("MUTATION_RULE", "Multi-operation mutation info", operation);
        assertTrue(lintResult.hasInfo());
    }

    @Test
    @DisplayName("Subscription Operations - All Subscription Types")
    void testSubscriptionOperationsAllSubscriptionTypes() {
        // Test simple subscription
        String simpleSubscription = "subscription { userUpdated { id name } }";
        Document document = parser.parseDocument(simpleSubscription);
        OperationDefinition operation = findOperation(document);
        
        lintResult.addError("SUBSCRIPTION_RULE", "Subscription error", operation);
        assertTrue(lintResult.hasErrors());
        
        // Test subscription with variables
        String subscriptionWithVars = "subscription UserUpdates($userId: ID!) { userUpdated(userId: $userId) { id name } }";
        document = parser.parseDocument(subscriptionWithVars);
        operation = findOperation(document);
        
        lintResult.clear();
        lintResult.addWarning("SUBSCRIPTION_RULE", "Subscription with variables warning", operation);
        assertTrue(lintResult.hasWarnings());
        
        // Test subscription with filters
        String subscriptionWithFilters = "subscription { userUpdated(filter: {active: true}) { id name status } }";
        document = parser.parseDocument(subscriptionWithFilters);
        operation = findOperation(document);
        
        lintResult.clear();
        lintResult.addInfo("SUBSCRIPTION_RULE", "Subscription with filters info", operation);
        assertTrue(lintResult.hasInfo());
    }

    @Test
    @DisplayName("Fragment Definitions - All Fragment Types")
    void testFragmentDefinitionsAllFragmentTypes() {
        // Test simple fragment
        String fragmentQuery = "query { user { ...userFields } } fragment userFields on User { id name }";
        Document document = parser.parseDocument(fragmentQuery);
        FragmentDefinition fragment = findFragmentDefinition(document);
        
        lintResult.addError("FRAGMENT_RULE", "Fragment error", fragment);
        assertTrue(lintResult.hasErrors());
        
        // Test fragment with directives
        String fragmentWithDirectives = "fragment userFields on User @include(if: $includeUser) { id name }";
        document = parser.parseDocument(fragmentWithDirectives);
        fragment = findFragmentDefinition(document);
        
        lintResult.clear();
        lintResult.addWarning("FRAGMENT_RULE", "Fragment with directives warning", fragment);
        assertTrue(lintResult.hasWarnings());
        
        // Test inline fragment
        String inlineFragmentQuery = "query { user { ... on User { id name } } }";
        document = parser.parseDocument(inlineFragmentQuery);
        InlineFragment inlineFragment = findInlineFragment(document);
        
        lintResult.clear();
        lintResult.addInfo("FRAGMENT_RULE", "Inline fragment info", inlineFragment);
        assertTrue(lintResult.hasInfo());
    }

    @Test
    @DisplayName("Directives - All Directive Types")
    void testDirectivesAllDirectiveTypes() {
        // Test field directive
        String fieldDirectiveQuery = "query { user @include(if: $includeUser) { id name } }";
        Document document = parser.parseDocument(fieldDirectiveQuery);
        Directive directive = findDirective(document);
        
        lintResult.addError("DIRECTIVE_RULE", "Directive error", directive);
        assertTrue(lintResult.hasErrors());
        
        // Test fragment directive
        String fragmentDirectiveQuery = "fragment userFields on User @include(if: $includeUser) { id name }";
        document = parser.parseDocument(fragmentDirectiveQuery);
        directive = findDirective(document);
        
        lintResult.clear();
        lintResult.addWarning("DIRECTIVE_RULE", "Fragment directive warning", directive);
        assertTrue(lintResult.hasWarnings());
        
        // Test operation directive
        String operationDirectiveQuery = "query @deprecated(reason: \"Use new query\") { user { id } }";
        document = parser.parseDocument(operationDirectiveQuery);
        directive = findDirective(document);
        
        lintResult.clear();
        lintResult.addInfo("DIRECTIVE_RULE", "Operation directive info", directive);
        assertTrue(lintResult.hasInfo());
    }

    @Test
    @DisplayName("Arguments - All Argument Types")
    void testArgumentsAllArgumentTypes() {
        // Test string argument
        String stringArgQuery = "query { user(id: \"123\") { id name } }";
        Document document = parser.parseDocument(stringArgQuery);
        Argument argument = findArgument(document);
        
        lintResult.addError("ARGUMENT_RULE", "String argument error", argument);
        assertTrue(lintResult.hasErrors());
        
        // Test int argument
        String intArgQuery = "query { users(limit: 10) { id name } }";
        document = parser.parseDocument(intArgQuery);
        argument = findArgument(document);
        
        lintResult.clear();
        lintResult.addWarning("ARGUMENT_RULE", "Int argument warning", argument);
        assertTrue(lintResult.hasWarnings());
        
        // Test boolean argument
        String boolArgQuery = "query { user(includeProfile: true) { id name } }";
        document = parser.parseDocument(boolArgQuery);
        argument = findArgument(document);
        
        lintResult.clear();
        lintResult.addInfo("ARGUMENT_RULE", "Boolean argument info", argument);
        assertTrue(lintResult.hasInfo());
        
        // Test variable argument
        String varArgQuery = "query GetUser($id: ID!) { user(id: $id) { id name } }";
        document = parser.parseDocument(varArgQuery);
        argument = findArgument(document);
        
        lintResult.clear();
        lintResult.addError("ARGUMENT_RULE", "Variable argument error", argument);
        assertTrue(lintResult.hasErrors());
    }

    @Test
    @DisplayName("Variables - All Variable Types")
    void testVariablesAllVariableTypes() {
        // Test simple variable
        String simpleVarQuery = "query GetUser($id: ID!) { user(id: $id) { id name } }";
        Document document = parser.parseDocument(simpleVarQuery);
        VariableDefinition variable = findVariableDefinition(document);
        
        lintResult.addError("VARIABLE_RULE", "Simple variable error", variable);
        assertTrue(lintResult.hasErrors());
        
        // Test variable with default value
        String varWithDefaultQuery = "query GetUsers($limit: Int = 10) { users(limit: $limit) { id name } }";
        document = parser.parseDocument(varWithDefaultQuery);
        variable = findVariableDefinition(document);
        
        lintResult.clear();
        lintResult.addWarning("VARIABLE_RULE", "Variable with default warning", variable);
        assertTrue(lintResult.hasWarnings());
        
        // Test variable with non-null type
        String nonNullVarQuery = "query GetUser($id: ID!, $includeProfile: Boolean!) { user(id: $id) { id name } }";
        document = parser.parseDocument(nonNullVarQuery);
        variable = findVariableDefinition(document);
        
        lintResult.clear();
        lintResult.addInfo("VARIABLE_RULE", "Non-null variable info", variable);
        assertTrue(lintResult.hasInfo());
    }

    @Test
    @DisplayName("Selection Sets - All Selection Types")
    void testSelectionSetsAllSelectionTypes() {
        // Test simple selection set
        String simpleSelectionQuery = "query { user { id name email } }";
        Document document = parser.parseDocument(simpleSelectionQuery);
        SelectionSet selectionSet = findSelectionSet(document);
        
        lintResult.addError("SELECTION_RULE", "Simple selection set error", selectionSet);
        assertTrue(lintResult.hasErrors());
        
        // Test nested selection set
        String nestedSelectionQuery = "query { user { id name profile { bio avatar } } }";
        document = parser.parseDocument(nestedSelectionQuery);
        selectionSet = findSelectionSet(document);
        
        lintResult.clear();
        lintResult.addWarning("SELECTION_RULE", "Nested selection set warning", selectionSet);
        assertTrue(lintResult.hasWarnings());
        
        // Test selection set with fragments
        String fragmentSelectionQuery = "query { user { ...userFields profile { ...profileFields } } }";
        document = parser.parseDocument(fragmentSelectionQuery);
        selectionSet = findSelectionSet(document);
        
        lintResult.clear();
        lintResult.addInfo("SELECTION_RULE", "Selection set with fragments info", selectionSet);
        assertTrue(lintResult.hasInfo());
    }

    @Test
    @DisplayName("Complex Nested Structures - Deep Nesting")
    void testComplexNestedStructuresDeepNesting() {
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
        
        Document document = parser.parseDocument(deepNestedQuery);
        
        // Test various nodes in the deep structure
        Field userField = findFieldByName(document, "user");
        Field postsField = findFieldByName(document, "posts");
        Field commentsField = findFieldByName(document, "comments");
        Field authorField = findFieldByName(document, "author");
        Field profileField = findFieldByName(document, "profile");
        
        lintResult.addError("DEEP_NESTING_RULE", "Deep nesting error", userField);
        lintResult.addWarning("DEEP_NESTING_RULE", "Deep nesting warning", postsField);
        lintResult.addInfo("DEEP_NESTING_RULE", "Deep nesting info", commentsField);
        lintResult.addError("DEEP_NESTING_RULE", "Author field error", authorField);
        lintResult.addWarning("DEEP_NESTING_RULE", "Profile field warning", profileField);
        
        assertTrue(lintResult.hasErrors());
        assertTrue(lintResult.hasWarnings());
        assertTrue(lintResult.hasInfo());
        assertEquals(2, lintResult.getErrorCount());
        assertEquals(2, lintResult.getWarningCount());
        assertEquals(1, lintResult.getInfoCount());
    }

    @Test
    @DisplayName("Multiple Operations - Mixed Operation Types")
    void testMultipleOperationsMixedOperationTypes() {
        // Test document with multiple operations
        String multiOpQuery = "query GetUser($id: ID!) { user(id: $id) { id name } } " +
            "mutation CreateUser($input: UserInput!) { createUser(input: $input) { id name } } " +
            "subscription UserUpdates { userUpdated { id name } }";
        
        Document document = parser.parseDocument(multiOpQuery);
        List<OperationDefinition> operations = document.getDefinitions().stream()
            .filter(def -> def instanceof OperationDefinition)
            .map(def -> (OperationDefinition) def)
            .toList();
        
        assertEquals(3, operations.size());
        
        // Test each operation type
        OperationDefinition queryOp = operations.get(0);
        OperationDefinition mutationOp = operations.get(1);
        OperationDefinition subscriptionOp = operations.get(2);
        
        lintResult.addError("QUERY_RULE", "Query operation error", queryOp);
        lintResult.addWarning("MUTATION_RULE", "Mutation operation warning", mutationOp);
        lintResult.addInfo("SUBSCRIPTION_RULE", "Subscription operation info", subscriptionOp);
        
        assertTrue(lintResult.hasErrors());
        assertTrue(lintResult.hasWarnings());
        assertTrue(lintResult.hasInfo());
        assertEquals(1, lintResult.getErrorCount());
        assertEquals(1, lintResult.getWarningCount());
        assertEquals(1, lintResult.getInfoCount());
    }

    @Test
    @DisplayName("Issue Management - Add, Clear, Merge")
    void testIssueManagementAddClearMerge() {
        // Test adding issues
        String query = "query { user { id name } }";
        Document document = parser.parseDocument(query);
        Field field = findFieldByName(document, "user");
        
        lintResult.addError("TEST_RULE", "Test error", field);
        lintResult.addWarning("TEST_RULE", "Test warning", field);
        lintResult.addInfo("TEST_RULE", "Test info", field);
        
        assertTrue(lintResult.hasIssues());
        assertEquals(1, lintResult.getErrorCount());
        assertEquals(1, lintResult.getWarningCount());
        assertEquals(1, lintResult.getInfoCount());
        assertEquals(3, lintResult.getTotalIssueCount());
        
        // Test clearing issues
        lintResult.clear();
        assertFalse(lintResult.hasIssues());
        assertEquals(0, lintResult.getTotalIssueCount());
        
        // Test merging results
        LintResult otherResult = new LintResult();
        otherResult.addError("OTHER_RULE", "Other error", field);
        otherResult.addWarning("OTHER_RULE", "Other warning", field);
        
        lintResult.addError("THIS_RULE", "This error", field);
        lintResult.merge(otherResult);
        
        assertEquals(2, lintResult.getErrorCount());
        assertEquals(1, lintResult.getWarningCount());
        assertEquals(3, lintResult.getTotalIssueCount());
    }

    @Test
    @DisplayName("Issue Filtering - By Level and Rule")
    void testIssueFilteringByLevelAndRule() {
        String query = "query { user { id name } }";
        Document document = parser.parseDocument(query);
        Field field = findFieldByName(document, "user");
        
        // Add issues from different rules
        lintResult.addError("RULE_A", "Error from rule A", field);
        lintResult.addWarning("RULE_A", "Warning from rule A", field);
        lintResult.addInfo("RULE_A", "Info from rule A", field);
        lintResult.addError("RULE_B", "Error from rule B", field);
        lintResult.addWarning("RULE_B", "Warning from rule B", field);
        
        // Test filtering by level
        List<LintIssue> errors = lintResult.getIssuesByLevel(LintLevel.ERROR);
        List<LintIssue> warnings = lintResult.getIssuesByLevel(LintLevel.WARNING);
        List<LintIssue> info = lintResult.getIssuesByLevel(LintLevel.INFO);
        
        assertEquals(2, errors.size());
        assertEquals(2, warnings.size());
        assertEquals(1, info.size());
        
        // Test filtering by rule
        List<LintIssue> ruleAIssues = lintResult.getIssuesByRule("RULE_A");
        List<LintIssue> ruleBIssues = lintResult.getIssuesByRule("RULE_B");
        List<LintIssue> ruleCIssues = lintResult.getIssuesByRule("RULE_C");
        
        assertEquals(3, ruleAIssues.size());
        assertEquals(2, ruleBIssues.size());
        assertEquals(0, ruleCIssues.size());
    }

    @Test
    @DisplayName("Summary Generation - All Issue Types")
    void testSummaryGenerationAllIssueTypes() {
        String query = "query { user { id name } }";
        Document document = parser.parseDocument(query);
        Field field = findFieldByName(document, "user");
        
        // Test empty result
        String emptySummary = lintResult.getSummary();
        assertTrue(emptySummary.contains("No issues found"));
        
        // Test with errors only
        lintResult.addError("ERROR_RULE", "Test error", field);
        String errorSummary = lintResult.getSummary();
        assertTrue(errorSummary.contains("1 error(s)"));
        assertFalse(errorSummary.contains("warning"));
        assertFalse(errorSummary.contains("info"));
        
        // Test with warnings only
        lintResult.clear();
        lintResult.addWarning("WARNING_RULE", "Test warning", field);
        String warningSummary = lintResult.getSummary();
        assertTrue(warningSummary.contains("1 warning(s)"));
        assertFalse(warningSummary.contains("error"));
        assertFalse(warningSummary.contains("info"));
        
        // Test with info only
        lintResult.clear();
        lintResult.addInfo("INFO_RULE", "Test info", field);
        String infoSummary = lintResult.getSummary();
        assertTrue(infoSummary.contains("1 info"));
        assertFalse(infoSummary.contains("error"));
        assertFalse(infoSummary.contains("warning"));
        
        // Test with all types
        lintResult.addError("ERROR_RULE", "Test error", field);
        lintResult.addWarning("WARNING_RULE", "Test warning", field);
        String allSummary = lintResult.getSummary();
        assertTrue(allSummary.contains("1 error(s)"));
        assertTrue(allSummary.contains("1 warning(s)"));
        assertTrue(allSummary.contains("1 info"));
    }

    // Helper methods to find specific nodes in the document
    private Field findFieldByName(Document document, String fieldName) {
        return document.getDefinitions().stream()
            .filter(def -> def instanceof OperationDefinition)
            .map(def -> (OperationDefinition) def)
            .flatMap(op -> op.getSelectionSet().getSelections().stream())
            .filter(sel -> sel instanceof Field)
            .map(sel -> (Field) sel)
            .filter(field -> fieldName.equals(field.getName()))
            .findFirst()
            .orElse(null);
    }

    private OperationDefinition findOperation(Document document) {
        return document.getDefinitions().stream()
            .filter(def -> def instanceof OperationDefinition)
            .map(def -> (OperationDefinition) def)
            .findFirst()
            .orElse(null);
    }

    private FragmentDefinition findFragmentDefinition(Document document) {
        return document.getDefinitions().stream()
            .filter(def -> def instanceof FragmentDefinition)
            .map(def -> (FragmentDefinition) def)
            .findFirst()
            .orElse(null);
    }

    private InlineFragment findInlineFragment(Document document) {
        return document.getDefinitions().stream()
            .filter(def -> def instanceof OperationDefinition)
            .map(def -> (OperationDefinition) def)
            .flatMap(op -> op.getSelectionSet().getSelections().stream())
            .filter(sel -> sel instanceof InlineFragment)
            .map(sel -> (InlineFragment) sel)
            .findFirst()
            .orElse(null);
    }

    private Directive findDirective(Document document) {
        return document.getDefinitions().stream()
            .filter(def -> def instanceof OperationDefinition)
            .map(def -> (OperationDefinition) def)
            .flatMap(op -> op.getSelectionSet().getSelections().stream())
            .filter(sel -> sel instanceof Field)
            .map(sel -> (Field) sel)
            .flatMap(field -> field.getDirectives().stream())
            .findFirst()
            .orElse(null);
    }

    private Argument findArgument(Document document) {
        return document.getDefinitions().stream()
            .filter(def -> def instanceof OperationDefinition)
            .map(def -> (OperationDefinition) def)
            .flatMap(op -> op.getSelectionSet().getSelections().stream())
            .filter(sel -> sel instanceof Field)
            .map(sel -> (Field) sel)
            .flatMap(field -> field.getArguments().stream())
            .findFirst()
            .orElse(null);
    }

    private VariableDefinition findVariableDefinition(Document document) {
        return document.getDefinitions().stream()
            .filter(def -> def instanceof OperationDefinition)
            .map(def -> (OperationDefinition) def)
            .flatMap(op -> op.getVariableDefinitions().stream())
            .findFirst()
            .orElse(null);
    }

    private SelectionSet findSelectionSet(Document document) {
        return document.getDefinitions().stream()
            .filter(def -> def instanceof OperationDefinition)
            .map(def -> (OperationDefinition) def)
            .map(OperationDefinition::getSelectionSet)
            .findFirst()
            .orElse(null);
    }
} 