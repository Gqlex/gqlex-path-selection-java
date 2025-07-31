# GraphQL Linting Core Components

[![Java](https://img.shields.io/badge/Java-11+-blue.svg)](https://openjdk.java.net/)
[![Tests](https://img.shields.io/badge/Tests-Passing-brightgreen.svg)](https://github.com/Gqlex/gqlex-path-selection-java)

> **Core Components for GraphQL Linting System**

The core components provide the foundation for the GraphQL linting system, including the main linter, context management, result handling, and issue representation.

## 📋 Table of Contents

- [🏗️ Architecture](#️-architecture)
- [🎯 Core Components](#-core-components)
- [💡 Usage Examples](#-usage-examples)
- [📚 API Reference](#-api-reference)
- [🧪 Testing](#-testing)

## 🏗️ Architecture

### **Component Overview**

```
com.intuit.gqlex.linting.core/
├── GraphQLLinter.java              # Main linting orchestrator
├── LintContext.java                # Linting context and state
├── LintResult.java                 # Linting result container
├── LintIssue.java                  # Individual linting issue
└── LintLevel.java                  # Error, Warning, Info levels
```

### **Component Relationships**

```
GraphQLLinter
    ├── Uses LintContext for rule execution
    ├── Collects LintResult from rules
    ├── Manages LintRule instances
    └── Handles LintIssue creation

LintContext
    ├── Provides document access
    ├── Manages configuration
    ├── Offers utility methods
    └── Supports AST traversal

LintResult
    ├── Collects LintIssue instances
    ├── Provides filtering methods
    ├── Offers summary statistics
    └── Supports result merging

LintIssue
    ├── Represents individual problems
    ├── Contains severity levels
    ├── Includes source location
    └── Links to GraphQL nodes
```

## 🎯 Core Components

### **GraphQLLinter**

The main orchestrator for GraphQL linting operations.

**Key Features:**
- **Rule Management**: Add, remove, and configure linting rules
- **Document Processing**: Parse and lint GraphQL documents
- **Error Handling**: Robust error recovery and reporting
- **Fluent API**: Method chaining for easy configuration

**Example:**
```java
GraphQLLinter linter = new GraphQLLinter()
    .addRule(new StyleRule())
    .addRule(new BestPracticeRule())
    .addRule(new PerformanceRule());

LintResult result = linter.lint("query { user { id name } }");
```

### **LintContext**

Provides context and utility methods for linting rules.

**Key Features:**
- **Document Access**: Safe access to GraphQL document structure
- **Configuration Management**: Access to linting configuration
- **AST Traversal**: Efficient node traversal utilities
- **Analysis Tools**: Built-in analysis methods for common patterns

**Example:**
```java
LintContext context = new LintContext(document, config);

// Find all fields with specific criteria
List<Field> fields = context.findFields(field -> 
    field.getName().startsWith("temp"));

// Calculate document metrics
int maxDepth = context.calculateMaxDepth();
int fieldCount = context.calculateFieldCount();
```

### **LintResult**

Collects and manages linting issues found during analysis.

**Key Features:**
- **Issue Collection**: Separate collections for errors, warnings, and info
- **Filtering**: Filter issues by level, rule, or other criteria
- **Statistics**: Count and summary information
- **Merging**: Combine results from multiple linting operations

**Example:**
```java
LintResult result = new LintResult();

// Add issues
result.addError("RULE_NAME", "Error message", node);
result.addWarning("RULE_NAME", "Warning message", node);
result.addInfo("RULE_NAME", "Info message", node);

// Check results
if (result.hasErrors()) {
    System.out.println("Found " + result.getErrorCount() + " errors");
}

// Filter issues
List<LintIssue> warnings = result.getWarnings();
List<LintIssue> ruleIssues = result.getIssuesByRule("RULE_NAME");
```

### **LintIssue**

Represents a single linting issue found during analysis.

**Key Features:**
- **Severity Levels**: ERROR, WARNING, INFO classification
- **Source Location**: Line and column information
- **Node Association**: Links to specific GraphQL nodes
- **Rule Attribution**: Tracks which rule generated the issue

**Example:**
```java
LintIssue issue = new LintIssue("RULE_NAME", "Issue message", LintLevel.WARNING, node);

// Access issue properties
String ruleName = issue.getRuleName();
String message = issue.getMessage();
LintLevel level = issue.getLevel();
int line = issue.getLine();
int column = issue.getColumn();

// Check severity
boolean isError = issue.isError();
boolean isWarning = issue.isWarning();
boolean isInfo = issue.isInfo();
```

### **LintLevel**

Defines the severity levels for linting issues.

**Levels:**
- **ERROR**: Critical issues that should be fixed
- **WARNING**: Issues that should be addressed
- **INFO**: Informational suggestions

**Example:**
```java
LintLevel level = LintLevel.WARNING;

// Compare levels
boolean isMoreSevere = LintLevel.ERROR.isMoreSevereThan(level);
boolean isAtLeastAsSevere = level.isAtLeastAsSevereAs(LintLevel.INFO);
```

## 💡 Usage Examples

### **Example 1: Basic Linting Workflow**

```java
import com.intuit.gqlex.linting.core.*;

public class BasicLintingExample {
    public static void main(String[] args) {
        // Create linter
        GraphQLLinter linter = new GraphQLLinter();
        
        // Add rules
        linter.addRule(new StyleRule());
        linter.addRule(new BestPracticeRule());
        
        // Lint query
        String query = "query { User { ID name } }";
        LintResult result = linter.lint(query);
        
        // Process results
        System.out.println("=== Linting Results ===");
        System.out.println("Total Issues: " + result.getTotalIssueCount());
        
        if (result.hasErrors()) {
            System.out.println("Errors: " + result.getErrorCount());
            result.getErrors().forEach(error -> 
                System.out.println("ERROR: " + error.getMessage()));
        }
        
        if (result.hasWarnings()) {
            System.out.println("Warnings: " + result.getWarningCount());
            result.getWarnings().forEach(warning -> 
                System.out.println("WARNING: " + warning.getMessage()));
        }
        
        if (result.hasInfo()) {
            System.out.println("Info: " + result.getInfoCount());
            result.getInfo().forEach(info -> 
                System.out.println("INFO: " + info.getMessage()));
        }
    }
}
```

### **Example 2: Advanced Context Usage**

```java
import com.intuit.gqlex.linting.core.*;
import graphql.language.*;

public class AdvancedContextExample {
    public static void main(String[] args) {
        // Create context
        String query = """
            query GetUser($id: ID!) {
                user(id: $id) {
                    id
                    name
                    posts {
                        id
                        title
                    }
                }
            }
            """;
        
        LintContext context = new LintContext(query);
        
        // Analyze document structure
        System.out.println("=== Document Analysis ===");
        System.out.println("Max Depth: " + context.calculateMaxDepth());
        System.out.println("Field Count: " + context.calculateFieldCount());
        System.out.println("Argument Count: " + context.calculateArgumentCount());
        
        // Find specific nodes
        List<Field> userFields = context.findFields(field -> 
            field.getName().equals("user"));
        System.out.println("User fields found: " + userFields.size());
        
        List<VariableDefinition> variables = context.findVariableDefinitions();
        System.out.println("Variables defined: " + variables.size());
        
        // Check for introspection
        boolean hasIntrospection = context.containsIntrospectionQueries();
        System.out.println("Contains introspection: " + hasIntrospection);
        
        // Get operations
        List<OperationDefinition> operations = context.getOperations();
        System.out.println("Operations: " + operations.size());
        
        List<OperationDefinition> queries = context.getQueries();
        System.out.println("Queries: " + queries.size());
    }
}
```

### **Example 3: Custom Issue Creation**

```java
import com.intuit.gqlex.linting.core.*;
import graphql.language.*;

public class CustomIssueExample {
    public static void main(String[] args) {
        // Create result container
        LintResult result = new LintResult();
        
        // Parse document
        String query = "query { user { id name } }";
        Document document = new Parser().parseDocument(query);
        
        // Find nodes to create issues for
        document.accept(new NodeVisitorStub() {
            @Override
            public void visitField(Field field) {
                // Create custom issues
                if (field.getName().length() > 10) {
                    result.addWarning("CUSTOM_RULE", 
                        "Field name '" + field.getName() + "' is too long", 
                        field);
                }
                
                if (field.getName().equals("id")) {
                    result.addInfo("CUSTOM_RULE", 
                        "Consider if 'id' field is necessary", 
                        field);
                }
            }
        });
        
        // Display results
        System.out.println("=== Custom Issues ===");
        result.getAllIssues().forEach(issue -> {
            System.out.println(issue.getLevel() + " [" + issue.getRuleName() + "]: " + issue.getMessage());
            if (issue.getLine() > 0) {
                System.out.println("  Location: line " + issue.getLine() + ", column " + issue.getColumn());
            }
        });
    }
}
```

### **Example 4: Result Filtering and Analysis**

```java
import com.intuit.gqlex.linting.core.*;

public class ResultAnalysisExample {
    public static void main(String[] args) {
        // Create linter and get results
        GraphQLLinter linter = new GraphQLLinter();
        linter.addRule(new StyleRule());
        linter.addRule(new BestPracticeRule());
        
        String query = "query { user { id name email } }";
        LintResult result = linter.lint(query);
        
        // Analyze results by level
        System.out.println("=== Analysis by Level ===");
        
        List<LintIssue> errors = result.getIssuesByLevel(LintLevel.ERROR);
        System.out.println("Errors: " + errors.size());
        errors.forEach(error -> System.out.println("  ERROR: " + error.getMessage()));
        
        List<LintIssue> warnings = result.getIssuesByLevel(LintLevel.WARNING);
        System.out.println("Warnings: " + warnings.size());
        warnings.forEach(warning -> System.out.println("  WARNING: " + warning.getMessage()));
        
        List<LintIssue> info = result.getIssuesByLevel(LintLevel.INFO);
        System.out.println("Info: " + info.size());
        info.forEach(i -> System.out.println("  INFO: " + i.getMessage()));
        
        // Analyze results by rule
        System.out.println("\n=== Analysis by Rule ===");
        
        List<LintIssue> styleIssues = result.getIssuesByRule("STYLE");
        System.out.println("Style issues: " + styleIssues.size());
        
        List<LintIssue> bestPracticeIssues = result.getIssuesByRule("BEST_PRACTICE");
        System.out.println("Best practice issues: " + bestPracticeIssues.size());
        
        // Get summary
        System.out.println("\n=== Summary ===");
        System.out.println(result.getSummary());
    }
}
```

## 📚 API Reference

### **GraphQLLinter**

#### **Constructors**
```java
GraphQLLinter()                           // Default configuration
GraphQLLinter(LintConfig config)          // Custom configuration
```

#### **Rule Management**
```java
GraphQLLinter addRule(LintRule rule)                    // Add single rule
GraphQLLinter addRules(LintRule... rules)               // Add multiple rules
GraphQLLinter addRules(List<LintRule> rules)            // Add rule list
GraphQLLinter removeRule(LintRule rule)                 // Remove rule
GraphQLLinter removeRule(String ruleName)               // Remove by name
GraphQLLinter clearRules()                              // Clear all rules
```

#### **Linting Operations**
```java
LintResult lint(Document document)                       // Lint document
LintResult lint(String queryString)                     // Lint query string
LintResult lint(Document document, LintContext context)  // Lint with context
LintResult lint(String queryString, LintContext context) // Lint string with context
```

#### **Information Methods**
```java
boolean hasRules()                                       // Check if has rules
int getRuleCount()                                       // Get rule count
LintRule getRule(String ruleName)                       // Get rule by name
boolean hasRule(String ruleName)                        // Check if has rule
List<LintRule> getRulesByCategory(String category)      // Get rules by category
```

#### **Utility Methods**
```java
GraphQLLinter copy()                                     // Shallow copy
GraphQLLinter deepCopy()                                 // Deep copy
LintConfig getConfig()                                   // Get configuration
```

### **LintContext**

#### **Constructors**
```java
LintContext(Document document, LintConfig config)        // Document with config
LintContext(Document document)                           // Document with default config
LintContext(String queryString, LintConfig config)       // String with config
LintContext(String queryString)                          // String with default config
```

#### **Document Access**
```java
Document getDocument()                                   // Get document
LintConfig getConfig()                                   // Get configuration
```

#### **Configuration Access**
```java
<T> T getConfigValue(String key, Class<T> type)         // Get config value
<T> T getConfigValue(String key, Class<T> type, T defaultValue) // Get with default
```

#### **Metadata Management**
```java
void setMetadata(String key, Object value)              // Set metadata
Object getMetadata(String key)                           // Get metadata
```

#### **AST Traversal**
```java
void traverseNodes(NodeVisitor visitor)                  // Traverse all nodes
```

#### **Node Finding**
```java
List<Field> findFields(Predicate<Field> predicate)       // Find fields
List<Argument> findArguments(Predicate<Argument> predicate) // Find arguments
List<Directive> findDirectives(Predicate<Directive> predicate) // Find directives
List<FragmentDefinition> findFragmentDefinitions()       // Find fragment definitions
List<InlineFragment> findInlineFragments()               // Find inline fragments
List<VariableDefinition> findVariableDefinitions()       // Find variable definitions
```

#### **Analysis Methods**
```java
int calculateMaxDepth()                                  // Calculate max depth
int calculateFieldCount()                                // Calculate field count
int calculateArgumentCount()                             // Calculate argument count
boolean containsIntrospectionQueries()                   // Check for introspection
```

#### **Operation Access**
```java
List<OperationDefinition> getOperations()                // Get all operations
List<OperationDefinition> getQueries()                   // Get queries
List<OperationDefinition> getMutations()                 // Get mutations
List<OperationDefinition> getSubscriptions()             // Get subscriptions
```

### **LintResult**

#### **Constructors**
```java
LintResult()                                             // Empty result
```

#### **Issue Addition**
```java
void addError(String ruleName, String message, Node node)    // Add error
void addWarning(String ruleName, String message, Node node)  // Add warning
void addInfo(String ruleName, String message, Node node)     // Add info
void addIssue(LintIssue issue)                               // Add issue
```

#### **Result Management**
```java
void merge(LintResult other)                               // Merge results
void clear()                                               // Clear all issues
```

#### **Status Checking**
```java
boolean hasErrors()                                        // Check for errors
boolean hasWarnings()                                      // Check for warnings
boolean hasInfo()                                          // Check for info
boolean hasIssues()                                        // Check for any issues
```

#### **Counting**
```java
int getErrorCount()                                        // Get error count
int getWarningCount()                                      // Get warning count
int getInfoCount()                                         // Get info count
int getTotalIssueCount()                                   // Get total count
```

#### **Issue Retrieval**
```java
List<LintIssue> getErrors()                               // Get all errors
List<LintIssue> getWarnings()                             // Get all warnings
List<LintIssue> getInfo()                                 // Get all info
List<LintIssue> getAllIssues()                            // Get all issues
List<LintIssue> getIssuesByLevel(LintLevel level)         // Get by level
List<LintIssue> getIssuesByRule(String ruleName)          // Get by rule
```

#### **Information**
```java
String getSummary()                                        // Get summary string
```

### **LintIssue**

#### **Constructors**
```java
LintIssue(String ruleName, String message, LintLevel level, Node node) // Full constructor
LintIssue(String ruleName, String message, LintLevel level)            // Without node
```

#### **Static Factory Methods**
```java
static LintIssue syntax(String ruleName, String message)  // Create syntax error
static LintIssue generic(String ruleName, String message) // Create generic issue
```

#### **Property Access**
```java
String getRuleName()                                       // Get rule name
String getMessage()                                        // Get message
LintLevel getLevel()                                       // Get level
Node getNode()                                             // Get associated node
String getPath()                                           // Get node path
int getLine()                                              // Get line number
int getColumn()                                            // Get column number
```

#### **Level Checking**
```java
boolean isError()                                          // Check if error
boolean isWarning()                                        // Check if warning
boolean isInfo()                                           // Check if info
```

### **LintLevel**

#### **Constants**
```java
LintLevel.ERROR                                            // Error level
LintLevel.WARNING                                          // Warning level
LintLevel.INFO                                             // Info level
```

#### **Comparison Methods**
```java
boolean isMoreSevereThan(LintLevel other)                 // Check if more severe
boolean isLessSevereThan(LintLevel other)                 // Check if less severe
boolean isAtLeastAsSevereAs(LintLevel other)              // Check if at least as severe
```

## 🧪 Testing

### **Running Core Tests**

```bash
# Run all core linting tests
mvn test -Dtest="*Lint*"

# Run specific test classes
mvn test -Dtest=LintResultTest
mvn test -Dtest=GraphQLLinterTest

# Run specific test methods
mvn test -Dtest=LintResultTest#testGenericFieldNamesAnyFieldNamesWork
```

### **Test Coverage**

The core components include comprehensive tests covering:

- ✅ **GraphQLLinter**: Rule management, linting operations, error handling
- ✅ **LintContext**: AST traversal, node finding, analysis methods
- ✅ **LintResult**: Issue management, filtering, statistics
- ✅ **LintIssue**: Issue creation, property access, level checking
- ✅ **LintLevel**: Level comparison and validation

### **Test Examples**

```java
@Test
@DisplayName("Generic Field Names - Any Field Names Work")
void testGenericFieldNamesAnyFieldNamesWork() {
    String[] fieldNames = {
        "user", "product", "order", "invoice", "customer", "supplier"
    };
    
    for (String fieldName : fieldNames) {
        String query = String.format("query { %s { id name } }", fieldName);
        LintResult result = linter.lint(query);
        
        assertNotNull(result, "Linting should complete for field: " + fieldName);
        assertTrue(result.hasIssues(), "Should have issues for field: " + fieldName);
    }
}
```

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**✅ CORE COMPONENTS - PRODUCTION READY** 