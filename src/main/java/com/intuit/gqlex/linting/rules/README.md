# GraphQL Linting Rules

[![Java](https://img.shields.io/badge/Java-11+-blue.svg)](https://openjdk.java.net/)
[![Tests](https://img.shields.io/badge/Tests-Passing-brightgreen.svg)](https://github.com/Gqlex/gqlex-path-selection-java)

> **Linting Rules for GraphQL Code Quality Analysis**

The linting rules package provides a comprehensive set of rules for analyzing GraphQL documents, including style enforcement, best practices, performance optimization, and security validation.

## 📋 Table of Contents

- [🎯 Rule Categories](#-rule-categories)
- [🔧 Rule Implementation](#-rule-implementation)
- [💡 Usage Examples](#-usage-examples)
- [📚 API Reference](#-api-reference)
- [🧪 Testing](#-testing)
- [🔧 Custom Rules](#-custom-rules)

## 🎯 Rule Categories

### **Style Rules** 🎨
Enforce consistent code style and formatting across GraphQL documents.

**Examples:**
- **Naming Conventions** - Field and argument naming patterns
- **Spacing Rules** - Consistent spacing around operators
- **Indentation** - Proper indentation levels
- **Line Length** - Maximum line length enforcement

### **Best Practice Rules** 📚
Enforce GraphQL best practices and patterns for maintainable code.

**Examples:**
- **Alias Usage** - Proper alias usage and conflict avoidance
- **Fragment Optimization** - Efficient fragment usage
- **Selection Set Optimization** - Optimal field selection
- **Variable Usage** - Proper variable definition and usage

### **Performance Rules** ⚡
Identify potential performance issues and optimization opportunities.

**Examples:**
- **Query Depth** - Maximum query depth limits
- **Field Count** - Total field count optimization
- **Fragment Usage** - Fragment reuse analysis
- **Selection Optimization** - Over-fetching detection

### **Security Rules** 🔒
Identify potential security vulnerabilities and risks.

**Examples:**
- **Introspection Usage** - Production introspection detection
- **Sensitive Fields** - Sensitive data access warnings
- **Argument Validation** - Input validation patterns
- **Depth Limits** - DoS protection through depth limits

## 🔧 Rule Implementation

### **Base Rule Interface**

All linting rules extend the `LintRule` base class:

```java
public abstract class LintRule {
    protected final String name;
    protected final LintLevel level;
    protected final String description;
    
    // Constructor
    protected LintRule(String name, LintLevel level, String description);
    protected LintRule(String name, String description); // Defaults to WARNING
    
    // Abstract method to implement
    public abstract void lint(LintContext context, LintResult result);
    
    // Utility methods
    public String getName();
    public LintLevel getLevel();
    public String getDescription();
    public boolean isEnabled(LintContext context);
    public String getCategory();
}
```

### **Rule Implementation Pattern**

```java
public class MyCustomRule extends LintRule {
    
    public MyCustomRule() {
        super("MY_CUSTOM_RULE", "My custom linting rule");
    }
    
    @Override
    public void lint(LintContext context, LintResult result) {
        // 1. Traverse the document
        context.traverseNodes(node -> {
            // 2. Check specific conditions
            if (node instanceof Field) {
                Field field = (Field) node;
                
                // 3. Apply your logic
                if (field.getName().length() > 20) {
                    // 4. Add issues to result
                    addWarning(result, 
                        "Field name '" + field.getName() + "' is too long", 
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

## 💡 Usage Examples

### **Example 1: Style Rule Implementation**

```java
import com.intuit.gqlex.linting.rules.LintRule;
import com.intuit.gqlex.linting.core.LintContext;
import com.intuit.gqlex.linting.core.LintResult;
import graphql.language.*;

public class StyleRule extends LintRule {
    
    public StyleRule() {
        super("STYLE", "Enforces GraphQL code style conventions");
    }
    
    @Override
    public void lint(LintContext context, LintResult result) {
        Document document = context.getDocument();
        
        // Validate naming conventions
        validateNamingConventions(document, context, result);
        
        // Validate consistent spacing
        validateConsistentSpacing(document, context, result);
        
        // Validate indentation
        validateIndentation(document, context, result);
        
        // Validate line length
        validateLineLength(document, context, result);
    }
    
    private void validateNamingConventions(Document document, LintContext context, LintResult result) {
        context.traverseNodes(node -> {
            if (node instanceof Field) {
                Field field = (Field) node;
                String fieldName = field.getName();
                
                // Check camelCase convention
                if (!fieldName.matches("^[a-z][a-zA-Z0-9]*$")) {
                    addWarning(result, 
                        String.format("Field name '%s' should follow camelCase convention", fieldName), 
                        field);
                }
            }
            
            if (node instanceof Argument) {
                Argument argument = (Argument) node;
                String argName = argument.getName();
                
                // Check camelCase convention
                if (!argName.matches("^[a-z][a-zA-Z0-9]*$")) {
                    addWarning(result, 
                        String.format("Argument name '%s' should follow camelCase convention", argName), 
                        argument);
                }
            }
        });
    }
    
    private void validateConsistentSpacing(Document document, LintContext context, LintResult result) {
        String queryString = AstPrinter.printAst(document);
        
        // Check for multiple consecutive spaces
        if (queryString.contains("  ")) {
            addInfo(result, 
                "Multiple consecutive spaces detected - use consistent indentation", 
                document);
        }
        
        // Check for missing spaces around operators
        if (queryString.contains(":") && !queryString.contains(" : ")) {
            addWarning(result, 
                "Missing spaces around colon - use consistent spacing", 
                document);
        }
    }
    
    private void validateIndentation(Document document, LintContext context, LintResult result) {
        String queryString = AstPrinter.printAst(document);
        String[] lines = queryString.split("\n");
        
        int expectedIndent = 0;
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            
            int actualIndent = line.length() - line.trim().length();
            if (actualIndent % 2 != 0) {
                addWarning(result, 
                    "Inconsistent indentation - use 2 spaces per level", 
                    document);
                break;
            }
        }
    }
    
    private void validateLineLength(Document document, LintContext context, LintResult result) {
        String queryString = AstPrinter.printAst(document);
        String[] lines = queryString.split("\n");
        
        int maxLineLength = context.getConfigValue("maxLineLength", Integer.class, 80);
        
        for (String line : lines) {
            if (line.length() > maxLineLength) {
                addWarning(result, 
                    String.format("Line length (%d) exceeds recommended limit (%d)", 
                        line.length(), maxLineLength), 
                    document);
                break;
            }
        }
    }
    
    @Override
    public String getCategory() {
        return "STYLE";
    }
}
```

### **Example 2: Best Practice Rule Implementation**

```java
import com.intuit.gqlex.linting.rules.LintRule;
import com.intuit.gqlex.linting.core.LintContext;
import com.intuit.gqlex.linting.core.LintResult;
import graphql.language.*;
import java.util.*;
import java.util.stream.Collectors;

public class BestPracticeRule extends LintRule {
    
    public BestPracticeRule() {
        super("BEST_PRACTICE", "Enforces GraphQL best practices");
    }
    
    @Override
    public void lint(LintContext context, LintResult result) {
        Document document = context.getDocument();
        
        // Validate alias usage
        validateAliasUsage(document, context, result);
        
        // Validate fragment usage
        validateFragmentUsage(document, context, result);
        
        // Validate selection set optimization
        validateSelectionSetOptimization(document, context, result);
        
        // Validate variable usage
        validateVariableUsage(document, context, result);
    }
    
    private void validateAliasUsage(Document document, LintContext context, LintResult result) {
        context.traverseNodes(node -> {
            if (node instanceof Field) {
                Field field = (Field) node;
                
                // Check for redundant aliases
                if (field.getAlias() != null && field.getAlias().equals(field.getName())) {
                    addInfo(result, 
                        String.format("Alias '%s' is redundant - same as field name", field.getAlias()), 
                        field);
                }
                
                // Check for missing aliases in conflicts
                if (field.getAlias() == null && hasConflictingFields(field, document)) {
                    addWarning(result, 
                        String.format("Consider using alias for field '%s' to avoid conflicts", field.getName()), 
                        field);
                }
            }
        });
    }
    
    private void validateFragmentUsage(Document document, LintContext context, LintResult result) {
        List<FragmentDefinition> fragments = document.getDefinitions().stream()
            .filter(def -> def instanceof FragmentDefinition)
            .map(def -> (FragmentDefinition) def)
            .collect(Collectors.toList());
            
        // Check for too many fragments
        if (fragments.size() > 5) {
            addWarning(result, 
                String.format("Consider consolidating %d fragments for better maintainability", fragments.size()), 
                document);
        }
        
        // Check for unused fragments
        Set<String> usedFragments = findUsedFragments(document);
        for (FragmentDefinition fragment : fragments) {
            if (!usedFragments.contains(fragment.getName())) {
                addWarning(result, 
                    String.format("Fragment '%s' is defined but never used", fragment.getName()), 
                    fragment);
            }
        }
    }
    
    private void validateSelectionSetOptimization(Document document, LintContext context, LintResult result) {
        context.traverseNodes(node -> {
            if (node instanceof SelectionSet) {
                SelectionSet selectionSet = (SelectionSet) node;
                
                // Check for large selection sets
                if (selectionSet.getSelections().size() > 10) {
                    addWarning(result, 
                        String.format("Large selection set with %d fields - consider using fragments", 
                            selectionSet.getSelections().size()), 
                        selectionSet);
                }
                
                // Check for duplicate fields
                Set<String> fieldNames = new HashSet<>();
                for (Selection selection : selectionSet.getSelections()) {
                    if (selection instanceof Field) {
                        Field field = (Field) selection;
                        String fieldKey = field.getAlias() != null ? field.getAlias() : field.getName();
                        
                        if (!fieldNames.add(fieldKey)) {
                            addError(result, 
                                String.format("Duplicate field '%s' in selection set", fieldKey), 
                                field);
                        }
                    }
                }
            }
        });
    }
    
    private void validateVariableUsage(Document document, LintContext context, LintResult result) {
        List<VariableDefinition> variables = document.getDefinitions().stream()
            .filter(def -> def instanceof OperationDefinition)
            .map(def -> (OperationDefinition) def)
            .flatMap(op -> op.getVariableDefinitions().stream())
            .collect(Collectors.toList());
            
        // Check for unused variables
        Set<String> usedVariables = findUsedVariables(document);
        for (VariableDefinition variable : variables) {
            if (!usedVariables.contains(variable.getName())) {
                addWarning(result, 
                    String.format("Variable '$%s' is defined but never used", variable.getName()), 
                    variable);
            }
        }
    }
    
    private boolean hasConflictingFields(Field field, Document document) {
        String fieldName = field.getName();
        AtomicInteger count = new AtomicInteger(0);
        
        document.accept(new NodeVisitorStub() {
            @Override
            public void visitField(Field node) {
                if (node.getName().equals(fieldName)) {
                    count.incrementAndGet();
                }
            }
        });
        
        return count.get() > 1;
    }
    
    private Set<String> findUsedFragments(Document document) {
        Set<String> usedFragments = new HashSet<>();
        
        document.accept(new NodeVisitorStub() {
            @Override
            public void visitFragmentSpread(FragmentSpread node) {
                usedFragments.add(node.getName());
            }
        });
        
        return usedFragments;
    }
    
    private Set<String> findUsedVariables(Document document) {
        Set<String> usedVariables = new HashSet<>();
        
        document.accept(new NodeVisitorStub() {
            @Override
            public void visitVariable(Variable node) {
                usedVariables.add(node.getName());
            }
        });
        
        return usedVariables;
    }
    
    @Override
    public String getCategory() {
        return "BEST_PRACTICE";
    }
}
```

### **Example 3: Performance Rule Implementation**

```java
import com.intuit.gqlex.linting.rules.LintRule;
import com.intuit.gqlex.linting.core.LintContext;
import com.intuit.gqlex.linting.core.LintResult;
import graphql.language.*;

public class PerformanceRule extends LintRule {
    
    public PerformanceRule() {
        super("PERFORMANCE", "Suggests performance optimizations");
    }
    
    @Override
    public void lint(LintContext context, LintResult result) {
        Document document = context.getDocument();
        
        // Validate query depth
        validateQueryDepth(document, context, result);
        
        // Validate field count
        validateFieldCount(document, context, result);
        
        // Validate fragment optimization
        validateFragmentOptimization(document, context, result);
        
        // Validate selection optimization
        validateSelectionOptimization(document, context, result);
    }
    
    private void validateQueryDepth(Document document, LintContext context, LintResult result) {
        int maxDepth = context.getConfigValue("maxDepth", Integer.class, 5);
        int actualDepth = context.calculateMaxDepth();
        
        if (actualDepth > maxDepth) {
            addWarning(result, 
                String.format("Query depth (%d) exceeds recommended limit (%d) - consider flattening", 
                    actualDepth, maxDepth), 
                document);
        }
    }
    
    private void validateFieldCount(Document document, LintContext context, LintResult result) {
        int maxFields = context.getConfigValue("maxFields", Integer.class, 50);
        int actualFields = context.calculateFieldCount();
        
        if (actualFields > maxFields) {
            addWarning(result, 
                String.format("Field count (%d) exceeds recommended limit (%d) - consider using fragments", 
                    actualFields, maxFields), 
                document);
        }
    }
    
    private void validateFragmentOptimization(Document document, LintContext context, LintResult result) {
        Map<String, Integer> fragmentUsage = calculateFragmentUsage(document);
        
        for (Map.Entry<String, Integer> entry : fragmentUsage.entrySet()) {
            if (entry.getValue() > 3) {
                addInfo(result, 
                    String.format("Fragment '%s' used %d times - good for performance", 
                        entry.getKey(), entry.getValue()), 
                    document);
            }
        }
    }
    
    private void validateSelectionOptimization(Document document, LintContext context, LintResult result) {
        context.traverseNodes(node -> {
            if (node instanceof Field) {
                Field field = (Field) node;
                
                // Check for potential over-fetching
                if (field.getSelectionSet() != null && field.getSelectionSet().getSelections().size() > 15) {
                    addWarning(result, 
                        String.format("Field '%s' has large selection set (%d fields) - consider splitting", 
                            field.getName(), field.getSelectionSet().getSelections().size()), 
                        field);
                }
            }
        });
    }
    
    private Map<String, Integer> calculateFragmentUsage(Document document) {
        Map<String, Integer> usage = new HashMap<>();
        
        document.accept(new NodeVisitorStub() {
            @Override
            public void visitFragmentSpread(FragmentSpread node) {
                usage.merge(node.getName(), 1, Integer::sum);
            }
        });
        
        return usage;
    }
    
    @Override
    public String getCategory() {
        return "PERFORMANCE";
    }
}
```

### **Example 4: Security Rule Implementation**

```java
import com.intuit.gqlex.linting.rules.LintRule;
import com.intuit.gqlex.linting.core.LintContext;
import com.intuit.gqlex.linting.core.LintResult;
import graphql.language.*;

public class SecurityRule extends LintRule {
    
    public SecurityRule() {
        super("SECURITY", "Identifies potential security issues");
    }
    
    @Override
    public void lint(LintContext context, LintResult result) {
        Document document = context.getDocument();
        
        // Validate introspection usage
        validateIntrospectionUsage(document, context, result);
        
        // Validate sensitive field access
        validateSensitiveFieldAccess(document, context, result);
        
        // Validate argument validation
        validateArgumentValidation(document, context, result);
        
        // Validate depth limits
        validateDepthLimits(document, context, result);
    }
    
    private void validateIntrospectionUsage(Document document, LintContext context, LintResult result) {
        context.traverseNodes(node -> {
            if (node instanceof Field) {
                Field field = (Field) node;
                String fieldName = field.getName();
                
                // Check for introspection fields
                if (fieldName.startsWith("__")) {
                    addWarning(result, 
                        String.format("Introspection field '%s' detected - consider restricting in production", fieldName), 
                        field);
                }
            }
        });
    }
    
    private void validateSensitiveFieldAccess(Document document, LintContext context, LintResult result) {
        Set<String> sensitiveFields = context.getConfigValue("sensitiveFields", Set.class, 
            Set.of("password", "ssn", "creditCard", "apiKey"));
            
        context.traverseNodes(node -> {
            if (node instanceof Field) {
                Field field = (Field) node;
                String fieldName = field.getName().toLowerCase();
                
                // Check for sensitive fields
                if (sensitiveFields.contains(fieldName)) {
                    addWarning(result, 
                        String.format("Sensitive field '%s' detected - ensure proper access control", field.getName()), 
                        field);
                }
            }
        });
    }
    
    private void validateArgumentValidation(Document document, LintContext context, LintResult result) {
        context.traverseNodes(node -> {
            if (node instanceof Argument) {
                Argument argument = (Argument) node;
                Value value = argument.getValue();
                
                // Check argument values for potential injection
                if (value instanceof StringValue) {
                    StringValue stringValue = (StringValue) value;
                    String content = stringValue.getValue();
                    
                    // Check for potential injection patterns
                    if (content.contains("'") || content.contains("\"") || content.contains(";")) {
                        addWarning(result, 
                            String.format("Argument '%s' contains potentially unsafe characters - ensure proper validation", 
                                argument.getName()), 
                            argument);
                    }
                }
            }
        });
    }
    
    private void validateDepthLimits(Document document, LintContext context, LintResult result) {
        int maxDepth = context.getConfigValue("maxSecurityDepth", Integer.class, 3);
        int actualDepth = context.calculateMaxDepth();
        
        if (actualDepth > maxDepth) {
            addWarning(result, 
                String.format("Query depth (%d) exceeds security limit (%d) - potential DoS risk", 
                    actualDepth, maxDepth), 
                document);
        }
    }
    
    @Override
    public String getCategory() {
        return "SECURITY";
    }
}
```

## 📚 API Reference

### **LintRule Base Class**

#### **Constructors**
```java
protected LintRule(String name, LintLevel level, String description)
protected LintRule(String name, String description)  // Defaults to WARNING level
```

#### **Abstract Methods**
```java
public abstract void lint(LintContext context, LintResult result)
```

#### **Property Access**
```java
public String getName()                               // Get rule name
public LintLevel getLevel()                           // Get default level
public String getDescription()                        // Get description
public String getCategory()                           // Get category (default: "GENERAL")
```

#### **Configuration**
```java
public boolean isEnabled(LintContext context)         // Check if enabled
public LintLevel getSeverityLevel(LintContext context) // Get severity level
```

#### **Utility Methods**
```java
protected void addIssue(LintContext context, LintResult result, String message, Node node)
protected void addIssue(LintResult result, String message, Node node, LintLevel severity)
protected void addError(LintResult result, String message, Node node)
protected void addWarning(LintResult result, String message, Node node)
protected void addInfo(LintResult result, String message, Node node)
```

### **Rule Categories**

#### **Style Rules**
- **Purpose**: Enforce consistent code style and formatting
- **Examples**: Naming conventions, spacing, indentation, line length
- **Category**: `"STYLE"`

#### **Best Practice Rules**
- **Purpose**: Enforce GraphQL best practices and patterns
- **Examples**: Alias usage, fragment optimization, selection sets
- **Category**: `"BEST_PRACTICE"`

#### **Performance Rules**
- **Purpose**: Identify performance issues and optimization opportunities
- **Examples**: Query depth, field count, fragment usage
- **Category**: `"PERFORMANCE"`

#### **Security Rules**
- **Purpose**: Identify security vulnerabilities and risks
- **Examples**: Introspection usage, sensitive fields, depth limits
- **Category**: `"SECURITY"`

## 🧪 Testing

### **Testing Rules**

```java
@Test
@DisplayName("Style Rule - Naming Conventions")
void testStyleRuleNamingConventions() {
    // Create rule
    StyleRule rule = new StyleRule();
    
    // Create context
    String query = "query { User { ID name } }";  // Bad naming
    Document document = new Parser().parseDocument(query);
    LintContext context = new LintContext(document);
    LintResult result = new LintResult();
    
    // Execute rule
    rule.lint(context, result);
    
    // Verify results
    assertTrue(result.hasWarnings());
    assertEquals(2, result.getWarningCount());
    
    List<String> messages = result.getWarnings().stream()
        .map(LintIssue::getMessage)
        .collect(Collectors.toList());
    
    assertTrue(messages.stream().anyMatch(msg -> msg.contains("User")));
    assertTrue(messages.stream().anyMatch(msg -> msg.contains("ID")));
}
```

### **Running Rule Tests**

```bash
# Run all rule tests
mvn test -Dtest="*Rule*"

# Run specific rule tests
mvn test -Dtest=StyleRuleTest
mvn test -Dtest=BestPracticeRuleTest
mvn test -Dtest=PerformanceRuleTest
mvn test -Dtest=SecurityRuleTest
```

## 🔧 Custom Rules

### **Creating Custom Rules**

1. **Extend LintRule**: Create a new class extending `LintRule`
2. **Implement lint()**: Add your linting logic in the `lint()` method
3. **Add Tests**: Create comprehensive tests for your rule
4. **Update Documentation**: Add examples and documentation

### **Custom Rule Template**

```java
public class MyCustomRule extends LintRule {
    
    public MyCustomRule() {
        super("MY_CUSTOM_RULE", "My custom linting rule");
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
linter.addRule(new MyCustomRule());

// Configure custom rule
LintConfig config = new LintConfig();
config.setRuleConfig("MY_CUSTOM_RULE", new RuleConfig(true, LintLevel.WARNING));

// Test with custom rule
String query = "query { temporaryUserData { id name } }";
LintResult result = linter.lint(query);

result.getAllIssues().forEach(issue -> {
    System.out.println(issue.getLevel() + ": " + issue.getMessage());
});
```

### **Testing Custom Rules**

```java
@Test
@DisplayName("My Custom Rule Test")
void testMyCustomRule() {
    // Create rule
    MyCustomRule rule = new MyCustomRule();
    
    // Create context
    String query = "query { veryLongFieldName { id } }";
    Document document = new Parser().parseDocument(query);
    LintContext context = new LintContext(document);
    LintResult result = new LintResult();
    
    // Execute rule
    rule.lint(context, result);
    
    // Verify results
    assertTrue(result.hasWarnings());
    assertEquals(1, result.getWarningCount());
    
    LintIssue issue = result.getWarnings().get(0);
    assertEquals("MY_CUSTOM_RULE", issue.getRuleName());
    assertTrue(issue.getMessage().contains("too long"));
}
```

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**✅ LINTING RULES - PRODUCTION READY** 