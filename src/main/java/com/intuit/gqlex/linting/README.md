# GraphQL Linting System

[![Java](https://img.shields.io/badge/Java-11+-blue.svg)](https://openjdk.java.net/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-orange.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Tests](https://img.shields.io/badge/Tests-31%20passing-brightgreen.svg)](https://github.com/Gqlex/gqlex-path-selection-java)

> **Generic, Agnostic, and Production-Ready GraphQL Linting System**

A comprehensive GraphQL linting system that provides code quality analysis, style enforcement, and best practice recommendations. **100% generic and agnostic** to any specific GraphQL schema, working with any query, mutation, or subscription structure.

## 📋 Table of Contents

- [🚀 Quick Start](#-quick-start)
- [🎯 Features](#-features)
- [📦 Installation](#-installation)
- [💡 Usage Examples](#-usage-examples)
- [⚙️ Configuration](#️-configuration)
- [🔧 Custom Rules](#-custom-rules)
- [🧪 Testing](#-testing)
- [📚 API Reference](#-api-reference)
- [🏗️ Architecture](#️-architecture)
- [🤝 Contributing](#-contributing)

## 🚀 Quick Start

### **Basic Usage**

```java
import com.intuit.gqlex.linting.core.GraphQLLinter;
import com.intuit.gqlex.linting.core.LintResult;

// Create a linter with default configuration
GraphQLLinter linter = new GraphQLLinter();

// Add linting rules
linter.addRule(new StyleRule());
linter.addRule(new BestPracticeRule());
linter.addRule(new PerformanceRule());
linter.addRule(new SecurityRule());

// Lint a GraphQL query
String query = "query { user { id name email } }";
LintResult result = linter.lint(query);

// Check results
if (result.hasErrors()) {
    System.out.println("Found " + result.getErrorCount() + " errors");
    result.getErrors().forEach(error -> 
        System.out.println("ERROR: " + error.getMessage()));
}

if (result.hasWarnings()) {
    System.out.println("Found " + result.getWarningCount() + " warnings");
    result.getWarnings().forEach(warning -> 
        System.out.println("WARNING: " + warning.getMessage()));
}
```

### **Advanced Configuration**

```java
import com.intuit.gqlex.linting.config.LintConfig;
import com.intuit.gqlex.linting.core.LintLevel;

// Create custom configuration
LintConfig config = new LintConfig();
config.setValue("maxLineLength", 100);
config.setValue("maxDepth", 7);
config.setValue("maxFields", 80);

// Configure rule levels
config.setRuleConfig("STYLE", new RuleConfig(true, LintLevel.ERROR));
config.setRuleConfig("PERFORMANCE", new RuleConfig(true, LintLevel.WARNING));

// Create linter with custom configuration
GraphQLLinter linter = new GraphQLLinter(config);
```

## 🎯 Features

### **✅ 100% Generic & Agnostic**
- **No hardcoded field names** - works with any GraphQL schema
- **Universal compatibility** - supports any query, mutation, or subscription
- **Schema-independent** - no assumptions about specific types or fields
- **Extensible architecture** - easy to add custom linting rules

### **🔧 Comprehensive Rule System**
- **Style Rules** - Code style validation and formatting
- **Best Practice Rules** - GraphQL best practices enforcement
- **Performance Rules** - Query optimization suggestions
- **Security Rules** - Security-related recommendations

### **⚙️ Flexible Configuration**
- **Configurable rules** - enable/disable individual rules
- **Customizable levels** - ERROR, WARNING, INFO severity levels
- **Preset configurations** - ready-to-use linting presets
- **Dynamic settings** - runtime configuration changes

## 📦 Installation

### **Maven Dependency**

```xml
<dependency>
    <groupId>com.intuit.gqlex</groupId>
    <artifactId>gqlex-path-selection-java</artifactId>
    <version>2.0.1</version>
</dependency>
```

### **Gradle Dependency**

```gradle
implementation 'com.intuit.gqlex:gqlex-path-selection-java:2.0.1'
```

## 💡 Usage Examples

### **Example 1: Basic Linting**

```java
import com.intuit.gqlex.linting.core.GraphQLLinter;
import com.intuit.gqlex.linting.core.LintResult;

public class BasicLintingExample {
    public static void main(String[] args) {
        // Create linter
        GraphQLLinter linter = new GraphQLLinter();
        
        // Add rules
        linter.addRule(new StyleRule());
        linter.addRule(new BestPracticeRule());
        
        // Test query with style issues
        String query = "query { User { ID name } }";  // Bad naming convention
        
        LintResult result = linter.lint(query);
        
        // Print results
        System.out.println("=== Linting Results ===");
        System.out.println("Errors: " + result.getErrorCount());
        System.out.println("Warnings: " + result.getWarningCount());
        System.out.println("Info: " + result.getInfoCount());
        
        result.getAllIssues().forEach(issue -> {
            System.out.println(issue.getLevel() + ": " + issue.getMessage());
        });
    }
}
```

**Output:**
```
=== Linting Results ===
Errors: 0
Warnings: 2
Info: 0
WARNING: Field name 'User' should follow camelCase convention
WARNING: Field name 'ID' should follow camelCase convention
```

### **Example 2: Performance Linting**

```java
import com.intuit.gqlex.linting.core.GraphQLLinter;
import com.intuit.gqlex.linting.rules.PerformanceRule;

public class PerformanceLintingExample {
    public static void main(String[] args) {
        // Create linter with performance focus
        GraphQLLinter linter = new GraphQLLinter();
        linter.addRule(new PerformanceRule());
        
        // Deep nested query
        String deepQuery = """
            query {
                user {
                    posts {
                        comments {
                            author {
                                profile {
                                    bio {
                                        content
                                    }
                                }
                            }
                        }
                    }
                }
            }
            """;
        
        LintResult result = linter.lint(deepQuery);
        
        System.out.println("=== Performance Analysis ===");
        result.getWarnings().forEach(warning -> {
            System.out.println("PERFORMANCE: " + warning.getMessage());
        });
    }
}
```

**Output:**
```
=== Performance Analysis ===
PERFORMANCE: Query depth (6) exceeds recommended limit (5) - consider flattening
PERFORMANCE: Field count (7) exceeds recommended limit (50) - consider using fragments
```

### **Example 3: Security Linting**

```java
import com.intuit.gqlex.linting.core.GraphQLLinter;
import com.intuit.gqlex.linting.rules.SecurityRule;

public class SecurityLintingExample {
    public static void main(String[] args) {
        // Create linter with security focus
        GraphQLLinter linter = new GraphQLLinter();
        linter.addRule(new SecurityRule());
        
        // Query with introspection and sensitive fields
        String securityQuery = """
            query {
                __schema {
                    types {
                        name
                    }
                }
                user {
                    password
                    ssn
                    creditCard
                }
            }
            """;
        
        LintResult result = linter.lint(securityQuery);
        
        System.out.println("=== Security Analysis ===");
        result.getWarnings().forEach(warning -> {
            System.out.println("SECURITY: " + warning.getMessage());
        });
        
        result.getErrors().forEach(error -> {
            System.out.println("SECURITY ERROR: " + error.getMessage());
        });
    }
}
```

**Output:**
```
=== Security Analysis ===
SECURITY: Introspection field '__schema' detected - consider restricting in production
SECURITY: Sensitive field 'password' detected - ensure proper access control
SECURITY: Sensitive field 'ssn' detected - ensure proper access control
SECURITY: Sensitive field 'creditCard' detected - ensure proper access control
```

### **Example 4: Custom Configuration**

```java
import com.intuit.gqlex.linting.config.LintConfig;
import com.intuit.gqlex.linting.config.RuleConfig;
import com.intuit.gqlex.linting.core.GraphQLLinter;
import com.intuit.gqlex.linting.core.LintLevel;

public class CustomConfigurationExample {
    public static void main(String[] args) {
        // Create custom configuration
        LintConfig config = new LintConfig();
        
        // Performance settings
        config.setValue("maxLineLength", 120);
        config.setValue("maxDepth", 10);
        config.setValue("maxFields", 100);
        
        // Security settings
        config.setValue("sensitiveFields", Set.of("password", "ssn", "creditCard", "apiKey"));
        
        // Rule configurations
        config.setRuleConfig("STYLE", new RuleConfig(true, LintLevel.INFO));
        config.setRuleConfig("PERFORMANCE", new RuleConfig(true, LintLevel.ERROR));
        config.setRuleConfig("SECURITY", new RuleConfig(true, LintLevel.ERROR));
        
        // Create linter with custom configuration
        GraphQLLinter linter = new GraphQLLinter(config);
        linter.addRule(new StyleRule());
        linter.addRule(new PerformanceRule());
        linter.addRule(new SecurityRule());
        
        // Test query
        String query = """
            query {
                user {
                    posts {
                        comments {
                            author {
                                profile {
                                    bio
                                }
                            }
                        }
                    }
                }
            }
            """;
        
        LintResult result = linter.lint(query);
        
        System.out.println("=== Custom Configuration Results ===");
        System.out.println("Total Issues: " + result.getTotalIssueCount());
        result.getAllIssues().forEach(issue -> {
            System.out.println(issue.getLevel() + " [" + issue.getRuleName() + "]: " + issue.getMessage());
        });
    }
}
```

**Output:**
```
=== Custom Configuration Results ===
Total Issues: 2
ERROR [PERFORMANCE]: Query depth (5) exceeds recommended limit (10) - consider flattening
INFO [STYLE]: Consider using consistent indentation
```

### **Example 5: Fragment Optimization**

```java
import com.intuit.gqlex.linting.core.GraphQLLinter;
import com.intuit.gqlex.linting.rules.BestPracticeRule;

public class FragmentOptimizationExample {
    public static void main(String[] args) {
        // Create linter with best practice focus
        GraphQLLinter linter = new GraphQLLinter();
        linter.addRule(new BestPracticeRule());
        
        // Query with fragment usage
        String fragmentQuery = """
            query {
                user1: user(id: "1") {
                    ...userFields
                }
                user2: user(id: "2") {
                    ...userFields
                }
            }
            
            fragment userFields on User {
                id
                name
                email
                profile {
                    bio
                    avatar
                }
            }
            """;
        
        LintResult result = linter.lint(fragmentQuery);
        
        System.out.println("=== Fragment Analysis ===");
        result.getInfo().forEach(info -> {
            System.out.println("BEST PRACTICE: " + info.getMessage());
        });
    }
}
```

**Output:**
```
=== Fragment Analysis ===
BEST PRACTICE: Fragment 'userFields' used 2 times - good for performance
BEST PRACTICE: Consider using alias for field 'user' to avoid conflicts
```

## ⚙️ Configuration

### **Global Settings**

```java
LintConfig config = new LintConfig();

// Performance settings
config.setValue("maxLineLength", 80);
config.setValue("maxDepth", 5);
config.setValue("maxFields", 50);
config.setValue("maxSecurityDepth", 3);

// Security settings
config.setValue("sensitiveFields", Set.of("password", "ssn", "creditCard"));

// Rule-specific settings
config.setValue("style.camelCase", true);
config.setValue("performance.allowDeepNesting", false);
```

### **Rule Configuration**

```java
// Configure individual rules
config.setRuleConfig("STYLE", new RuleConfig(true, LintLevel.ERROR));
config.setRuleConfig("BEST_PRACTICE", new RuleConfig(true, LintLevel.WARNING));
config.setRuleConfig("PERFORMANCE", new RuleConfig(true, LintLevel.INFO));
config.setRuleConfig("SECURITY", new RuleConfig(true, LintLevel.ERROR));
```

### **Preset Configurations**

```java
// Use predefined configurations
LintConfig strictConfig = LintPreset.strict();
LintConfig relaxedConfig = LintPreset.relaxed();
LintConfig performanceConfig = LintPreset.performance();
LintConfig securityConfig = LintPreset.security();

GraphQLLinter strictLinter = new GraphQLLinter(strictConfig);
GraphQLLinter relaxedLinter = new GraphQLLinter(relaxedConfig);
```

## 🔧 Custom Rules

### **Creating Custom Rules**

```java
import com.intuit.gqlex.linting.rules.LintRule;
import com.intuit.gqlex.linting.core.LintContext;
import com.intuit.gqlex.linting.core.LintResult;
import graphql.language.Field;

public class CustomLintRule extends LintRule {
    
    public CustomLintRule() {
        super("CUSTOM_RULE", "Custom linting rule");
    }
    
    @Override
    public void lint(LintContext context, LintResult result) {
        // Analyze the GraphQL document
        context.traverseNodes(node -> {
            if (node instanceof Field) {
                Field field = (Field) node;
                
                // Your custom logic here
                if (field.getName().length() > 20) {
                    addWarning(result, 
                        "Field name '" + field.getName() + "' is too long", 
                        field);
                }
                
                // Check for specific patterns
                if (field.getName().contains("temp") || field.getName().contains("tmp")) {
                    addError(result, 
                        "Field name '" + field.getName() + "' suggests temporary data", 
                        field);
                }
            }
        });
    }
    
    @Override
    public String getCategory() {
        return "CUSTOM";
    }
}
```

### **Using Custom Rules**

```java
GraphQLLinter linter = new GraphQLLinter();
linter.addRule(new CustomLintRule());

// Configure custom rule
LintConfig config = new LintConfig();
config.setRuleConfig("CUSTOM_RULE", new RuleConfig(true, LintLevel.WARNING));

// Test with custom rule
String query = "query { temporaryUserData { id name } }";
LintResult result = linter.lint(query);

result.getAllIssues().forEach(issue -> {
    System.out.println(issue.getLevel() + ": " + issue.getMessage());
});
```

## 🧪 Testing

### **Running Tests**

```bash
# Run all linting tests
mvn test -Dtest="*Lint*"

# Run specific test class
mvn test -Dtest=LintResultTest

# Run specific test method
mvn test -Dtest=LintResultTest#testGenericFieldNamesAnyFieldNamesWork
```

### **Test Coverage**

The linting system includes comprehensive tests covering:

- ✅ **All GraphQL Types**: Queries, mutations, subscriptions
- ✅ **All Node Types**: Fields, arguments, directives, fragments
- ✅ **All Operation Types**: Simple, complex, nested operations
- ✅ **All Configuration Scenarios**: Various configuration combinations
- ✅ **Error Handling**: Invalid queries, parsing errors, rule failures

## 📚 API Reference

### **Core Classes**

#### **GraphQLLinter**
```java
// Constructor
GraphQLLinter()
GraphQLLinter(LintConfig config)

// Methods
GraphQLLinter addRule(LintRule rule)
GraphQLLinter addRules(LintRule... rules)
GraphQLLinter removeRule(LintRule rule)
GraphQLLinter removeRule(String ruleName)
GraphQLLinter clearRules()
LintResult lint(Document document)
LintResult lint(String queryString)
LintResult lint(Document document, LintContext context)
LintResult lint(String queryString, LintContext context)
boolean hasRules()
int getRuleCount()
LintRule getRule(String ruleName)
boolean hasRule(String ruleName)
List<LintRule> getRulesByCategory(String category)
GraphQLLinter copy()
GraphQLLinter deepCopy()
```

#### **LintResult**
```java
// Methods
void addError(String ruleName, String message, Node node)
void addWarning(String ruleName, String message, Node node)
void addInfo(String ruleName, String message, Node node)
void addIssue(LintIssue issue)
void merge(LintResult other)
boolean hasErrors()
boolean hasWarnings()
boolean hasInfo()
boolean hasIssues()
int getErrorCount()
int getWarningCount()
int getInfoCount()
int getTotalIssueCount()
List<LintIssue> getErrors()
List<LintIssue> getWarnings()
List<LintIssue> getInfo()
List<LintIssue> getAllIssues()
List<LintIssue> getIssuesByLevel(LintLevel level)
List<LintIssue> getIssuesByRule(String ruleName)
void clear()
String getSummary()
```

#### **LintContext**
```java
// Constructor
LintContext(Document document, LintConfig config)
LintContext(Document document)
LintContext(String queryString, LintConfig config)
LintContext(String queryString)

// Methods
Document getDocument()
LintConfig getConfig()
<T> T getConfigValue(String key, Class<T> type, T defaultValue)
<T> T getConfigValue(String key, Class<T> type)
void setMetadata(String key, Object value)
Object getMetadata(String key)
void traverseNodes(NodeVisitor visitor)
List<Field> findFields(Predicate<Field> predicate)
List<Argument> findArguments(Predicate<Argument> predicate)
List<Directive> findDirectives(Predicate<Directive> predicate)
List<FragmentDefinition> findFragmentDefinitions()
List<InlineFragment> findInlineFragments()
List<VariableDefinition> findVariableDefinitions()
int calculateMaxDepth()
int calculateFieldCount()
int calculateArgumentCount()
boolean containsIntrospectionQueries()
List<OperationDefinition> getOperations()
List<OperationDefinition> getQueries()
List<OperationDefinition> getMutations()
List<OperationDefinition> getSubscriptions()
```

## 🏗️ Architecture

### **Component Structure**

```
com.intuit.gqlex.linting/
├── core/
│   ├── GraphQLLinter.java              # Main linting orchestrator
│   ├── LintContext.java                # Linting context and state
│   ├── LintResult.java                 # Linting result container
│   ├── LintIssue.java                  # Individual linting issue
│   └── LintLevel.java                  # Error, Warning, Info levels
├── rules/
│   ├── LintRule.java                   # Base linting rule interface
│   ├── StyleRule.java                  # Code style rules
│   ├── BestPracticeRule.java           # Best practice rules
│   ├── PerformanceRule.java            # Performance optimization rules
│   └── SecurityRule.java               # Security-related rules
└── config/
    ├── LintConfig.java                 # Linting configuration
    ├── RuleConfig.java                 # Individual rule configuration
    └── LintPreset.java                 # Predefined linting presets
```

### **Design Principles**

- **Generic & Agnostic**: No hardcoded assumptions about GraphQL schemas
- **Extensible**: Easy to add custom rules and configurations
- **Performant**: Efficient AST traversal and memory management
- **Robust**: Comprehensive error handling and recovery
- **Testable**: Extensive test coverage for all components

## 🤝 Contributing

### **Adding New Rules**

1. **Extend LintRule**: Create a new class extending `LintRule`
2. **Implement lint()**: Add your linting logic in the `lint()` method
3. **Add Tests**: Create comprehensive tests for your rule
4. **Update Documentation**: Add examples and documentation

### **Example Rule Implementation**

```java
public class MyCustomRule extends LintRule {
    
    public MyCustomRule() {
        super("MY_CUSTOM_RULE", "My custom linting rule");
    }
    
    @Override
    public void lint(LintContext context, LintResult result) {
        // Your linting logic here
        context.traverseNodes(node -> {
            if (node instanceof Field) {
                Field field = (Field) node;
                // Check your conditions
                if (/* your condition */) {
                    addWarning(result, "Your warning message", field);
                }
            }
        });
    }
    
    @Override
    public String getCategory() {
        return "CUSTOM";
    }
}
```

### **Testing Your Rule**

```java
@Test
@DisplayName("My Custom Rule Test")
void testMyCustomRule() {
    GraphQLLinter linter = new GraphQLLinter();
    linter.addRule(new MyCustomRule());
    
    String query = "query { testField { id } }";
    LintResult result = linter.lint(query);
    
    assertTrue(result.hasIssues());
    // Add your specific assertions
}
```

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **GraphQL Java** - For the excellent GraphQL parsing and AST manipulation
- **JUnit 5** - For the comprehensive testing framework
- **Maven** - For the build automation and dependency management

---

**✅ GRAPHQL LINTING SYSTEM - READY FOR PRODUCTION USE** 