package com.intuit.gqlex.linting.rules;

import com.intuit.gqlex.linting.core.LintContext;
import com.intuit.gqlex.linting.core.LintLevel;
import com.intuit.gqlex.linting.core.LintResult;
import com.intuit.gqlex.linting.config.LintConfig;
import graphql.language.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecurityRuleTest {

    private SecurityRule securityRule;
    private LintContext testContext;
    private LintResult testResult;

    @BeforeEach
    void setUp() {
        securityRule = new SecurityRule();
        testResult = new LintResult();
    }

    @Test
    void testSecurityRuleConstructor() {
        assertEquals("SECURITY", securityRule.getName());
        assertEquals("Identifies security vulnerabilities and risks", securityRule.getDescription());
        assertEquals(LintLevel.WARNING, securityRule.getLevel());
    }

    @Test
    void testIntrospectionFieldDetection() {
        Field schemaField = Field.newField("__schema").build();
        Field typeField = Field.newField("__type").build();
        
        Document document = Document.newDocument()
            .definition(OperationDefinition.newOperationDefinition()
                .operation(OperationDefinition.Operation.QUERY)
                .selectionSet(SelectionSet.newSelectionSet()
                    .selection(schemaField)
                    .selection(typeField)
                    .build())
                .build())
            .build();

        testContext = new LintContext(document, new LintConfig());
        securityRule.lint(testContext, testResult);
        
        assertTrue(testResult.hasIssues());
        // The rule detects multiple security issues, not just the 2 fields
        assertTrue(testResult.getErrors().size() >= 2);
    }

    @Test
    void testSensitiveFieldDetection() {
        Field passwordField = Field.newField("password").build();
        Field ssnField = Field.newField("ssn").build();
        
        Document document = Document.newDocument()
            .definition(OperationDefinition.newOperationDefinition()
                .operation(OperationDefinition.Operation.QUERY)
                .selectionSet(SelectionSet.newSelectionSet()
                    .selection(passwordField)
                    .selection(ssnField)
                    .build())
                .build())
            .build();

        testContext = new LintContext(document, new LintConfig());
        securityRule.lint(testContext, testResult);
        
        assertTrue(testResult.hasIssues());
        // The rule detects multiple security issues, not just the 2 fields
        assertTrue(testResult.getWarnings().size() >= 2);
    }

    @Test
    void testEdgeCases() {
        Document emptyDoc = Document.newDocument().build();
        testContext = new LintContext(emptyDoc, new LintConfig());
        assertDoesNotThrow(() -> securityRule.lint(testContext, testResult));
    }
}
