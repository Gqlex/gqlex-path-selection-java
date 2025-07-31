package com.intuit.gqlex.linting.config;

import com.intuit.gqlex.linting.core.LintLevel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.Map;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LintPreset Tests")
public class LintPresetTest {
    
    @Test
    @DisplayName("Strict Preset - All Rules at ERROR Level")
    void testStrictPreset() {
        LintConfig config = LintPreset.strict();
        
        // Test performance settings
        assertEquals(100, config.getValue("maxLineLength", Integer.class));
        assertEquals(3, config.getValue("maxDepth", Integer.class));
        assertEquals(30, config.getValue("maxFields", Integer.class));
        assertEquals(2, config.getValue("maxSecurityDepth", Integer.class));
        assertEquals(10, config.getValue("maxArguments", Integer.class));
        assertEquals(5, config.getValue("maxFragments", Integer.class));
        
        // Test security settings
        Set<String> sensitiveFields = config.getValue("sensitiveFields", Set.class);
        assertTrue(sensitiveFields.contains("password"));
        assertTrue(sensitiveFields.contains("ssn"));
        assertTrue(sensitiveFields.contains("creditCard"));
        assertTrue(sensitiveFields.contains("apiKey"));
        assertTrue(sensitiveFields.contains("token"));
        assertTrue(sensitiveFields.contains("secret"));
        
        assertTrue(config.getValue("forbiddenIntrospection", Boolean.class));
        assertEquals(50, config.getValue("maxQueryComplexity", Integer.class));
        
        // Test style settings
        assertTrue(config.getValue("enforceCamelCase", Boolean.class));
        assertTrue(config.getValue("enforceConsistentSpacing", Boolean.class));
        assertTrue(config.getValue("enforceIndentation", Boolean.class));
        assertEquals(4, config.getValue("maxIndentationLevel", Integer.class));
        
        // Test rule configurations - all should be ERROR level
        assertEquals(LintLevel.ERROR, config.getRuleConfig("STYLE").getLevel());
        assertEquals(LintLevel.ERROR, config.getRuleConfig("BEST_PRACTICE").getLevel());
        assertEquals(LintLevel.ERROR, config.getRuleConfig("PERFORMANCE").getLevel());
        assertEquals(LintLevel.ERROR, config.getRuleConfig("SECURITY").getLevel());
        
        // Test rule enabled status
        assertTrue(config.getRuleConfig("STYLE").isEnabled());
        assertTrue(config.getRuleConfig("BEST_PRACTICE").isEnabled());
        assertTrue(config.getRuleConfig("PERFORMANCE").isEnabled());
        assertTrue(config.getRuleConfig("SECURITY").isEnabled());
    }
    
    @Test
    @DisplayName("Relaxed Preset - Most Rules at INFO Level")
    void testRelaxedPreset() {
        LintConfig config = LintPreset.relaxed();
        
        // Test performance settings - more permissive
        assertEquals(120, config.getValue("maxLineLength", Integer.class));
        assertEquals(7, config.getValue("maxDepth", Integer.class));
        assertEquals(80, config.getValue("maxFields", Integer.class));
        assertEquals(4, config.getValue("maxSecurityDepth", Integer.class));
        assertEquals(20, config.getValue("maxArguments", Integer.class));
        assertEquals(10, config.getValue("maxFragments", Integer.class));
        
        // Test security settings - more permissive
        Set<String> sensitiveFields = config.getValue("sensitiveFields", Set.class);
        assertTrue(sensitiveFields.contains("password"));
        assertTrue(sensitiveFields.contains("ssn"));
        assertTrue(sensitiveFields.contains("creditCard"));
        assertEquals(3, sensitiveFields.size()); // Only 3 sensitive fields
        
        assertFalse(config.getValue("forbiddenIntrospection", Boolean.class));
        assertEquals(100, config.getValue("maxQueryComplexity", Integer.class));
        
        // Test style settings - more permissive
        assertFalse(config.getValue("enforceCamelCase", Boolean.class));
        assertFalse(config.getValue("enforceConsistentSpacing", Boolean.class));
        assertFalse(config.getValue("enforceIndentation", Boolean.class));
        assertEquals(8, config.getValue("maxIndentationLevel", Integer.class));
        
        // Test rule configurations - mostly INFO level
        assertEquals(LintLevel.INFO, config.getRuleConfig("STYLE").getLevel());
        assertEquals(LintLevel.WARNING, config.getRuleConfig("BEST_PRACTICE").getLevel());
        assertEquals(LintLevel.INFO, config.getRuleConfig("PERFORMANCE").getLevel());
        assertEquals(LintLevel.WARNING, config.getRuleConfig("SECURITY").getLevel());
        
        // Test rule enabled status
        assertTrue(config.getRuleConfig("STYLE").isEnabled());
        assertTrue(config.getRuleConfig("BEST_PRACTICE").isEnabled());
        assertTrue(config.getRuleConfig("PERFORMANCE").isEnabled());
        assertTrue(config.getRuleConfig("SECURITY").isEnabled());
    }
    
    @Test
    @DisplayName("Performance Preset - Performance Rules at ERROR Level")
    void testPerformancePreset() {
        LintConfig config = LintPreset.performance();
        
        // Test performance settings - strict
        assertEquals(80, config.getValue("maxLineLength", Integer.class));
        assertEquals(4, config.getValue("maxDepth", Integer.class));
        assertEquals(40, config.getValue("maxFields", Integer.class));
        assertEquals(3, config.getValue("maxSecurityDepth", Integer.class));
        assertEquals(8, config.getValue("maxArguments", Integer.class));
        assertEquals(3, config.getValue("maxFragments", Integer.class));
        assertEquals(30, config.getValue("maxQueryComplexity", Integer.class));
        
        // Test performance-specific settings
        assertTrue(config.getValue("enforceFragmentUsage", Boolean.class));
        assertTrue(config.getValue("enforceFieldSelection", Boolean.class));
        assertTrue(config.getValue("enforceAliasUsage", Boolean.class));
        assertEquals(15, config.getValue("maxSelectionSetSize", Integer.class));
        
        // Test security settings - moderate
        Set<String> sensitiveFields = config.getValue("sensitiveFields", Set.class);
        assertTrue(sensitiveFields.contains("password"));
        assertTrue(sensitiveFields.contains("ssn"));
        assertTrue(sensitiveFields.contains("creditCard"));
        assertTrue(sensitiveFields.contains("apiKey"));
        assertEquals(4, sensitiveFields.size());
        
        assertTrue(config.getValue("forbiddenIntrospection", Boolean.class));
        
        // Test style settings - moderate
        assertTrue(config.getValue("enforceCamelCase", Boolean.class));
        assertTrue(config.getValue("enforceConsistentSpacing", Boolean.class));
        assertTrue(config.getValue("enforceIndentation", Boolean.class));
        assertEquals(4, config.getValue("maxIndentationLevel", Integer.class));
        
        // Test rule configurations - performance rules at ERROR level
        assertEquals(LintLevel.INFO, config.getRuleConfig("STYLE").getLevel());
        assertEquals(LintLevel.WARNING, config.getRuleConfig("BEST_PRACTICE").getLevel());
        assertEquals(LintLevel.ERROR, config.getRuleConfig("PERFORMANCE").getLevel());
        assertEquals(LintLevel.WARNING, config.getRuleConfig("SECURITY").getLevel());
        
        // Test rule enabled status
        assertTrue(config.getRuleConfig("STYLE").isEnabled());
        assertTrue(config.getRuleConfig("BEST_PRACTICE").isEnabled());
        assertTrue(config.getRuleConfig("PERFORMANCE").isEnabled());
        assertTrue(config.getRuleConfig("SECURITY").isEnabled());
    }
    
    @Test
    @DisplayName("Security Preset - Security Rules at ERROR Level")
    void testSecurityPreset() {
        LintConfig config = LintPreset.security();
        
        // Test security settings - very strict
        assertEquals(80, config.getValue("maxLineLength", Integer.class));
        assertEquals(2, config.getValue("maxDepth", Integer.class));
        assertEquals(20, config.getValue("maxFields", Integer.class));
        assertEquals(2, config.getValue("maxSecurityDepth", Integer.class));
        assertEquals(5, config.getValue("maxArguments", Integer.class));
        assertEquals(2, config.getValue("maxFragments", Integer.class));
        assertEquals(20, config.getValue("maxQueryComplexity", Integer.class));
        
        // Test security-specific settings
        Set<String> sensitiveFields = config.getValue("sensitiveFields", Set.class);
        assertTrue(sensitiveFields.contains("password"));
        assertTrue(sensitiveFields.contains("ssn"));
        assertTrue(sensitiveFields.contains("creditCard"));
        assertTrue(sensitiveFields.contains("apiKey"));
        assertTrue(sensitiveFields.contains("token"));
        assertTrue(sensitiveFields.contains("secret"));
        assertTrue(sensitiveFields.contains("privateKey"));
        assertTrue(sensitiveFields.contains("authToken"));
        assertEquals(8, sensitiveFields.size());
        
        assertTrue(config.getValue("forbiddenIntrospection", Boolean.class));
        assertTrue(config.getValue("enforceInputValidation", Boolean.class));
        assertTrue(config.getValue("enforceAccessControl", Boolean.class));
        
        Set<String> forbiddenDirectives = config.getValue("forbiddenDirectives", Set.class);
        assertTrue(forbiddenDirectives.contains("auth"));
        assertTrue(forbiddenDirectives.contains("admin"));
        assertTrue(forbiddenDirectives.contains("internal"));
        assertEquals(3, forbiddenDirectives.size());
        
        // Test performance settings - moderate
        assertTrue(config.getValue("enforceFragmentUsage", Boolean.class));
        assertTrue(config.getValue("enforceFieldSelection", Boolean.class));
        assertEquals(10, config.getValue("maxSelectionSetSize", Integer.class));
        
        // Test style settings - moderate
        assertTrue(config.getValue("enforceCamelCase", Boolean.class));
        assertTrue(config.getValue("enforceConsistentSpacing", Boolean.class));
        assertTrue(config.getValue("enforceIndentation", Boolean.class));
        assertEquals(3, config.getValue("maxIndentationLevel", Integer.class));
        
        // Test rule configurations - security rules at ERROR level
        assertEquals(LintLevel.WARNING, config.getRuleConfig("STYLE").getLevel());
        assertEquals(LintLevel.WARNING, config.getRuleConfig("BEST_PRACTICE").getLevel());
        assertEquals(LintLevel.WARNING, config.getRuleConfig("PERFORMANCE").getLevel());
        assertEquals(LintLevel.ERROR, config.getRuleConfig("SECURITY").getLevel());
        
        // Test rule enabled status
        assertTrue(config.getRuleConfig("STYLE").isEnabled());
        assertTrue(config.getRuleConfig("BEST_PRACTICE").isEnabled());
        assertTrue(config.getRuleConfig("PERFORMANCE").isEnabled());
        assertTrue(config.getRuleConfig("SECURITY").isEnabled());
    }
    
    @Test
    @DisplayName("Development Preset - Balanced Configuration")
    void testDevelopmentPreset() {
        LintConfig config = LintPreset.development();
        
        // Test balanced settings for development
        assertEquals(100, config.getValue("maxLineLength", Integer.class));
        assertEquals(5, config.getValue("maxDepth", Integer.class));
        assertEquals(50, config.getValue("maxFields", Integer.class));
        assertEquals(3, config.getValue("maxSecurityDepth", Integer.class));
        assertEquals(15, config.getValue("maxArguments", Integer.class));
        assertEquals(5, config.getValue("maxFragments", Integer.class));
        assertEquals(75, config.getValue("maxQueryComplexity", Integer.class));
        
        // Test development-specific settings
        Set<String> sensitiveFields = config.getValue("sensitiveFields", Set.class);
        assertTrue(sensitiveFields.contains("password"));
        assertTrue(sensitiveFields.contains("ssn"));
        assertTrue(sensitiveFields.contains("creditCard"));
        assertEquals(3, sensitiveFields.size());
        
        assertFalse(config.getValue("forbiddenIntrospection", Boolean.class)); // Allow introspection in development
        assertTrue(config.getValue("enforceFragmentUsage", Boolean.class));
        assertTrue(config.getValue("enforceFieldSelection", Boolean.class));
        assertEquals(20, config.getValue("maxSelectionSetSize", Integer.class));
        
        // Test style settings - helpful but not strict
        assertTrue(config.getValue("enforceCamelCase", Boolean.class));
        assertTrue(config.getValue("enforceConsistentSpacing", Boolean.class));
        assertTrue(config.getValue("enforceIndentation", Boolean.class));
        assertEquals(5, config.getValue("maxIndentationLevel", Integer.class));
        
        // Test rule configurations - mostly WARNING level
        assertEquals(LintLevel.WARNING, config.getRuleConfig("STYLE").getLevel());
        assertEquals(LintLevel.WARNING, config.getRuleConfig("BEST_PRACTICE").getLevel());
        assertEquals(LintLevel.WARNING, config.getRuleConfig("PERFORMANCE").getLevel());
        assertEquals(LintLevel.WARNING, config.getRuleConfig("SECURITY").getLevel());
        
        // Test rule enabled status
        assertTrue(config.getRuleConfig("STYLE").isEnabled());
        assertTrue(config.getRuleConfig("BEST_PRACTICE").isEnabled());
        assertTrue(config.getRuleConfig("PERFORMANCE").isEnabled());
        assertTrue(config.getRuleConfig("SECURITY").isEnabled());
    }
    
    @Test
    @DisplayName("Production Preset - Strict Security and Performance")
    void testProductionPreset() {
        LintConfig config = LintPreset.production();
        
        // Test production settings - strict
        assertEquals(80, config.getValue("maxLineLength", Integer.class));
        assertEquals(4, config.getValue("maxDepth", Integer.class));
        assertEquals(40, config.getValue("maxFields", Integer.class));
        assertEquals(3, config.getValue("maxSecurityDepth", Integer.class));
        assertEquals(10, config.getValue("maxArguments", Integer.class));
        assertEquals(5, config.getValue("maxFragments", Integer.class));
        assertEquals(50, config.getValue("maxQueryComplexity", Integer.class));
        
        // Test production-specific settings
        Set<String> sensitiveFields = config.getValue("sensitiveFields", Set.class);
        assertTrue(sensitiveFields.contains("password"));
        assertTrue(sensitiveFields.contains("ssn"));
        assertTrue(sensitiveFields.contains("creditCard"));
        assertTrue(sensitiveFields.contains("apiKey"));
        assertTrue(sensitiveFields.contains("token"));
        assertTrue(sensitiveFields.contains("secret"));
        assertEquals(6, sensitiveFields.size());
        
        assertTrue(config.getValue("forbiddenIntrospection", Boolean.class));
        assertTrue(config.getValue("enforceFragmentUsage", Boolean.class));
        assertTrue(config.getValue("enforceFieldSelection", Boolean.class));
        assertEquals(15, config.getValue("maxSelectionSetSize", Integer.class));
        assertTrue(config.getValue("enforceInputValidation", Boolean.class));
        assertTrue(config.getValue("enforceAccessControl", Boolean.class));
        
        // Test style settings - strict
        assertTrue(config.getValue("enforceCamelCase", Boolean.class));
        assertTrue(config.getValue("enforceConsistentSpacing", Boolean.class));
        assertTrue(config.getValue("enforceIndentation", Boolean.class));
        assertEquals(4, config.getValue("maxIndentationLevel", Integer.class));
        
        // Test rule configurations - strict enforcement
        assertEquals(LintLevel.ERROR, config.getRuleConfig("STYLE").getLevel());
        assertEquals(LintLevel.ERROR, config.getRuleConfig("BEST_PRACTICE").getLevel());
        assertEquals(LintLevel.WARNING, config.getRuleConfig("PERFORMANCE").getLevel());
        assertEquals(LintLevel.ERROR, config.getRuleConfig("SECURITY").getLevel());
        
        // Test rule enabled status
        assertTrue(config.getRuleConfig("STYLE").isEnabled());
        assertTrue(config.getRuleConfig("BEST_PRACTICE").isEnabled());
        assertTrue(config.getRuleConfig("PERFORMANCE").isEnabled());
        assertTrue(config.getRuleConfig("SECURITY").isEnabled());
    }
    
    @Test
    @DisplayName("Custom Preset - User-Defined Configuration")
    void testCustomPreset() {
        // Create custom settings
        Map<String, Object> customSettings = Map.of(
            "maxLineLength", 150,
            "maxDepth", 8,
            "customSetting", "customValue"
        );
        
        // Create custom rule configs
        Map<String, RuleConfig> customRuleConfigs = Map.of(
            "STYLE", new RuleConfig(true, LintLevel.INFO),
            "CUSTOM_RULE", new RuleConfig(true, LintLevel.WARNING)
        );
        
        LintConfig config = LintPreset.custom(customSettings, customRuleConfigs);
        
        // Test custom settings
        assertEquals(150, config.getValue("maxLineLength", Integer.class));
        assertEquals(8, config.getValue("maxDepth", Integer.class));
        assertEquals("customValue", config.getValue("customSetting", String.class));
        
        // Test custom rule configs
        assertEquals(LintLevel.INFO, config.getRuleConfig("STYLE").getLevel());
        assertEquals(LintLevel.WARNING, config.getRuleConfig("CUSTOM_RULE").getLevel());
        assertTrue(config.getRuleConfig("STYLE").isEnabled());
        assertTrue(config.getRuleConfig("CUSTOM_RULE").isEnabled());
    }
    
    @Test
    @DisplayName("Custom Preset - Null Rule Configs")
    void testCustomPresetWithNullRuleConfigs() {
        Map<String, Object> customSettings = Map.of(
            "maxLineLength", 120,
            "maxDepth", 6
        );
        
        LintConfig config = LintPreset.custom(customSettings, null);
        
        // Test custom settings
        assertEquals(120, config.getValue("maxLineLength", Integer.class));
        assertEquals(6, config.getValue("maxDepth", Integer.class));
        
        // Should have default rule configs
        assertNotNull(config.getRuleConfig("STYLE"));
        assertNotNull(config.getRuleConfig("BEST_PRACTICE"));
        assertNotNull(config.getRuleConfig("PERFORMANCE"));
        assertNotNull(config.getRuleConfig("SECURITY"));
    }
    
    @Test
    @DisplayName("Merge Presets - Combine Configurations")
    void testMergePresets() {
        LintConfig base = LintPreset.relaxed();
        LintConfig override = LintPreset.strict();
        
        LintConfig merged = LintPreset.merge(base, override);
        
        // Test that override values take precedence
        assertEquals(100, merged.getValue("maxLineLength", Integer.class)); // From strict
        assertEquals(3, merged.getValue("maxDepth", Integer.class)); // From strict
        assertEquals(30, merged.getValue("maxFields", Integer.class)); // From strict (overridden)
        
        // Test rule configs
        assertEquals(LintLevel.ERROR, merged.getRuleConfig("STYLE").getLevel()); // From strict
        assertEquals(LintLevel.ERROR, merged.getRuleConfig("BEST_PRACTICE").getLevel()); // From strict
        assertEquals(LintLevel.ERROR, merged.getRuleConfig("PERFORMANCE").getLevel()); // From strict
        assertEquals(LintLevel.ERROR, merged.getRuleConfig("SECURITY").getLevel()); // From strict
        
        // Test sensitive fields (should be merged)
        Set<String> sensitiveFields = merged.getValue("sensitiveFields", Set.class);
        assertTrue(sensitiveFields.contains("password"));
        assertTrue(sensitiveFields.contains("ssn"));
        assertTrue(sensitiveFields.contains("creditCard"));
        assertTrue(sensitiveFields.contains("apiKey"));
        assertTrue(sensitiveFields.contains("token"));
        assertTrue(sensitiveFields.contains("secret"));
    }
    
    @Test
    @DisplayName("All Presets - Verify No Null Values")
    void testAllPresetsNoNullValues() {
        LintConfig[] presets = {
            LintPreset.strict(),
            LintPreset.relaxed(),
            LintPreset.performance(),
            LintPreset.security(),
            LintPreset.development(),
            LintPreset.production()
        };
        
        for (LintConfig config : presets) {
            // Test that all required settings are present
            assertNotNull(config.getValue("maxLineLength", Integer.class));
            assertNotNull(config.getValue("maxDepth", Integer.class));
            assertNotNull(config.getValue("maxFields", Integer.class));
            assertNotNull(config.getValue("maxSecurityDepth", Integer.class));
            assertNotNull(config.getValue("maxArguments", Integer.class));
            assertNotNull(config.getValue("maxFragments", Integer.class));
            assertNotNull(config.getValue("maxQueryComplexity", Integer.class));
            assertNotNull(config.getValue("sensitiveFields", Set.class));
            assertNotNull(config.getValue("forbiddenIntrospection", Boolean.class));
            assertNotNull(config.getValue("enforceCamelCase", Boolean.class));
            assertNotNull(config.getValue("enforceConsistentSpacing", Boolean.class));
            assertNotNull(config.getValue("enforceIndentation", Boolean.class));
            assertNotNull(config.getValue("maxIndentationLevel", Integer.class));
            
            // Test that all rule configs are present
            assertNotNull(config.getRuleConfig("STYLE"));
            assertNotNull(config.getRuleConfig("BEST_PRACTICE"));
            assertNotNull(config.getRuleConfig("PERFORMANCE"));
            assertNotNull(config.getRuleConfig("SECURITY"));
            
            // Test that all rule configs are enabled
            assertTrue(config.getRuleConfig("STYLE").isEnabled());
            assertTrue(config.getRuleConfig("BEST_PRACTICE").isEnabled());
            assertTrue(config.getRuleConfig("PERFORMANCE").isEnabled());
            assertTrue(config.getRuleConfig("SECURITY").isEnabled());
        }
    }
} 