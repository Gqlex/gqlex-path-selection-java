package com.intuit.gqlex.linting.rules;

import com.intuit.gqlex.linting.config.LintConfig;
import com.intuit.gqlex.linting.core.LintContext;
import com.intuit.gqlex.linting.core.LintResult;
import graphql.language.Document;
import graphql.parser.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StyleRule Tests")
public class StyleRuleTest {
    
    private StyleRule styleRule;
    private Parser parser;
    private LintConfig config;
    
    @BeforeEach
    void setUp() {
        styleRule = new StyleRule();
        parser = new Parser();
        config = new LintConfig();
    }
    
    @Test
    @DisplayName("Naming Conventions - CamelCase Fields")
    void testNamingConventionsCamelCaseFields() {
        String query = "query { user { id name emailAddress phoneNumber } }";
        Document document = parser.parseDocument(query);
        LintContext context = new LintContext(document, config);
        LintResult result = new LintResult();
        
        styleRule.lint(context, result);
        
        // Should not have warnings for proper camelCase
        assertFalse(result.hasWarnings());
        assertFalse(result.hasErrors());
    }
    
    @Test
    @DisplayName("Naming Conventions - Non-CamelCase Fields")
    void testNamingConventionsNonCamelCaseFields() {
        String query = "query { user { ID Name email_address phone_number } }";
        Document document = parser.parseDocument(query);
        LintContext context = new LintContext(document, config);
        LintResult result = new LintResult();
        
        styleRule.lint(context, result);
        
        // Should have warnings for non-camelCase fields
        assertTrue(result.hasWarnings());
        assertEquals(4, result.getWarningCount());
        
        assertTrue(result.getWarnings().stream()
            .anyMatch(warning -> warning.getMessage().contains("ID") && warning.getMessage().contains("camelCase")));
        assertTrue(result.getWarnings().stream()
            .anyMatch(warning -> warning.getMessage().contains("Name") && warning.getMessage().contains("camelCase")));
        assertTrue(result.getWarnings().stream()
            .anyMatch(warning -> warning.getMessage().contains("email_address") && warning.getMessage().contains("camelCase")));
        assertTrue(result.getWarnings().stream()
            .anyMatch(warning -> warning.getMessage().contains("phone_number") && warning.getMessage().contains("camelCase")));
    }
    
    @Test
    @DisplayName("Naming Conventions - Introspection Fields Ignored")
    void testNamingConventionsIntrospectionFieldsIgnored() {
        String query = "query { __schema { types { name } } }";
        Document document = parser.parseDocument(query);
        LintContext context = new LintContext(document, config);
        LintResult result = new LintResult();
        
        styleRule.lint(context, result);
        
        // Should not have warnings for introspection fields
        assertFalse(result.hasWarnings());
        assertFalse(result.hasErrors());
    }
    
    @Test
    @DisplayName("Naming Conventions - Arguments")
    void testNamingConventionsArguments() {
        String query = "query { user(id: 1, user_name: \"test\", emailAddress: \"test@test.com\") { id } }";
        Document document = parser.parseDocument(query);
        LintContext context = new LintContext(document, config);
        LintResult result = new LintResult();
        
        styleRule.lint(context, result);
        
        // Should have warning for non-camelCase argument
        assertTrue(result.hasWarnings());
        assertTrue(result.getWarnings().stream()
            .anyMatch(warning -> warning.getMessage().contains("user_name") && warning.getMessage().contains("camelCase")));
    }
    
    @Test
    @DisplayName("Naming Conventions - Directives")
    void testNamingConventionsDirectives() {
        String query = "query { user @include(if: true) @skip_condition(if: false) { id } }";
        Document document = parser.parseDocument(query);
        LintContext context = new LintContext(document, config);
        LintResult result = new LintResult();
        
        styleRule.lint(context, result);
        
        // Should have warning for non-camelCase directive
        assertTrue(result.hasWarnings());
        assertTrue(result.getWarnings().stream()
            .anyMatch(warning -> warning.getMessage().contains("skip_condition") && warning.getMessage().contains("camelCase")));
    }
    
    @Test
    @DisplayName("Consistent Spacing - Multiple Spaces")
    void testConsistentSpacingMultipleSpaces() {
        String query = "query  {  user  {  id  name  }  }";
        Document document = parser.parseDocument(query);
        LintContext context = new LintContext(document, config);
        LintResult result = new LintResult();
        
        styleRule.lint(context, result);
        
        // Should have info about multiple spaces
        assertTrue(result.hasInfo());
        assertTrue(result.getInfo().stream()
            .anyMatch(info -> info.getMessage().contains("Multiple consecutive spaces")));
    }
    
    @Test
    @DisplayName("Consistent Spacing - Missing Spaces Around Colon")
    void testConsistentSpacingMissingSpacesAroundColon() {
        String query = "query($id:ID!) { user(id:$id) { id } }";
        Document document = parser.parseDocument(query);
        LintContext context = new LintContext(document, config);
        LintResult result = new LintResult();
        
        styleRule.lint(context, result);
        
        // Should have warning about missing spaces around colon
        assertTrue(result.hasWarnings());
        assertTrue(result.getWarnings().stream()
            .anyMatch(warning -> warning.getMessage().contains("Missing spaces around colon")));
    }
    
    @Test
    @DisplayName("Indentation - Inconsistent Indentation")
    void testIndentationInconsistentIndentation() {
        String query = "query {\n user {\n  id\n   name\n }\n}";
        Document document = parser.parseDocument(query);
        LintContext context = new LintContext(document, config);
        LintResult result = new LintResult();
        
        styleRule.lint(context, result);
        
        // Should have warning about inconsistent indentation
        assertTrue(result.hasWarnings());
        assertTrue(result.getWarnings().stream()
            .anyMatch(warning -> warning.getMessage().contains("Inconsistent indentation")));
    }
    
    @Test
    @DisplayName("Indentation - Excessive Indentation Level")
    void testIndentationExcessiveIndentationLevel() {
        String query = "query {\n  user {\n    posts {\n      comments {\n        author {\n          profile {\n            bio {\n              content\n            }\n          }\n        }\n      }\n    }\n  }\n}";
        Document document = parser.parseDocument(query);
        LintContext context = new LintContext(document, config);
        LintResult result = new LintResult();
        
        styleRule.lint(context, result);
        
        // Should have warning about excessive indentation level
        assertTrue(result.hasWarnings());
        assertTrue(result.getWarnings().stream()
            .anyMatch(warning -> warning.getMessage().contains("Indentation level") && warning.getMessage().contains("exceeds")));
    }
    
    @Test
    @DisplayName("Line Length - Exceeds Limit")
    void testLineLengthExceedsLimit() {
        String longLine = "query { user { id name email phone address city state country zipCode dateOfBirth socialSecurityNumber } }";
        Document document = parser.parseDocument(longLine);
        LintContext context = new LintContext(document, config);
        LintResult result = new LintResult();
        
        styleRule.lint(context, result);
        
        // Should have warning about line length
        assertTrue(result.hasWarnings());
        assertTrue(result.getWarnings().stream()
            .anyMatch(warning -> warning.getMessage().contains("Line") && warning.getMessage().contains("length") && warning.getMessage().contains("exceeds")));
    }
    
    @Test
    @DisplayName("Field Naming Consistency - Underscores")
    void testFieldNamingConsistencyUnderscores() {
        String query = "query { user { user_id user_name email_address phone_number } }";
        Document document = parser.parseDocument(query);
        LintContext context = new LintContext(document, config);
        LintResult result = new LintResult();
        
        styleRule.lint(context, result);
        
        // Should have info about underscores
        assertTrue(result.hasInfo());
        assertEquals(4, result.getInfoCount());
        assertTrue(result.getInfo().stream()
            .allMatch(info -> info.getMessage().contains("contains underscores") && info.getMessage().contains("camelCase")));
    }
    
    @Test
    @DisplayName("Field Naming Consistency - Consecutive Uppercase")
    void testFieldNamingConsistencyConsecutiveUppercase() {
        String query = "query { user { userID userName emailAddress phoneNumber } }";
        Document document = parser.parseDocument(query);
        LintContext context = new LintContext(document, config);
        LintResult result = new LintResult();
        
        styleRule.lint(context, result);
        
        // Should have info about consecutive uppercase letters
        assertTrue(result.hasInfo());
        assertTrue(result.getInfo().stream()
            .anyMatch(info -> info.getMessage().contains("consecutive uppercase letters") && info.getMessage().contains("camelCase")));
    }
    
    @Test
    @DisplayName("Field Naming Consistency - Reserved Words")
    void testFieldNamingConsistencyReservedWords() {
        String query = "query { user { query mutation subscription fragment type interface } }";
        Document document = parser.parseDocument(query);
        LintContext context = new LintContext(document, config);
        LintResult result = new LintResult();
        
        styleRule.lint(context, result);
        
        // Should have warnings about reserved words
        assertTrue(result.hasWarnings());
        assertTrue(result.getWarnings().stream()
            .anyMatch(warning -> warning.getMessage().contains("reserved word") && warning.getMessage().contains("alternative naming")));
    }
    
    @Test
    @DisplayName("Fragment Naming - PascalCase")
    void testFragmentNamingPascalCase() {
        String query = "fragment UserFields on User { id name } query { user { ...UserFields } }";
        Document document = parser.parseDocument(query);
        LintContext context = new LintContext(document, config);
        LintResult result = new LintResult();
        
        styleRule.lint(context, result);
        
        // Should not have warnings for proper PascalCase fragment
        assertFalse(result.hasWarnings());
        assertFalse(result.hasErrors());
    }
    
    @Test
    @DisplayName("Fragment Naming - Non-PascalCase")
    void testFragmentNamingNonPascalCase() {
        String query = "fragment userFields on User { id name } query { user { ...userFields } }";
        Document document = parser.parseDocument(query);
        LintContext context = new LintContext(document, config);
        LintResult result = new LintResult();
        
        styleRule.lint(context, result);
        
        // Should have warning for non-PascalCase fragment
        assertTrue(result.hasWarnings());
        assertTrue(result.getWarnings().stream()
            .anyMatch(warning -> warning.getMessage().contains("userFields") && warning.getMessage().contains("PascalCase")));
    }
    
    @Test
    @DisplayName("Operation Naming - PascalCase")
    void testOperationNamingPascalCase() {
        String query = "query GetUser { user { id name } }";
        Document document = parser.parseDocument(query);
        LintContext context = new LintContext(document, config);
        LintResult result = new LintResult();
        
        styleRule.lint(context, result);
        
        // Should not have warnings for proper PascalCase operation
        assertFalse(result.hasWarnings());
        assertFalse(result.hasErrors());
    }
    
    @Test
    @DisplayName("Operation Naming - Non-PascalCase")
    void testOperationNamingNonPascalCase() {
        String query = "query getUser { user { id name } }";
        Document document = parser.parseDocument(query);
        LintContext context = new LintContext(document, config);
        LintResult result = new LintResult();
        
        styleRule.lint(context, result);
        
        // Should have warning for non-PascalCase operation
        assertTrue(result.hasWarnings());
        assertTrue(result.getWarnings().stream()
            .anyMatch(warning -> warning.getMessage().contains("getUser") && warning.getMessage().contains("PascalCase")));
    }
    
    @Test
    @DisplayName("Configuration - Disable CamelCase Enforcement")
    void testConfigurationDisableCamelCaseEnforcement() {
        config.setValue("enforceCamelCase", false);
        
        String query = "query { user { ID Name email_address } }";
        Document document = parser.parseDocument(query);
        LintContext context = new LintContext(document, config);
        LintResult result = new LintResult();
        
        styleRule.lint(context, result);
        
        // Should not have warnings when camelCase enforcement is disabled
        assertFalse(result.hasWarnings());
        assertFalse(result.hasErrors());
    }
    
    @Test
    @DisplayName("Configuration - Custom Line Length")
    void testConfigurationCustomLineLength() {
        config.setValue("maxLineLength", 50);
        
        String query = "query { user { id name email phone address } }";
        Document document = parser.parseDocument(query);
        LintContext context = new LintContext(document, config);
        LintResult result = new LintResult();
        
        styleRule.lint(context, result);
        
        // Should have warning about line length with custom limit
        assertTrue(result.hasWarnings());
        assertTrue(result.getWarnings().stream()
            .anyMatch(warning -> warning.getMessage().contains("50")));
    }
    
    @Test
    @DisplayName("Configuration - Disable Spacing Enforcement")
    void testConfigurationDisableSpacingEnforcement() {
        config.setValue("enforceConsistentSpacing", false);
        
        String query = "query  {  user  {  id  }  }";
        Document document = parser.parseDocument(query);
        LintContext context = new LintContext(document, config);
        LintResult result = new LintResult();
        
        styleRule.lint(context, result);
        
        // Should not have info about spacing when enforcement is disabled
        assertFalse(result.hasInfo());
    }
    
    @Test
    @DisplayName("Configuration - Disable Indentation Enforcement")
    void testConfigurationDisableIndentationEnforcement() {
        config.setValue("enforceIndentation", false);
        
        String query = "query {\n user {\n  id\n   name\n }\n}";
        Document document = parser.parseDocument(query);
        LintContext context = new LintContext(document, config);
        LintResult result = new LintResult();
        
        styleRule.lint(context, result);
        
        // Should not have warnings about indentation when enforcement is disabled
        assertFalse(result.hasWarnings());
    }
    
    @Test
    @DisplayName("Complex Query - Multiple Style Issues")
    void testComplexQueryMultipleStyleIssues() {
        String query = "query  get_user  {  user  (  user_id  :  1  )  {  user_ID  user_name  email_address  @include  (  if  :  true  )  }  }";
        Document document = parser.parseDocument(query);
        LintContext context = new LintContext(document, config);
        LintResult result = new LintResult();
        
        styleRule.lint(context, result);
        
        // Should have multiple style issues
        assertTrue(result.hasWarnings());
        assertTrue(result.hasInfo());
        
        // Check for various style issues
        assertTrue(result.getWarnings().stream()
            .anyMatch(warning -> warning.getMessage().contains("get_user") && warning.getMessage().contains("PascalCase")));
        assertTrue(result.getWarnings().stream()
            .anyMatch(warning -> warning.getMessage().contains("user_ID") && warning.getMessage().contains("camelCase")));
        assertTrue(result.getWarnings().stream()
            .anyMatch(warning -> warning.getMessage().contains("user_name") && warning.getMessage().contains("camelCase")));
        assertTrue(result.getWarnings().stream()
            .anyMatch(warning -> warning.getMessage().contains("email_address") && warning.getMessage().contains("camelCase")));
        assertTrue(result.getWarnings().stream()
            .anyMatch(warning -> warning.getMessage().contains("user_id") && warning.getMessage().contains("camelCase")));
    }
    
    @Test
    @DisplayName("Mutation - Style Validation")
    void testMutationStyleValidation() {
        String mutation = "mutation CreateUser($userData: UserInput!) { createUser(input: $userData) { id name email } }";
        Document document = parser.parseDocument(mutation);
        LintContext context = new LintContext(document, config);
        LintResult result = new LintResult();
        
        styleRule.lint(context, result);
        
        // Should not have warnings for proper style
        assertFalse(result.hasWarnings());
        assertFalse(result.hasErrors());
    }
    
    @Test
    @DisplayName("Subscription - Style Validation")
    void testSubscriptionStyleValidation() {
        String subscription = "subscription UserUpdates { userUpdates { id name email } }";
        Document document = parser.parseDocument(subscription);
        LintContext context = new LintContext(document, config);
        LintResult result = new LintResult();
        
        styleRule.lint(context, result);
        
        // Should not have warnings for proper style
        assertFalse(result.hasWarnings());
        assertFalse(result.hasErrors());
    }
    
    @Test
    @DisplayName("Fragment with Inline Fragment - Style Validation")
    void testFragmentWithInlineFragmentStyleValidation() {
        String query = "fragment UserFields on User { id name ... on AdminUser { permissions } } query { user { ...UserFields } }";
        Document document = parser.parseDocument(query);
        LintContext context = new LintContext(document, config);
        LintResult result = new LintResult();
        
        styleRule.lint(context, result);
        
        // Should not have warnings for proper style
        assertFalse(result.hasWarnings());
        assertFalse(result.hasErrors());
    }
} 