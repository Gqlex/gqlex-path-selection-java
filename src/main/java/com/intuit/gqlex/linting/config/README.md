# GraphQL Linting Configuration

[![Java](https://img.shields.io/badge/Java-11+-blue.svg)](https://openjdk.java.net/)
[![Tests](https://img.shields.io/badge/Tests-Passing-brightgreen.svg)](https://github.com/Gqlex/gqlex-path-selection-java)

> **Configuration Management for GraphQL Linting System**

The configuration package provides flexible and extensible configuration management for the GraphQL linting system, including global settings, rule-specific configurations, and preset configurations.

## 📋 Table of Contents

- [⚙️ Configuration Overview](#️-configuration-overview)
- [🔧 Configuration Components](#-configuration-components)
- [💡 Usage Examples](#-usage-examples)
- [📚 API Reference](#-api-reference)
- [🎯 Preset Configurations](#-preset-configurations)
- [🧪 Testing](#-testing)

## ⚙️ Configuration Overview

### **Configuration Hierarchy**

```
LintConfig (Global Configuration)
├── Global Settings (maxLineLength, maxDepth, etc.)
├── Rule Configurations (per-rule settings)
└── Metadata (custom key-value pairs)

RuleConfig (Individual Rule Configuration)
├── Enabled/Disabled state
└── Severity level (ERROR, WARNING, INFO)
```

### **Configuration Features**

- **Global Settings**: Application-wide configuration values
- **Rule-Specific Settings**: Individual rule configuration
- **Type Safety**: Generic configuration access with type checking
- **Default Values**: Fallback values for missing configuration
- **Preset Configurations**: Ready-to-use configuration templates
- **Configuration Merging**: Combine multiple configurations

## 🔧 Configuration Components

### **LintConfig**

The main configuration class that manages global settings and rule configurations.

**Key Features:**
- **Global Settings**: Application-wide configuration values
- **Rule Management**: Individual rule configuration
- **Type Safety**: Generic configuration access
- **Default Values**: Fallback values for missing settings
- **Configuration Merging**: Combine multiple configurations

### **RuleConfig**

Configuration for individual linting rules.

**Key Features:**
- **Enabled/Disabled State**: Control rule execution
- **Severity Level**: Configure rule severity (ERROR, WARNING, INFO)
- **Immutable Design**: Thread-safe configuration objects

### **LintPreset**

Predefined configuration templates for common use cases.

**Available Presets:**
- **Strict**: Maximum enforcement with ERROR level
- **Relaxed**: Minimum enforcement with INFO level
- **Performance**: Performance-focused configuration
- **Security**: Security-focused configuration

## 💡 Usage Examples

### **Example 1: Basic Configuration**

```java
import com.intuit.gqlex.linting.config.LintConfig;
import com.intuit.gqlex.linting.config.RuleConfig;
import com.intuit.gqlex.linting.core.LintLevel;

public class BasicConfigurationExample {
    public static void main(String[] args) {
        // Create configuration
        LintConfig config = new LintConfig();
        
        // Set global settings
        config.setValue("maxLineLength", 80);
        config.setValue("maxDepth", 5);
        config.setValue("maxFields", 50);
        config.setValue("maxSecurityDepth", 3);
        
        // Set sensitive fields
        config.setValue("sensitiveFields", Set.of("password", "ssn", "creditCard", "apiKey"));
        
        // Configure rules
        config.setRuleConfig("STYLE", new RuleConfig(true, LintLevel.WARNING));
        config.setRuleConfig("BEST_PRACTICE", new RuleConfig(true, LintLevel.WARNING));
        config.setRuleConfig("PERFORMANCE", new RuleConfig(true, LintLevel.INFO));
        config.setRuleConfig("SECURITY", new RuleConfig(true, LintLevel.ERROR));
        
        // Use configuration
        GraphQLLinter linter = new GraphQLLinter(config);
        
        // Access configuration values
        int maxLineLength = config.getValue("maxLineLength", Integer.class, 80);
        int maxDepth = config.getValue("maxDepth", Integer.class, 5);
        
        System.out.println("Max line length: " + maxLineLength);
        System.out.println("Max depth: " + maxDepth);
        
        // Check rule configuration
        RuleConfig styleConfig = config.getRuleConfig("STYLE");
        System.out.println("Style rule enabled: " + styleConfig.isEnabled());
        System.out.println("Style rule level: " + styleConfig.getLevel());
    }
}
```

### **Example 2: Advanced Configuration**

```java
import com.intuit.gqlex.linting.config.LintConfig;
import com.intuit.gqlex.linting.config.RuleConfig;
import com.intuit.gqlex.linting.core.LintLevel;
import java.util.*;

public class AdvancedConfigurationExample {
    public static void main(String[] args) {
        // Create configuration with custom settings
        Map<String, Object> customSettings = new HashMap<>();
        customSettings.put("maxLineLength", 120);
        customSettings.put("maxDepth", 10);
        customSettings.put("maxFields", 100);
        customSettings.put("customSetting", "customValue");
        
        LintConfig config = new LintConfig(customSettings);
        
        // Add more settings
        config.setValue("performance.threshold", 0.8);
        config.setValue("security.allowedIntrospection", false);
        config.setValue("style.enforceCamelCase", true);
        
        // Configure rules with different levels
        config.setRuleConfig("STYLE", new RuleConfig(true, LintLevel.ERROR));
        config.setRuleConfig("BEST_PRACTICE", new RuleConfig(true, LintLevel.WARNING));
        config.setRuleConfig("PERFORMANCE", new RuleConfig(false, LintLevel.INFO)); // Disabled
        config.setRuleConfig("SECURITY", new RuleConfig(true, LintLevel.ERROR));
        
        // Add custom rule configuration
        config.setRuleConfig("CUSTOM_RULE", new RuleConfig(true, LintLevel.WARNING));
        
        // Use configuration
        GraphQLLinter linter = new GraphQLLinter(config);
        
        // Access typed configuration values
        int maxLineLength = config.getValue("maxLineLength", Integer.class);
        double performanceThreshold = config.getValue("performance.threshold", Double.class, 0.5);
        boolean enforceCamelCase = config.getValue("style.enforceCamelCase", Boolean.class, false);
        String customValue = config.getValue("customSetting", String.class);
        
        System.out.println("Configuration loaded:");
        System.out.println("  Max line length: " + maxLineLength);
        System.out.println("  Performance threshold: " + performanceThreshold);
        System.out.println("  Enforce camel case: " + enforceCamelCase);
        System.out.println("  Custom value: " + customValue);
        
        // Check all rule configurations
        System.out.println("\nRule configurations:");
        Map<String, RuleConfig> ruleConfigs = config.getAllRuleConfigs();
        ruleConfigs.forEach((ruleName, ruleConfig) -> {
            System.out.println("  " + ruleName + ": " + 
                (ruleConfig.isEnabled() ? "ENABLED" : "DISABLED") + 
                " (" + ruleConfig.getLevel() + ")");
        });
    }
}
```

### **Example 3: Configuration Merging**

```java
import com.intuit.gqlex.linting.config.LintConfig;
import com.intuit.gqlex.linting.config.RuleConfig;
import com.intuit.gqlex.linting.core.LintLevel;

public class ConfigurationMergingExample {
    public static void main(String[] args) {
        // Create base configuration
        LintConfig baseConfig = new LintConfig();
        baseConfig.setValue("maxLineLength", 80);
        baseConfig.setValue("maxDepth", 5);
        baseConfig.setRuleConfig("STYLE", new RuleConfig(true, LintLevel.WARNING));
        baseConfig.setRuleConfig("BEST_PRACTICE", new RuleConfig(true, LintLevel.WARNING));
        
        // Create override configuration
        LintConfig overrideConfig = new LintConfig();
        overrideConfig.setValue("maxLineLength", 120); // Override
        overrideConfig.setValue("customSetting", "overrideValue"); // New setting
        overrideConfig.setRuleConfig("STYLE", new RuleConfig(true, LintLevel.ERROR)); // Override
        overrideConfig.setRuleConfig("PERFORMANCE", new RuleConfig(true, LintLevel.INFO)); // New rule
        
        // Merge configurations
        baseConfig.merge(overrideConfig);
        
        // Verify merged configuration
        System.out.println("=== Merged Configuration ===");
        System.out.println("Max line length: " + baseConfig.getValue("maxLineLength", Integer.class));
        System.out.println("Max depth: " + baseConfig.getValue("maxDepth", Integer.class));
        System.out.println("Custom setting: " + baseConfig.getValue("customSetting", String.class));
        
        System.out.println("\nRule configurations:");
        Map<String, RuleConfig> ruleConfigs = baseConfig.getAllRuleConfigs();
        ruleConfigs.forEach((ruleName, ruleConfig) -> {
            System.out.println("  " + ruleName + ": " + ruleConfig.getLevel());
        });
    }
}
```

### **Example 4: Preset Configurations**

```java
import com.intuit.gqlex.linting.config.LintPreset;
import com.intuit.gqlex.linting.core.GraphQLLinter;

public class PresetConfigurationExample {
    public static void main(String[] args) {
        // Use strict preset
        GraphQLLinter strictLinter = new GraphQLLinter(LintPreset.strict());
        strictLinter.addRule(new StyleRule());
        strictLinter.addRule(new BestPracticeRule());
        strictLinter.addRule(new PerformanceRule());
        strictLinter.addRule(new SecurityRule());
        
        // Use relaxed preset
        GraphQLLinter relaxedLinter = new GraphQLLinter(LintPreset.relaxed());
        relaxedLinter.addRule(new StyleRule());
        relaxedLinter.addRule(new BestPracticeRule());
        relaxedLinter.addRule(new PerformanceRule());
        relaxedLinter.addRule(new SecurityRule());
        
        // Use performance preset
        GraphQLLinter performanceLinter = new GraphQLLinter(LintPreset.performance());
        performanceLinter.addRule(new StyleRule());
        performanceLinter.addRule(new BestPracticeRule());
        performanceLinter.addRule(new PerformanceRule());
        performanceLinter.addRule(new SecurityRule());
        
        // Use security preset
        GraphQLLinter securityLinter = new GraphQLLinter(LintPreset.security());
        securityLinter.addRule(new StyleRule());
        securityLinter.addRule(new BestPracticeRule());
        securityLinter.addRule(new PerformanceRule());
        securityLinter.addRule(new SecurityRule());
        
        // Test with different presets
        String query = "query { user { id name posts { id title } } }";
        
        System.out.println("=== Strict Preset Results ===");
        LintResult strictResult = strictLinter.lint(query);
        System.out.println("Errors: " + strictResult.getErrorCount());
        System.out.println("Warnings: " + strictResult.getWarningCount());
        
        System.out.println("\n=== Relaxed Preset Results ===");
        LintResult relaxedResult = relaxedLinter.lint(query);
        System.out.println("Errors: " + relaxedResult.getErrorCount());
        System.out.println("Warnings: " + relaxedResult.getWarningCount());
        
        System.out.println("\n=== Performance Preset Results ===");
        LintResult performanceResult = performanceLinter.lint(query);
        System.out.println("Errors: " + performanceResult.getErrorCount());
        System.out.println("Warnings: " + performanceResult.getWarningCount());
        
        System.out.println("\n=== Security Preset Results ===");
        LintResult securityResult = securityLinter.lint(query);
        System.out.println("Errors: " + securityResult.getErrorCount());
        System.out.println("Warnings: " + securityResult.getWarningCount());
    }
}
```

### **Example 5: Environment-Specific Configuration**

```java
import com.intuit.gqlex.linting.config.LintConfig;
import com.intuit.gqlex.linting.config.RuleConfig;
import com.intuit.gqlex.linting.core.LintLevel;

public class EnvironmentConfigurationExample {
    public static void main(String[] args) {
        String environment = System.getProperty("env", "development");
        
        LintConfig config = createEnvironmentConfig(environment);
        GraphQLLinter linter = new GraphQLLinter(config);
        
        System.out.println("=== " + environment.toUpperCase() + " Configuration ===");
        System.out.println("Max line length: " + config.getValue("maxLineLength", Integer.class));
        System.out.println("Max depth: " + config.getValue("maxDepth", Integer.class));
        System.out.println("Security level: " + config.getRuleConfig("SECURITY").getLevel());
    }
    
    private static LintConfig createEnvironmentConfig(String environment) {
        LintConfig config = new LintConfig();
        
        switch (environment.toLowerCase()) {
            case "development":
                // Relaxed configuration for development
                config.setValue("maxLineLength", 120);
                config.setValue("maxDepth", 10);
                config.setValue("maxFields", 100);
                config.setRuleConfig("STYLE", new RuleConfig(true, LintLevel.INFO));
                config.setRuleConfig("BEST_PRACTICE", new RuleConfig(true, LintLevel.WARNING));
                config.setRuleConfig("PERFORMANCE", new RuleConfig(false, LintLevel.INFO));
                config.setRuleConfig("SECURITY", new RuleConfig(true, LintLevel.WARNING));
                break;
                
            case "staging":
                // Moderate configuration for staging
                config.setValue("maxLineLength", 100);
                config.setValue("maxDepth", 7);
                config.setValue("maxFields", 75);
                config.setRuleConfig("STYLE", new RuleConfig(true, LintLevel.WARNING));
                config.setRuleConfig("BEST_PRACTICE", new RuleConfig(true, LintLevel.WARNING));
                config.setRuleConfig("PERFORMANCE", new RuleConfig(true, LintLevel.WARNING));
                config.setRuleConfig("SECURITY", new RuleConfig(true, LintLevel.WARNING));
                break;
                
            case "production":
                // Strict configuration for production
                config.setValue("maxLineLength", 80);
                config.setValue("maxDepth", 5);
                config.setValue("maxFields", 50);
                config.setRuleConfig("STYLE", new RuleConfig(true, LintLevel.ERROR));
                config.setRuleConfig("BEST_PRACTICE", new RuleConfig(true, LintLevel.ERROR));
                config.setRuleConfig("PERFORMANCE", new RuleConfig(true, LintLevel.WARNING));
                config.setRuleConfig("SECURITY", new RuleConfig(true, LintLevel.ERROR));
                break;
                
            default:
                // Default configuration
                config.setValue("maxLineLength", 80);
                config.setValue("maxDepth", 5);
                config.setValue("maxFields", 50);
                config.setRuleConfig("STYLE", new RuleConfig(true, LintLevel.WARNING));
                config.setRuleConfig("BEST_PRACTICE", new RuleConfig(true, LintLevel.WARNING));
                config.setRuleConfig("PERFORMANCE", new RuleConfig(true, LintLevel.INFO));
                config.setRuleConfig("SECURITY", new RuleConfig(true, LintLevel.WARNING));
                break;
        }
        
        return config;
    }
}
```

## 📚 API Reference

### **LintConfig**

#### **Constructors**
```java
LintConfig()                                              // Default configuration
LintConfig(Map<String, Object> settings)                 // Custom settings
```

#### **Global Settings**
```java
<T> T getValue(String key, Class<T> type)                // Get value
<T> T getValue(String key, Class<T> type, T defaultValue) // Get with default
void setValue(String key, Object value)                  // Set value
Map<String, Object> getAllSettings()                     // Get all settings
```

#### **Rule Configuration**
```java
RuleConfig getRuleConfig(String ruleName)                // Get rule config
void setRuleConfig(String ruleName, RuleConfig config)   // Set rule config
boolean isRuleEnabled(String ruleName)                   // Check if rule enabled
LintLevel getRuleLevel(String ruleName)                  // Get rule level
Map<String, RuleConfig> getAllRuleConfigs()              // Get all rule configs
```

#### **Configuration Management**
```java
void merge(LintConfig other)                             // Merge configuration
LintConfig copy()                                         // Copy configuration
```

### **RuleConfig**

#### **Constructors**
```java
RuleConfig(boolean enabled, LintLevel level)             // Full constructor
RuleConfig(boolean enabled)                              // Default WARNING level
```

#### **Properties**
```java
boolean isEnabled()                                       // Check if enabled
LintLevel getLevel()                                      // Get severity level
```

### **LintPreset**

#### **Static Methods**
```java
static LintConfig strict()                                // Strict configuration
static LintConfig relaxed()                               // Relaxed configuration
static LintConfig performance()                           // Performance configuration
static LintConfig security()                              // Security configuration
```

## 🎯 Preset Configurations

### **Strict Preset**

Maximum enforcement with ERROR level for most rules.

**Settings:**
- `maxLineLength`: 100
- `maxDepth`: 3
- `maxFields`: 30
- `maxSecurityDepth`: 2

**Rule Levels:**
- `STYLE`: ERROR
- `BEST_PRACTICE`: ERROR
- `PERFORMANCE`: WARNING
- `SECURITY`: ERROR

### **Relaxed Preset**

Minimum enforcement with INFO level for most rules.

**Settings:**
- `maxLineLength`: 120
- `maxDepth`: 7
- `maxFields`: 80
- `maxSecurityDepth`: 4

**Rule Levels:**
- `STYLE`: INFO
- `BEST_PRACTICE`: WARNING
- `PERFORMANCE`: INFO
- `SECURITY`: WARNING

### **Performance Preset**

Performance-focused configuration with emphasis on optimization.

**Settings:**
- `maxLineLength`: 80
- `maxDepth`: 4
- `maxFields`: 40
- `maxSecurityDepth`: 3

**Rule Levels:**
- `STYLE`: INFO
- `BEST_PRACTICE`: WARNING
- `PERFORMANCE`: ERROR
- `SECURITY`: WARNING

### **Security Preset**

Security-focused configuration with emphasis on security rules.

**Settings:**
- `maxLineLength`: 80
- `maxDepth`: 2
- `maxFields`: 20
- `maxSecurityDepth`: 2

**Rule Levels:**
- `STYLE`: WARNING
- `BEST_PRACTICE`: WARNING
- `PERFORMANCE`: WARNING
- `SECURITY`: ERROR

## 🧪 Testing

### **Testing Configuration**

```java
@Test
@DisplayName("Configuration - Default Settings")
void testConfigurationDefaultSettings() {
    LintConfig config = new LintConfig();
    
    // Test default values
    assertEquals(80, config.getValue("maxLineLength", Integer.class));
    assertEquals(5, config.getValue("maxDepth", Integer.class));
    assertEquals(50, config.getValue("maxFields", Integer.class));
    
    // Test rule configurations
    RuleConfig styleConfig = config.getRuleConfig("STYLE");
    assertTrue(styleConfig.isEnabled());
    assertEquals(LintLevel.WARNING, styleConfig.getLevel());
}
```

### **Testing Preset Configurations**

```java
@Test
@DisplayName("Preset Configurations - Strict")
void testStrictPreset() {
    LintConfig config = LintPreset.strict();
    
    // Test strict settings
    assertEquals(100, config.getValue("maxLineLength", Integer.class));
    assertEquals(3, config.getValue("maxDepth", Integer.class));
    assertEquals(30, config.getValue("maxFields", Integer.class));
    
    // Test strict rule levels
    assertEquals(LintLevel.ERROR, config.getRuleConfig("STYLE").getLevel());
    assertEquals(LintLevel.ERROR, config.getRuleConfig("BEST_PRACTICE").getLevel());
    assertEquals(LintLevel.WARNING, config.getRuleConfig("PERFORMANCE").getLevel());
    assertEquals(LintLevel.ERROR, config.getRuleConfig("SECURITY").getLevel());
}
```

### **Running Configuration Tests**

```bash
# Run all configuration tests
mvn test -Dtest="*Config*"

# Run specific configuration tests
mvn test -Dtest=LintConfigTest
mvn test -Dtest=RuleConfigTest
mvn test -Dtest=LintPresetTest
```

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**✅ CONFIGURATION SYSTEM - PRODUCTION READY** 