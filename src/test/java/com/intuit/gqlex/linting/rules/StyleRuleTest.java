package com.intuit.gqlex.linting.rules;

import com.intuit.gqlex.linting.core.LintContext;
import com.intuit.gqlex.linting.core.LintLevel;
import com.intuit.gqlex.linting.core.LintResult;
import com.intuit.gqlex.linting.config.LintConfig;
import graphql.language.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StyleRuleTest {

    private StyleRule styleRule;
    private LintContext testContext;
    private LintResult testResult;

    @BeforeEach
    void setUp() {
        styleRule = new StyleRule();
        testResult = new LintResult();
    }

    @Test
    void testStyleRuleConstructor() {
        assertEquals("STYLE", styleRule.getName());
        assertEquals("Enforces GraphQL code style conventions", styleRule.getDescription());
        assertEquals(LintLevel.WARNING, styleRule.getLevel());
    }

    @Test
    void testFieldNamingConvention() {
        Field field = Field.newField("user_name").build();
        
        Document document = Document.newDocument()
            .definition(OperationDefinition.newOperationDefinition()
                .operation(OperationDefinition.Operation.QUERY)
                .selectionSet(SelectionSet.newSelectionSet()
                    .selection(field)
                    .build())
                .build())
            .build();

        testContext = new LintContext(document, new LintConfig());
        styleRule.lint(testContext, testResult);
        
        assertTrue(testResult.hasIssues());
    }

    @Test
    void testOperationNamingConvention() {
        OperationDefinition operation = OperationDefinition.newOperationDefinition()
            .operation(OperationDefinition.Operation.QUERY)
            .name("get_user_data")
            .selectionSet(SelectionSet.newSelectionSet()
                .selection(Field.newField("users").build())
                .build())
            .build();
        
        Document document = Document.newDocument()
            .definition(operation)
            .build();

        testContext = new LintContext(document, new LintConfig());
        styleRule.lint(testContext, testResult);
        
        assertTrue(testResult.hasIssues());
    }

    @Test
    void testEdgeCases() {
        Document emptyDoc = Document.newDocument().build();
        testContext = new LintContext(emptyDoc, new LintConfig());
        assertDoesNotThrow(() -> styleRule.lint(testContext, testResult));
    }
}
