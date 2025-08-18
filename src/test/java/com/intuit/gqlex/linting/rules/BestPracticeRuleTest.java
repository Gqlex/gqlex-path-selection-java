package com.intuit.gqlex.linting.rules;

import com.intuit.gqlex.linting.core.LintContext;
import com.intuit.gqlex.linting.core.LintLevel;
import com.intuit.gqlex.linting.core.LintResult;
import com.intuit.gqlex.linting.config.LintConfig;
import graphql.language.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BestPracticeRuleTest {

    private BestPracticeRule bestPracticeRule;
    private LintContext testContext;
    private LintResult testResult;

    @BeforeEach
    void setUp() {
        bestPracticeRule = new BestPracticeRule();
        testResult = new LintResult();
    }

    @Test
    void testBestPracticeRuleConstructor() {
        assertEquals("BEST_PRACTICE", bestPracticeRule.getName());
        assertEquals("Enforces GraphQL best practices", bestPracticeRule.getDescription());
        assertEquals(LintLevel.WARNING, bestPracticeRule.getLevel());
    }

    @Test
    void testFragmentNamingConvention() {
        FragmentDefinition fragment = FragmentDefinition.newFragmentDefinition()
            .name("userFragment")
            .typeCondition(TypeName.newTypeName("User").build())
            .selectionSet(SelectionSet.newSelectionSet()
                .selection(Field.newField("id").build())
                .build())
            .build();
        
        Document document = Document.newDocument()
            .definition(fragment)
            .definition(OperationDefinition.newOperationDefinition()
                .operation(OperationDefinition.Operation.QUERY)
                .selectionSet(SelectionSet.newSelectionSet()
                    .selection(FragmentSpread.newFragmentSpread("userFragment").build())
                    .build())
                .build())
            .build();

        testContext = new LintContext(document, new LintConfig());
        bestPracticeRule.lint(testContext, testResult);
        
        assertTrue(testResult.hasIssues());
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
        bestPracticeRule.lint(testContext, testResult);
        
        assertTrue(testResult.hasIssues());
    }

    @Test
    void testEdgeCases() {
        Document emptyDoc = Document.newDocument().build();
        testContext = new LintContext(emptyDoc, new LintConfig());
        assertDoesNotThrow(() -> bestPracticeRule.lint(testContext, testResult));
    }
}
