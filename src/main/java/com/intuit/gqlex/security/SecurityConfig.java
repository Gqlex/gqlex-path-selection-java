package com.intuit.gqlex.security;

import java.util.Map;
import java.util.HashMap;

/**
 * Configuration class for security settings.
 * Provides a centralized way to configure all security-related settings.
 */
public class SecurityConfig {
    
    // Basic security settings
    private int maxDepth = 10;
    private int maxFields = 100;
    private int maxArguments = 20;
    private int maxComplexity = 1000;
    private boolean allowIntrospection = false;
    
    // Feature toggles
    private boolean enableRateLimiting = true;
    private boolean enableAuditLogging = true;
    private boolean enableAccessControl = true;
    
    // Rate limiting settings
    private int maxQueriesPerMinute = 1000;
    private int maxQueriesPerHour = 10000;
    private int maxQueriesPerDay = 100000;
    
    // Custom settings
    private Map<String, Object> customSettings = new HashMap<>();
    
    /**
     * Creates a security config with default settings.
     */
    public SecurityConfig() {
    }
    
    /**
     * Creates a security config with custom settings.
     */
    public SecurityConfig(int maxDepth, int maxFields, int maxArguments, int maxComplexity) {
        this.maxDepth = maxDepth;
        this.maxFields = maxFields;
        this.maxArguments = maxArguments;
        this.maxComplexity = maxComplexity;
    }
    
    // Getters and setters
    
    public int getMaxDepth() {
        return maxDepth;
    }
    
    public SecurityConfig setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }
    
    public int getMaxFields() {
        return maxFields;
    }
    
    public SecurityConfig setMaxFields(int maxFields) {
        this.maxFields = maxFields;
        return this;
    }
    
    public int getMaxArguments() {
        return maxArguments;
    }
    
    public SecurityConfig setMaxArguments(int maxArguments) {
        this.maxArguments = maxArguments;
        return this;
    }
    
    public int getMaxComplexity() {
        return maxComplexity;
    }
    
    public SecurityConfig setMaxComplexity(int maxComplexity) {
        this.maxComplexity = maxComplexity;
        return this;
    }
    
    public boolean isAllowIntrospection() {
        return allowIntrospection;
    }
    
    public SecurityConfig setAllowIntrospection(boolean allowIntrospection) {
        this.allowIntrospection = allowIntrospection;
        return this;
    }
    
    public boolean isEnableRateLimiting() {
        return enableRateLimiting;
    }
    
    public SecurityConfig setEnableRateLimiting(boolean enableRateLimiting) {
        this.enableRateLimiting = enableRateLimiting;
        return this;
    }
    
    public boolean isEnableAuditLogging() {
        return enableAuditLogging;
    }
    
    public SecurityConfig setEnableAuditLogging(boolean enableAuditLogging) {
        this.enableAuditLogging = enableAuditLogging;
        return this;
    }
    
    public boolean isEnableAccessControl() {
        return enableAccessControl;
    }
    
    public SecurityConfig setEnableAccessControl(boolean enableAccessControl) {
        this.enableAccessControl = enableAccessControl;
        return this;
    }
    
    public int getMaxQueriesPerMinute() {
        return maxQueriesPerMinute;
    }
    
    public SecurityConfig setMaxQueriesPerMinute(int maxQueriesPerMinute) {
        this.maxQueriesPerMinute = maxQueriesPerMinute;
        return this;
    }
    
    public int getMaxQueriesPerHour() {
        return maxQueriesPerHour;
    }
    
    public SecurityConfig setMaxQueriesPerHour(int maxQueriesPerHour) {
        this.maxQueriesPerHour = maxQueriesPerHour;
        return this;
    }
    
    public int getMaxQueriesPerDay() {
        return maxQueriesPerDay;
    }
    
    public SecurityConfig setMaxQueriesPerDay(int maxQueriesPerDay) {
        this.maxQueriesPerDay = maxQueriesPerDay;
        return this;
    }
    
    public Map<String, Object> getCustomSettings() {
        return new HashMap<>(customSettings);
    }
    
    public SecurityConfig setCustomSettings(Map<String, Object> customSettings) {
        this.customSettings = new HashMap<>(customSettings);
        return this;
    }
    
    public SecurityConfig addCustomSetting(String key, Object value) {
        this.customSettings.put(key, value);
        return this;
    }
    
    public Object getCustomSetting(String key) {
        return customSettings.get(key);
    }
    
    public <T> T getCustomSetting(String key, Class<T> type) {
        Object value = customSettings.get(key);
        return type.isInstance(value) ? type.cast(value) : null;
    }
} 