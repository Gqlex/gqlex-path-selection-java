package com.intuit.gqlex.linting.rules;

import com.intuit.gqlex.linting.core.LintLevel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
@Tag("linting")
class LintRuleTest {

    @Test
    void testLintRuleConstructor() {
        TestLintRule rule = new TestLintRule("TEST_RULE", LintLevel.WARNING, "Test description");
        
        assertEquals("TEST_RULE", rule.getName());
        assertEquals(LintLevel.WARNING, rule.getLevel());
        assertEquals("Test description", rule.getDescription());
        assertEquals("TEST_CATEGORY", rule.getCategory());
    }

    @Test
    void testLintRuleConstructorWithoutLevel() {
        TestLintRule rule = new TestLintRule("TEST_RULE", "Test description");
        
        assertEquals("TEST_RULE", rule.getName());
        assertEquals(LintLevel.WARNING, rule.getLevel()); // Default level
        assertEquals("Test description", rule.getDescription());
    }

    @Test
    void testLintRuleToString() {
        TestLintRule rule = new TestLintRule("TEST_RULE", LintLevel.ERROR, "Test description");
        String result = rule.toString();
        
        assertTrue(result.contains("TEST_RULE"));
        assertTrue(result.contains("ERROR"));
        assertTrue(result.contains("TEST_CATEGORY"));
        assertTrue(result.contains("Test description"));
    }

    @Test
    void testLintRuleEquals() {
        TestLintRule rule1 = new TestLintRule("TEST_RULE", LintLevel.WARNING, "Test description");
        TestLintRule rule2 = new TestLintRule("TEST_RULE", LintLevel.WARNING, "Test description");
        TestLintRule rule3 = new TestLintRule("DIFFERENT", LintLevel.WARNING, "Test description");
        
        assertEquals(rule1, rule1); // Same object
        assertEquals(rule1, rule2); // Same values
        assertNotEquals(rule1, rule3); // Different values
        assertNotEquals(rule1, null); // Not null
        assertNotEquals(rule1, "string"); // Different type
    }

    @Test
    void testLintRuleHashCode() {
        TestLintRule rule1 = new TestLintRule("TEST_RULE", LintLevel.WARNING, "Test description");
        TestLintRule rule2 = new TestLintRule("TEST_RULE", LintLevel.WARNING, "Test description");
        
        assertEquals(rule1.hashCode(), rule2.hashCode());
        assertEquals(rule1.hashCode(), rule1.hashCode()); // Consistency
    }

    @Test
    void testLintRuleEdgeCases() {
        TestLintRule emptyRule = new TestLintRule("", LintLevel.INFO, "");
        assertEquals("", emptyRule.getName());
        assertEquals("", emptyRule.getDescription());
        
        TestLintRule specialRule = new TestLintRule("RULE@#$%", LintLevel.ERROR, "Description with @#$%");
        assertEquals("RULE@#$%", specialRule.getName());
        assertEquals("Description with @#$%", specialRule.getDescription());
    }

    private static class TestLintRule extends LintRule {
        public TestLintRule(String name, LintLevel level, String description) {
            super(name, level, description);
        }

        public TestLintRule(String name, String description) {
            super(name, description);
        }

        @Override
        public void lint(com.intuit.gqlex.linting.core.LintContext context, com.intuit.gqlex.linting.core.LintResult result) {
            // Test implementation - do nothing
        }

        @Override
        public String getCategory() {
            return "TEST_CATEGORY";
        }
    }
}

