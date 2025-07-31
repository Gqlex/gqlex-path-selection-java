package com.intuit.gqlex.linting.config;

import com.intuit.gqlex.linting.core.LintLevel;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Configuration class for GraphQL linting operations.
 * 
 * <p>This class manages settings and rule configurations for the linting system.
 * It provides a flexible way to configure linting behavior without hardcoding values.</p>
 * 
 * <p>The linting system is completely generic and agnostic to any specific GraphQL schema,
 * working with any query, mutation, or subscription structure.</p>
 * 
 * @author gqlex
 * @version 2.0.1
 * @since 2.0.1
 */
public class LintConfig {
    private final Map<String, Object> settings = new HashMap<>();
    private final Map<String, RuleConfig> ruleConfigs = new HashMap<>();

    /**
     * Creates a new linting configuration with default settings.
     */
    public LintConfig() {
        setDefaultSettings();
    }

    /**
     * Creates a new linting configuration with custom settings.
     * 
     * @param settings the custom settings to use
     */
    public LintConfig(Map<String, Object> settings) {
        setDefaultSettings();
        if (settings != null) {
            this.settings.putAll(settings);
        }
    }

    /**
     * Sets the default configuration settings.
     */
    private void setDefaultSettings() {
        // Generic default settings - no field-specific logic
        settings.put("maxLineLength", 80);
        settings.put("maxDepth", 5);
        settings.put("maxFields", 50);
        settings.put("maxSecurityDepth", 3);
        settings.put("sensitiveFields", Set.of("password", "ssn", "creditCard", "apiKey"));
        
        // Default rule configurations
        ruleConfigs.put("STYLE", new RuleConfig(true, LintLevel.WARNING));
        ruleConfigs.put("BEST_PRACTICE", new RuleConfig(true, LintLevel.WARNING));
        ruleConfigs.put("PERFORMANCE", new RuleConfig(true, LintLevel.INFO));
        ruleConfigs.put("SECURITY", new RuleConfig(true, LintLevel.WARNING));
    }

    /**
     * Gets a configuration value by key.
     * 
     * @param key the configuration key
     * @param type the expected type
     * @param defaultValue the default value if not found
     * @param <T> the type parameter
     * @return the configuration value or default
     */
    public <T> T getValue(String key, Class<T> type, T defaultValue) {
        if (key == null || type == null) {
            return defaultValue;
        }
        
        Object value = settings.get(key);
        if (value != null && type.isInstance(value)) {
            return type.cast(value);
        }
        return defaultValue;
    }

    /**
     * Gets a configuration value by key.
     * 
     * @param key the configuration key
     * @param type the expected type
     * @param <T> the type parameter
     * @return the configuration value or null
     */
    public <T> T getValue(String key, Class<T> type) {
        return getValue(key, type, null);
    }

    /**
     * Sets a configuration value.
     * 
     * @param key the configuration key
     * @param value the configuration value
     */
    public void setValue(String key, Object value) {
        if (key != null) {
            settings.put(key, value);
        }
    }

    /**
     * Gets the rule configuration for a specific rule.
     * 
     * @param ruleName the name of the rule
     * @return the rule configuration, or a default configuration if not found
     */
    public RuleConfig getRuleConfig(String ruleName) {
        if (ruleName == null) {
            return new RuleConfig(true, LintLevel.WARNING);
        }
        return ruleConfigs.getOrDefault(ruleName, new RuleConfig(true, LintLevel.WARNING));
    }

    /**
     * Sets the rule configuration for a specific rule.
     * 
     * @param ruleName the name of the rule
     * @param config the rule configuration
     */
    public void setRuleConfig(String ruleName, RuleConfig config) {
        if (ruleName != null && config != null) {
            ruleConfigs.put(ruleName, config);
        }
    }

    /**
     * Checks if a rule is enabled.
     * 
     * @param ruleName the name of the rule
     * @return true if the rule is enabled, false otherwise
     */
    public boolean isRuleEnabled(String ruleName) {
        return getRuleConfig(ruleName).isEnabled();
    }

    /**
     * Gets the level for a specific rule.
     * 
     * @param ruleName the name of the rule
     * @return the rule level
     */
    public LintLevel getRuleLevel(String ruleName) {
        return getRuleConfig(ruleName).getLevel();
    }

    /**
     * Gets all configuration settings.
     * 
     * @return a copy of all settings
     */
    public Map<String, Object> getAllSettings() {
        return new HashMap<>(settings);
    }

    /**
     * Gets all rule configurations.
     * 
     * @return a copy of all rule configurations
     */
    public Map<String, RuleConfig> getAllRuleConfigs() {
        return new HashMap<>(ruleConfigs);
    }

    /**
     * Merges another configuration into this one.
     * 
     * @param other the configuration to merge
     */
    public void merge(LintConfig other) {
        if (other == null) {
            return;
        }
        
        // Merge settings
        settings.putAll(other.settings);
        
        // Merge rule configs
        ruleConfigs.putAll(other.ruleConfigs);
    }

    /**
     * Creates a copy of this configuration.
     * 
     * @return a new configuration with the same settings
     */
    public LintConfig copy() {
        LintConfig copy = new LintConfig();
        copy.merge(this);
        return copy;
    }

    @Override
    public String toString() {
        return String.format("LintConfig{settings=%d, ruleConfigs=%d}", 
            settings.size(), ruleConfigs.size());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        LintConfig that = (LintConfig) obj;
        
        if (!settings.equals(that.settings)) return false;
        return ruleConfigs.equals(that.ruleConfigs);
    }

    @Override
    public int hashCode() {
        int result = settings.hashCode();
        result = 31 * result + ruleConfigs.hashCode();
        return result;
    }
} 