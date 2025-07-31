package com.intuit.gqlex.linting.config;

import com.intuit.gqlex.linting.core.LintLevel;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Predefined linting configurations for common use cases.
 * Provides ready-to-use configurations that can be customized further.
 */
public class LintPreset {
    
    /**
     * Strict configuration with maximum enforcement.
     * All rules are set to ERROR level for maximum compliance.
     */
    public static LintConfig strict() {
        LintConfig config = new LintConfig();
        
        // Performance settings
        config.setValue("maxLineLength", 100);
        config.setValue("maxDepth", 3);
        config.setValue("maxFields", 30);
        config.setValue("maxSecurityDepth", 2);
        config.setValue("maxArguments", 10);
        config.setValue("maxFragments", 5);
        
        // Security settings
        config.setValue("sensitiveFields", Set.of("password", "ssn", "creditCard", "apiKey", "token", "secret"));
        config.setValue("forbiddenIntrospection", true);
        config.setValue("maxQueryComplexity", 50);
        
        // Style settings
        config.setValue("enforceCamelCase", true);
        config.setValue("enforceConsistentSpacing", true);
        config.setValue("enforceIndentation", true);
        config.setValue("maxIndentationLevel", 4);
        
        // Rule configurations - all set to ERROR
        config.setRuleConfig("STYLE", new RuleConfig(true, LintLevel.ERROR));
        config.setRuleConfig("BEST_PRACTICE", new RuleConfig(true, LintLevel.ERROR));
        config.setRuleConfig("PERFORMANCE", new RuleConfig(true, LintLevel.ERROR));
        config.setRuleConfig("SECURITY", new RuleConfig(true, LintLevel.ERROR));
        
        return config;
    }
    
    /**
     * Relaxed configuration with minimum enforcement.
     * Most rules are set to INFO level for gentle guidance.
     */
    public static LintConfig relaxed() {
        LintConfig config = new LintConfig();
        
        // Performance settings - more permissive
        config.setValue("maxLineLength", 120);
        config.setValue("maxDepth", 7);
        config.setValue("maxFields", 80);
        config.setValue("maxSecurityDepth", 4);
        config.setValue("maxArguments", 20);
        config.setValue("maxFragments", 10);
        
        // Security settings - more permissive
        config.setValue("sensitiveFields", Set.of("password", "ssn", "creditCard"));
        config.setValue("forbiddenIntrospection", false);
        config.setValue("maxQueryComplexity", 100);
        
        // Style settings - more permissive
        config.setValue("enforceCamelCase", false);
        config.setValue("enforceConsistentSpacing", false);
        config.setValue("enforceIndentation", false);
        config.setValue("maxIndentationLevel", 8);
        
        // Rule configurations - mostly INFO level
        config.setRuleConfig("STYLE", new RuleConfig(true, LintLevel.INFO));
        config.setRuleConfig("BEST_PRACTICE", new RuleConfig(true, LintLevel.WARNING));
        config.setRuleConfig("PERFORMANCE", new RuleConfig(true, LintLevel.INFO));
        config.setRuleConfig("SECURITY", new RuleConfig(true, LintLevel.WARNING));
        
        return config;
    }
    
    /**
     * Performance-focused configuration.
     * Emphasizes performance rules and optimizations.
     */
    public static LintConfig performance() {
        LintConfig config = new LintConfig();
        
        // Performance settings - strict
        config.setValue("maxLineLength", 80);
        config.setValue("maxDepth", 4);
        config.setValue("maxFields", 40);
        config.setValue("maxSecurityDepth", 3);
        config.setValue("maxArguments", 8);
        config.setValue("maxFragments", 3);
        config.setValue("maxQueryComplexity", 30);
        
        // Performance-specific settings
        config.setValue("enforceFragmentUsage", true);
        config.setValue("enforceFieldSelection", true);
        config.setValue("enforceAliasUsage", true);
        config.setValue("maxSelectionSetSize", 15);
        
        // Security settings - moderate
        config.setValue("sensitiveFields", Set.of("password", "ssn", "creditCard", "apiKey"));
        config.setValue("forbiddenIntrospection", true);
        
        // Style settings - moderate
        config.setValue("enforceCamelCase", true);
        config.setValue("enforceConsistentSpacing", true);
        config.setValue("enforceIndentation", true);
        config.setValue("maxIndentationLevel", 4);
        
        // Rule configurations - performance rules at ERROR level
        config.setRuleConfig("STYLE", new RuleConfig(true, LintLevel.INFO));
        config.setRuleConfig("BEST_PRACTICE", new RuleConfig(true, LintLevel.WARNING));
        config.setRuleConfig("PERFORMANCE", new RuleConfig(true, LintLevel.ERROR));
        config.setRuleConfig("SECURITY", new RuleConfig(true, LintLevel.WARNING));
        
        return config;
    }
    
    /**
     * Security-focused configuration.
     * Emphasizes security rules and protection.
     */
    public static LintConfig security() {
        LintConfig config = new LintConfig();
        
        // Security settings - very strict
        config.setValue("maxLineLength", 80);
        config.setValue("maxDepth", 2);
        config.setValue("maxFields", 20);
        config.setValue("maxSecurityDepth", 2);
        config.setValue("maxArguments", 5);
        config.setValue("maxFragments", 2);
        config.setValue("maxQueryComplexity", 20);
        
        // Security-specific settings
        config.setValue("sensitiveFields", Set.of("password", "ssn", "creditCard", "apiKey", "token", "secret", "privateKey", "authToken"));
        config.setValue("forbiddenIntrospection", true);
        config.setValue("enforceInputValidation", true);
        config.setValue("enforceAccessControl", true);
        config.setValue("forbiddenDirectives", Set.of("auth", "admin", "internal"));
        
        // Performance settings - moderate
        config.setValue("enforceFragmentUsage", true);
        config.setValue("enforceFieldSelection", true);
        config.setValue("maxSelectionSetSize", 10);
        
        // Style settings - moderate
        config.setValue("enforceCamelCase", true);
        config.setValue("enforceConsistentSpacing", true);
        config.setValue("enforceIndentation", true);
        config.setValue("maxIndentationLevel", 3);
        
        // Rule configurations - security rules at ERROR level
        config.setRuleConfig("STYLE", new RuleConfig(true, LintLevel.WARNING));
        config.setRuleConfig("BEST_PRACTICE", new RuleConfig(true, LintLevel.WARNING));
        config.setRuleConfig("PERFORMANCE", new RuleConfig(true, LintLevel.WARNING));
        config.setRuleConfig("SECURITY", new RuleConfig(true, LintLevel.ERROR));
        
        return config;
    }
    
    /**
     * Development configuration for development environments.
     * Balanced approach with helpful warnings.
     */
    public static LintConfig development() {
        LintConfig config = new LintConfig();
        
        // Balanced settings for development
        config.setValue("maxLineLength", 100);
        config.setValue("maxDepth", 5);
        config.setValue("maxFields", 50);
        config.setValue("maxSecurityDepth", 3);
        config.setValue("maxArguments", 15);
        config.setValue("maxFragments", 5);
        config.setValue("maxQueryComplexity", 75);
        
        // Development-specific settings
        config.setValue("sensitiveFields", Set.of("password", "ssn", "creditCard"));
        config.setValue("forbiddenIntrospection", false); // Allow introspection in development
        config.setValue("enforceFragmentUsage", true);
        config.setValue("enforceFieldSelection", true);
        config.setValue("maxSelectionSetSize", 20);
        
        // Style settings - helpful but not strict
        config.setValue("enforceCamelCase", true);
        config.setValue("enforceConsistentSpacing", true);
        config.setValue("enforceIndentation", true);
        config.setValue("maxIndentationLevel", 5);
        
        // Rule configurations - mostly WARNING level
        config.setRuleConfig("STYLE", new RuleConfig(true, LintLevel.WARNING));
        config.setRuleConfig("BEST_PRACTICE", new RuleConfig(true, LintLevel.WARNING));
        config.setRuleConfig("PERFORMANCE", new RuleConfig(true, LintLevel.WARNING));
        config.setRuleConfig("SECURITY", new RuleConfig(true, LintLevel.WARNING));
        
        return config;
    }
    
    /**
     * Production configuration for production environments.
     * Strict security and performance with moderate style enforcement.
     */
    public static LintConfig production() {
        LintConfig config = new LintConfig();
        
        // Production settings - strict
        config.setValue("maxLineLength", 80);
        config.setValue("maxDepth", 4);
        config.setValue("maxFields", 40);
        config.setValue("maxSecurityDepth", 3);
        config.setValue("maxArguments", 10);
        config.setValue("maxFragments", 5);
        config.setValue("maxQueryComplexity", 50);
        
        // Production-specific settings
        config.setValue("sensitiveFields", Set.of("password", "ssn", "creditCard", "apiKey", "token", "secret"));
        config.setValue("forbiddenIntrospection", true);
        config.setValue("enforceFragmentUsage", true);
        config.setValue("enforceFieldSelection", true);
        config.setValue("maxSelectionSetSize", 15);
        config.setValue("enforceInputValidation", true);
        config.setValue("enforceAccessControl", true);
        
        // Style settings - strict
        config.setValue("enforceCamelCase", true);
        config.setValue("enforceConsistentSpacing", true);
        config.setValue("enforceIndentation", true);
        config.setValue("maxIndentationLevel", 4);
        
        // Rule configurations - strict enforcement
        config.setRuleConfig("STYLE", new RuleConfig(true, LintLevel.ERROR));
        config.setRuleConfig("BEST_PRACTICE", new RuleConfig(true, LintLevel.ERROR));
        config.setRuleConfig("PERFORMANCE", new RuleConfig(true, LintLevel.WARNING));
        config.setRuleConfig("SECURITY", new RuleConfig(true, LintLevel.ERROR));
        
        return config;
    }
    
    /**
     * Custom configuration builder for specific requirements.
     * Allows fine-grained control over all settings.
     */
    public static LintConfig custom(Map<String, Object> settings, Map<String, RuleConfig> ruleConfigs) {
        LintConfig config = new LintConfig(settings);
        
        if (ruleConfigs != null) {
            for (Map.Entry<String, RuleConfig> entry : ruleConfigs.entrySet()) {
                config.setRuleConfig(entry.getKey(), entry.getValue());
            }
        }
        
        return config;
    }
    
    /**
     * Merge multiple presets with custom overrides.
     * Useful for combining different preset characteristics.
     */
    public static LintConfig merge(LintConfig base, LintConfig override) {
        LintConfig merged = new LintConfig();
        
        // Merge settings
        Map<String, Object> baseSettings = base.getAllSettings();
        Map<String, Object> overrideSettings = override.getAllSettings();
        
        Map<String, Object> mergedSettings = new HashMap<>(baseSettings);
        mergedSettings.putAll(overrideSettings);
        
        // Merge rule configs
        Map<String, RuleConfig> baseRules = base.getAllRuleConfigs();
        Map<String, RuleConfig> overrideRules = override.getAllRuleConfigs();
        
        Map<String, RuleConfig> mergedRules = new HashMap<>(baseRules);
        mergedRules.putAll(overrideRules);
        
        // Create merged config
        for (Map.Entry<String, Object> entry : mergedSettings.entrySet()) {
            merged.setValue(entry.getKey(), entry.getValue());
        }
        
        for (Map.Entry<String, RuleConfig> entry : mergedRules.entrySet()) {
            merged.setRuleConfig(entry.getKey(), entry.getValue());
        }
        
        return merged;
    }
} 