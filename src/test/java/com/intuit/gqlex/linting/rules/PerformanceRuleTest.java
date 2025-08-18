package com.intuit.gqlex.linting.rules;

import com.intuit.gqlex.linting.core.LintContext;
import com.intuit.gqlex.linting.core.LintLevel;
import com.intuit.gqlex.linting.core.LintResult;
import com.intuit.gqlex.linting.config.LintConfig;
import graphql.language.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PerformanceRuleTest {

    private PerformanceRule performanceRule;
    private LintContext testContext;
    private LintResult testResult;

    @BeforeEach
    void setUp() {
        performanceRule = new PerformanceRule();
        testResult = new LintResult();
    }

    @Test
    void testPerformanceRuleConstructor() {
        assertEquals("PERFORMANCE", performanceRule.getName());
        assertEquals("Identifies performance optimization opportunities", performanceRule.getDescription());
        assertEquals(LintLevel.WARNING, performanceRule.getLevel());
    }

    @Test
    void testDeepNestingDetection() {
        // Create a very deep nested structure
        Field level1 = Field.newField("level1")
            .selectionSet(SelectionSet.newSelectionSet()
                .selection(Field.newField("level2")
                    .selectionSet(SelectionSet.newSelectionSet()
                        .selection(Field.newField("level3")
                            .selectionSet(SelectionSet.newSelectionSet()
                                .selection(Field.newField("level4")
                                    .selectionSet(SelectionSet.newSelectionSet()
                                        .selection(Field.newField("level5")
                                            .selectionSet(SelectionSet.newSelectionSet()
                                                .selection(Field.newField("level6")
                                                    .selectionSet(SelectionSet.newSelectionSet()
                                                        .selection(Field.newField("level7")
                                                            .selectionSet(SelectionSet.newSelectionSet()
                                                                .selection(Field.newField("level8").build())
                                                                .build())
                                                            .build())
                                                        .build())
                                                    .build())
                                                .build())
                                            .build())
                                        .build())
                                    .build())
                                .build())
                            .build())
                        .build())
                    .build())
                .build())
            .build();
        
        Document document = Document.newDocument()
            .definition(OperationDefinition.newOperationDefinition()
                .operation(OperationDefinition.Operation.QUERY)
                .selectionSet(SelectionSet.newSelectionSet()
                    .selection(level1)
                    .build())
                .build())
            .build();

        testContext = new LintContext(document, new LintConfig());
        
        // Set configuration to trigger depth validation
        testContext.getConfig().setValue("maxDepth", 5);
        
        performanceRule.lint(testContext, testResult);
        
        // Debug output
        System.out.println("PerformanceRule deep nesting test - Issues found: " + testResult.getErrors().size() + " errors, " + 
                          testResult.getWarnings().size() + " warnings, " + testResult.getInfo().size() + " infos");
        
        assertTrue(testResult.hasIssues());
    }

    @Test
    void testLargeSelectionSetDetection() {
        // Create a very large selection set
        List<Selection> selections = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            selections.add(Field.newField("field" + i).build());
        }
        
        Document document = Document.newDocument()
            .definition(OperationDefinition.newOperationDefinition()
                .operation(OperationDefinition.Operation.QUERY)
                .selectionSet(SelectionSet.newSelectionSet()
                    .selections(selections)
                    .build())
                .build())
            .build();

        testContext = new LintContext(document, new LintConfig());
        
        // Set configuration to trigger field count validation
        testContext.getConfig().setValue("maxFields", 50);
        
        performanceRule.lint(testContext, testResult);
        
        // Debug output
        System.out.println("PerformanceRule large selection test - Issues found: " + testResult.getErrors().size() + " errors, " + 
                          testResult.getWarnings().size() + " warnings, " + testResult.getInfo().size() + " infos");
        
        assertTrue(testResult.hasIssues());
    }

    @Test
    void testFragmentOptimization() {
        // Create a document with fragments
        FragmentDefinition fragment = FragmentDefinition.newFragmentDefinition()
            .name("UserFragment")
            .typeCondition(TypeName.newTypeName("User").build())
            .selectionSet(SelectionSet.newSelectionSet()
                .selection(Field.newField("id").build())
                .selection(Field.newField("name").build())
                .selection(Field.newField("email").build())
                .selection(Field.newField("phone").build())
                .selection(Field.newField("address").build())
                .build())
            .build();
        
        FragmentSpread fragmentSpread1 = FragmentSpread.newFragmentSpread("UserFragment").build();
        FragmentSpread fragmentSpread2 = FragmentSpread.newFragmentSpread("UserFragment").build();
        FragmentSpread fragmentSpread3 = FragmentSpread.newFragmentSpread("UserFragment").build();
        FragmentSpread fragmentSpread4 = FragmentSpread.newFragmentSpread("UserFragment").build();
        
        Document document = Document.newDocument()
            .definition(fragment)
            .definition(OperationDefinition.newOperationDefinition()
                .operation(OperationDefinition.Operation.QUERY)
                .selectionSet(SelectionSet.newSelectionSet()
                    .selection(fragmentSpread1)
                    .selection(fragmentSpread2)
                    .selection(fragmentSpread3)
                    .selection(fragmentSpread4)
                    .build())
                .build())
            .build();

        testContext = new LintContext(document, new LintConfig());
        
        // Set configuration to trigger fragment validation
        testContext.getConfig().setValue("enforceFragmentUsage", true);
        
        performanceRule.lint(testContext, testResult);
        
        // Debug output
        System.out.println("PerformanceRule fragment test - Issues found: " + testResult.getErrors().size() + " errors, " + 
                          testResult.getWarnings().size() + " warnings, " + testResult.getInfo().size() + " infos");
        
        assertTrue(testResult.hasIssues());
    }

    @Test
    void testEdgeCases() {
        Document emptyDoc = Document.newDocument().build();
        testContext = new LintContext(emptyDoc, new LintConfig());
        assertDoesNotThrow(() -> performanceRule.lint(testContext, testResult));
    }
}
