# GraphQL Validation and Linting System

[![Java](https://img.shields.io/badge/Java-11+-blue.svg)](https://openjdk.java.net/)
[![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)](LICENSE)

## Overview

The **GraphQL Validation and Linting System** is a powerful, generic validation framework that provides comprehensive validation capabilities for GraphQL documents. It's designed to be completely schema-agnostic and works with any GraphQL query, mutation, or subscription.

## TL;DR 🚀

**GraphQL Validation & Linting** = **Generic Validation** + **Security Analysis** + **Performance Optimization** + **Code Quality**

### What it does:
- **✅ Validates GraphQL structure** - Ensures proper syntax and structure
- **✅ Security validation** - Detects SQL injection, XSS, DoS attacks
- **✅ Performance analysis** - Identifies performance bottlenecks
- **✅ Generic & agnostic** - Works with any GraphQL schema
- **✅ Extensible rules** - Easy to add custom validation rules

### Quick Example:
```java
// Create validator with all rules
GraphQLValidator validator = new GraphQLValidator()
    .addRule(new StructuralRule())
    .addRule(new PerformanceRule())
    .addRule(new SecurityRule());

// Validate any GraphQL query
ValidationResult result = validator.validate("query { user { id name } }");

if (result.isValid()) {
    System.out.println("✅ Query is valid!");
} else {
    System.out.println("❌ Validation errors: " + result.getErrorCount());
    System.out.println("⚠️  Warnings: " + result.getWarningCount());
}
```

### Perfect for:
- ✅ **Production Validation** - Ensure GraphQL queries are safe and optimized
- ✅ **Security Auditing** - Detect potential security vulnerabilities
- ✅ **Performance Monitoring** - Identify performance issues
- ✅ **Code Quality** - Enforce best practices
- ✅ **API Gateway Integration** - Validate incoming GraphQL requests
- ✅ **Development Tools** - IDE plugins and linting tools

**Ready to use with any GraphQL schema!** 🎯

## Table of Contents 📋

- [Features](#features)
- [Quick Start](#quick-start)
- [Validation Rules](#validation-rules)
  - [Structural Rule](#structural-rule)
  - [Performance Rule](#performance-rule)
  - [Security Rule](#security-rule)
- [Usage Examples](#usage-examples)
- [Advanced Configuration](#advanced-configuration)
- [Custom Validation Rules](#custom-validation-rules)
- [Integration Patterns](#integration-patterns)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)

## Features

### 🏗️ **Structural Validation**
- **Document Structure**: Validates basic GraphQL document integrity
- **Field Validation**: Ensures proper field definitions and structure
- **Argument Validation**: Validates argument syntax and structure
- **Fragment Validation**: Checks fragment definitions and usage
- **Generic & Agnostic**: Works with any field names or schema

### 🚀 **Performance Analysis**
- **Query Depth Analysis**: Detects overly nested queries
- **Field Count Monitoring**: Identifies queries with too many fields
- **Complexity Scoring**: Calculates query complexity metrics
- **Optimization Suggestions**: Provides actionable performance tips
- **Configurable Limits**: Customizable thresholds for your use case

### 🔒 **Security Validation**
- **SQL Injection Detection**: Identifies potential SQL injection attempts
- **XSS Attack Prevention**: Detects cross-site scripting patterns
- **DoS Attack Prevention**: Prevents denial-of-service attacks
- **Introspection Abuse**: Monitors introspection query usage
- **Path Traversal Detection**: Identifies directory traversal attempts

### 🎯 **Generic & Agnostic Design**
- **No Hardcoded Fields**: Works with any field naming convention
- **Schema Independent**: No assumptions about specific GraphQL schemas
- **Flexible Configuration**: Customizable validation rules and thresholds
- **Extensible Architecture**: Easy to add custom validation rules

## Quick Start

### Basic Usage

```java
import com.intuit.gqlex.validation.core.GraphQLValidator;
import com.intuit.gqlex.validation.core.ValidationResult;
import com.intuit.gqlex.validation.rules.StructuralRule;
import com.intuit.gqlex.validation.rules.PerformanceRule;
import com.intuit.gqlex.validation.rules.SecurityRule;

// Create validator with all rules
GraphQLValidator validator = new GraphQLValidator()
    .addRule(new StructuralRule())
    .addRule(new PerformanceRule())
    .addRule(new SecurityRule());

// Your GraphQL query
String query = """
    query UserQuery {
        user(id: 1) {
            name
            email
            profile {
                avatar
                bio
            }
        }
    }
    """;

// Validate the query
ValidationResult result = validator.validate(query);

// Check results
if (result.isValid()) {
    System.out.println("✅ Query is valid!");
} else {
    System.out.println("❌ Validation failed!");
    System.out.println("Errors: " + result.getErrorCount());
    System.out.println("Warnings: " + result.getWarningCount());
    
    // Print all issues
    result.getAllIssuesSorted().forEach(issue -> 
        System.out.println(issue.toString()));
}
```

## Validation Rules

### Structural Rule

The **Structural Rule** validates the basic integrity of GraphQL documents.

```java
import com.intuit.gqlex.validation.rules.StructuralRule;

// Create structural validator
GraphQLValidator validator = new GraphQLValidator()
    .addRule(new StructuralRule());

// Valid query - passes structural validation
String validQuery = """
    query UserQuery {
        user {
            name
            email
        }
    }
    """;

ValidationResult result = validator.validate(validQuery);
assert result.isValid(); // ✅ Passes

// Invalid query - fails structural validation
String invalidQuery = """
    query UserQuery {
        user {
            // Empty selection set
        }
    }
    """;

result = validator.validate(invalidQuery);
assert !result.isValid(); // ❌ Fails
```

**What Structural Rule Validates:**
- ✅ **Document Structure**: Ensures document has valid definitions
- ✅ **Field Names**: Validates field names are not empty
- ✅ **Argument Values**: Ensures arguments have valid values
- ✅ **Fragment Structure**: Validates fragment definitions
- ✅ **Operation Types**: Ensures proper query/mutation/subscription structure

### Performance Rule

The **Performance Rule** analyzes queries for performance issues and provides optimization suggestions.

```java
import com.intuit.gqlex.validation.rules.PerformanceRule;

// Create performance validator with custom limits
PerformanceRule performanceRule = new PerformanceRule(
    5,   // max depth
    50,  // max fields
    10   // max arguments
);

GraphQLValidator validator = new GraphQLValidator()
    .addRule(performanceRule);

// Deep query - triggers performance warnings
String deepQuery = """
    query DeepQuery {
        user {
            profile {
                details {
                    preferences {
                        settings {
                            options {
                                values {
                                    items {
                                        data {
                                            content {
                                                metadata {
                                                    info {
                                                        description {
                                                            text {
                                                                value
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    """;

ValidationResult result = validator.validate(deepQuery);
assert result.hasWarnings(); // ⚠️ Has performance warnings

// Print performance warnings
result.getWarnings().forEach(warning -> 
    System.out.println("Performance: " + warning.getMessage()));
```

**What Performance Rule Validates:**
- ✅ **Query Depth**: Warns about overly nested queries
- ✅ **Field Count**: Identifies queries with too many fields
- ✅ **Argument Count**: Monitors argument complexity
- ✅ **Introspection Queries**: Warns about performance-intensive introspection
- ✅ **Complexity Scoring**: Calculates overall query complexity
- ✅ **Optimization Suggestions**: Provides actionable performance tips

### Security Rule

The **Security Rule** detects potential security vulnerabilities in GraphQL queries.

```java
import com.intuit.gqlex.validation.rules.SecurityRule;

// Create security validator
GraphQLValidator validator = new GraphQLValidator()
    .addRule(new SecurityRule());

// Query with potential security issues
String suspiciousQuery = """
    query UserQuery {
        user(id: "1; DROP TABLE users;") {
            name
        }
    }
    """;

ValidationResult result = validator.validate(suspiciousQuery);
assert !result.isValid(); // ❌ Security validation failed

// Print security errors
result.getErrors().forEach(error -> 
    System.out.println("Security: " + error.getMessage()));

// Introspection query - security concern
String introspectionQuery = """
    query IntrospectionQuery {
        __schema {
            types {
                name
                fields {
                    name
                }
            }
        }
    }
    """;

result = validator.validate(introspectionQuery);
assert !result.isValid(); // ❌ Introspection abuse detected
```

**What Security Rule Validates:**
- ✅ **SQL Injection**: Detects SQL-like patterns in arguments
- ✅ **XSS Attacks**: Identifies script injection attempts
- ✅ **Path Traversal**: Detects directory traversal patterns
- ✅ **Introspection Abuse**: Monitors introspection query usage
- ✅ **DoS Prevention**: Prevents overly complex queries
- ✅ **Suspicious Patterns**: Identifies potentially dangerous patterns

## Usage Examples

### 1. **Production API Gateway Integration**

```java
public class GraphQLValidationMiddleware {
    
    private final GraphQLValidator validator;
    
    public GraphQLValidationMiddleware() {
        this.validator = new GraphQLValidator()
            .addRule(new StructuralRule())
            .addRule(new PerformanceRule(10, 100, 20))
            .addRule(new SecurityRule());
    }
    
    public ValidationResult validateRequest(String query) {
        return validator.validate(query);
    }
    
    public boolean isRequestAllowed(String query) {
        ValidationResult result = validateRequest(query);
        
        // Block requests with security issues
        if (result.getErrorCount() > 0) {
            return false;
        }
        
        // Allow requests with warnings (log them)
        if (result.hasWarnings()) {
            logWarnings(result.getWarnings());
        }
        
        return true;
    }
    
    private void logWarnings(List<ValidationError> warnings) {
        warnings.forEach(warning -> 
            logger.warn("Performance warning: " + warning.getMessage()));
    }
}
```

### 2. **Development Environment Integration**

```java
public class DevelopmentValidator {
    
    private final GraphQLValidator validator;
    
    public DevelopmentValidator() {
        // Stricter rules for development
        this.validator = new GraphQLValidator()
            .addRule(new StructuralRule())
            .addRule(new PerformanceRule(5, 30, 5))  // Stricter limits
            .addRule(new SecurityRule());
    }
    
    public void validateQuery(String query, String fileName) {
        ValidationResult result = validator.validate(query);
        
        if (!result.isValid()) {
            System.err.println("❌ Validation failed in " + fileName);
            result.getErrors().forEach(error -> 
                System.err.println("  Error: " + error.getMessage()));
        }
        
        if (result.hasWarnings()) {
            System.out.println("⚠️  Warnings in " + fileName);
            result.getWarnings().forEach(warning -> 
                System.out.println("  Warning: " + warning.getMessage()));
        }
        
        if (result.hasInfo()) {
            System.out.println("ℹ️  Suggestions for " + fileName);
            result.getInfo().forEach(info -> 
                System.out.println("  Suggestion: " + info.getMessage()));
        }
    }
}
```

### 3. **Custom Validation Rules**

```java
public class CustomBusinessRule extends ValidationRule {
    
    public CustomBusinessRule() {
        super("BUSINESS", ValidationLevel.WARNING, 
            "Validates business-specific requirements");
    }
    
    @Override
    public String getCategory() {
        return "BUSINESS";
    }
    
    @Override
    public void validate(ValidationContext context, ValidationResult result) {
        // Check for required fields in user queries
        context.traverseNodes(node -> {
            if (node instanceof Field) {
                Field field = (Field) node;
                if (field.getName().equals("user")) {
                    // Check if user query includes required fields
                    boolean hasId = context.findFields(f -> 
                        f.getName().equals("id")).size() > 0;
                    boolean hasName = context.findFields(f -> 
                        f.getName().equals("name")).size() > 0;
                    
                    if (!hasId || !hasName) {
                        result.addWarning(getName(), 
                            "User queries should include 'id' and 'name' fields for proper identification");
                    }
                }
            }
        });
    }
}

// Usage
GraphQLValidator validator = new GraphQLValidator()
    .addRule(new StructuralRule())
    .addRule(new CustomBusinessRule());
```

### 4. **Batch Validation**

```java
public class BatchValidator {
    
    private final GraphQLValidator validator;
    
    public BatchValidator() {
        this.validator = new GraphQLValidator()
            .addRule(new StructuralRule())
            .addRule(new PerformanceRule())
            .addRule(new SecurityRule());
    }
    
    public Map<String, ValidationResult> validateQueries(Map<String, String> queries) {
        Map<String, ValidationResult> results = new HashMap<>();
        
        queries.forEach((name, query) -> {
            ValidationResult result = validator.validate(query);
            results.put(name, result);
        });
        
        return results;
    }
    
    public ValidationSummary generateSummary(Map<String, ValidationResult> results) {
        int totalQueries = results.size();
        int validQueries = (int) results.values().stream()
            .mapToInt(r -> r.isValid() ? 1 : 0)
            .sum();
        int totalErrors = results.values().stream()
            .mapToInt(ValidationResult::getErrorCount)
            .sum();
        int totalWarnings = results.values().stream()
            .mapToInt(ValidationResult::getWarningCount)
            .sum();
        
        return new ValidationSummary(totalQueries, validQueries, totalErrors, totalWarnings);
    }
}
```

## Advanced Configuration

### Custom Performance Limits

```java
// Create performance rule with custom limits
PerformanceRule customPerformanceRule = new PerformanceRule(
    15,   // max depth (default: 10)
    200,  // max fields (default: 100)
    30    // max arguments (default: 20)
);

GraphQLValidator validator = new GraphQLValidator()
    .addRule(customPerformanceRule);
```

### Rule-Specific Validation

```java
// Validate with specific rules only
GraphQLValidator validator = new GraphQLValidator();

// Add only structural validation
validator.addRule(new StructuralRule());
ValidationResult structuralResult = validator.validate(query);

// Add performance validation
validator.addRule(new PerformanceRule());
ValidationResult performanceResult = validator.validate(query);

// Add security validation
validator.addRule(new SecurityRule());
ValidationResult securityResult = validator.validate(query);
```

### Schema-Aware Validation

```java
// Create validator with schema provider (future enhancement)
SchemaProvider schemaProvider = new MySchemaProvider();
GraphQLValidator validator = new GraphQLValidator(schemaProvider)
    .addRule(new StructuralRule())
    .addRule(new PerformanceRule())
    .addRule(new SecurityRule());
```

## Integration Patterns

### 1. **API Gateway Pattern**

```java
@Component
public class GraphQLValidationFilter implements WebFilter {
    
    private final GraphQLValidator validator;
    
    public GraphQLValidationFilter() {
        this.validator = new GraphQLValidator()
            .addRule(new StructuralRule())
            .addRule(new PerformanceRule())
            .addRule(new SecurityRule());
    }
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (isGraphQLRequest(exchange)) {
            String query = extractQuery(exchange);
            ValidationResult result = validator.validate(query);
            
            if (!result.isValid()) {
                return createErrorResponse(exchange, result);
            }
            
            if (result.hasWarnings()) {
                logWarnings(result.getWarnings());
            }
        }
        
        return chain.filter(exchange);
    }
}
```

### 2. **IDE Plugin Pattern**

```java
public class IDEValidationService {
    
    private final GraphQLValidator validator;
    
    public IDEValidationService() {
        this.validator = new GraphQLValidator()
            .addRule(new StructuralRule())
            .addRule(new PerformanceRule(5, 30, 5))  // Stricter for IDE
            .addRule(new SecurityRule());
    }
    
    public List<Diagnostic> validateDocument(String document) {
        ValidationResult result = validator.validate(document);
        List<Diagnostic> diagnostics = new ArrayList<>();
        
        // Convert validation errors to IDE diagnostics
        result.getErrors().forEach(error -> 
            diagnostics.add(createDiagnostic(error, DiagnosticSeverity.Error)));
        
        result.getWarnings().forEach(warning -> 
            diagnostics.add(createDiagnostic(warning, DiagnosticSeverity.Warning)));
        
        result.getInfo().forEach(info -> 
            diagnostics.add(createDiagnostic(info, DiagnosticSeverity.Information)));
        
        return diagnostics;
    }
}
```

### 3. **CI/CD Pipeline Pattern**

```java
public class CICDValidationStep {
    
    private final GraphQLValidator validator;
    
    public CICDValidationStep() {
        this.validator = new GraphQLValidator()
            .addRule(new StructuralRule())
            .addRule(new PerformanceRule())
            .addRule(new SecurityRule());
    }
    
    public boolean validateQueriesInRepository(String repositoryPath) {
        List<String> graphqlFiles = findGraphQLFiles(repositoryPath);
        boolean allValid = true;
        
        for (String file : graphqlFiles) {
            String content = readFile(file);
            ValidationResult result = validator.validate(content);
            
            if (!result.isValid()) {
                System.err.println("❌ Validation failed in " + file);
                result.getErrors().forEach(error -> 
                    System.err.println("  " + error.getMessage()));
                allValid = false;
            }
            
            if (result.hasWarnings()) {
                System.out.println("⚠️  Warnings in " + file);
                result.getWarnings().forEach(warning -> 
                    System.out.println("  " + warning.getMessage()));
            }
        }
        
        return allValid;
    }
}
```

## Best Practices

### 1. **Rule Selection**

```java
// ✅ Good: Choose rules based on environment
public GraphQLValidator createValidator(Environment env) {
    GraphQLValidator validator = new GraphQLValidator();
    
    // Always include structural validation
    validator.addRule(new StructuralRule());
    
    switch (env) {
        case PRODUCTION:
            // Strict performance limits for production
            validator.addRule(new PerformanceRule(8, 80, 15));
            validator.addRule(new SecurityRule());
            break;
        case DEVELOPMENT:
            // Relaxed limits for development
            validator.addRule(new PerformanceRule(12, 120, 25));
            break;
        case TESTING:
            // Very strict for testing
            validator.addRule(new PerformanceRule(5, 30, 5));
            validator.addRule(new SecurityRule());
            break;
    }
    
    return validator;
}
```

### 2. **Error Handling**

```java
// ✅ Good: Comprehensive error handling
public ValidationResponse validateQuery(String query) {
    try {
        ValidationResult result = validator.validate(query);
        
        return ValidationResponse.builder()
            .valid(result.isValid())
            .errors(convertToResponseErrors(result.getErrors()))
            .warnings(convertToResponseWarnings(result.getWarnings()))
            .info(convertToResponseInfo(result.getInfo()))
            .build();
            
    } catch (Exception e) {
        logger.error("Validation failed with exception", e);
        return ValidationResponse.builder()
            .valid(false)
            .errors(List.of("Validation system error: " + e.getMessage()))
            .build();
    }
}
```

### 3. **Performance Optimization**

```java
// ✅ Good: Reuse validator instances
public class ValidationService {
    
    private final GraphQLValidator validator;
    
    public ValidationService() {
        // Create once, reuse many times
        this.validator = new GraphQLValidator()
            .addRule(new StructuralRule())
            .addRule(new PerformanceRule())
            .addRule(new SecurityRule());
    }
    
    public ValidationResult validate(String query) {
        return validator.validate(query);
    }
}
```

### 4. **Logging and Monitoring**

```java
// ✅ Good: Comprehensive logging
public class ValidationLogger {
    
    public void logValidationResult(String query, ValidationResult result) {
        if (!result.isValid()) {
            logger.error("GraphQL validation failed for query: {}", 
                query.substring(0, Math.min(100, query.length())));
            result.getErrors().forEach(error -> 
                logger.error("Validation error: {}", error.getMessage()));
        }
        
        if (result.hasWarnings()) {
            logger.warn("GraphQL validation warnings for query: {}", 
                query.substring(0, Math.min(100, query.length())));
            result.getWarnings().forEach(warning -> 
                logger.warn("Validation warning: {}", warning.getMessage()));
        }
        
        // Monitor validation metrics
        metrics.recordValidation(result.isValid(), 
            result.getErrorCount(), result.getWarningCount());
    }
}
```

## Troubleshooting

### Common Issues

#### 1. **False Positive Security Warnings**

```java
// Issue: Legitimate queries flagged as security risks
// Solution: Adjust security rule patterns or create allowlist

public class CustomSecurityRule extends SecurityRule {
    
    @Override
    protected void validateSuspiciousPatterns(ValidationContext context, ValidationResult result) {
        // Override to be less strict for your use case
        // Or add allowlist for known safe patterns
    }
}
```

#### 2. **Performance Warnings for Valid Queries**

```java
// Issue: Complex but necessary queries trigger performance warnings
// Solution: Adjust performance limits or add exceptions

PerformanceRule customPerformanceRule = new PerformanceRule(
    20,   // Higher depth limit
    300,  // Higher field limit
    50    // Higher argument limit
);
```

#### 3. **Validation System Errors**

```java
// Issue: Validation system throws exceptions
// Solution: Add proper error handling

public ValidationResult safeValidate(String query) {
    try {
        return validator.validate(query);
    } catch (Exception e) {
        logger.error("Validation failed", e);
        ValidationResult result = new ValidationResult();
        result.addError("VALIDATION_SYSTEM", 
            "Validation system error: " + e.getMessage());
        return result;
    }
}
```

### Debug Mode

```java
// Enable debug mode for troubleshooting
public class DebugValidator {
    
    private final GraphQLValidator validator;
    
    public DebugValidator() {
        this.validator = new GraphQLValidator()
            .addRule(new StructuralRule())
            .addRule(new PerformanceRule())
            .addRule(new SecurityRule());
    }
    
    public ValidationResult validateWithDebug(String query) {
        System.out.println("🔍 Validating query: " + query.substring(0, Math.min(200, query.length())));
        
        ValidationResult result = validator.validate(query);
        
        System.out.println("📊 Validation summary:");
        System.out.println("  Valid: " + result.isValid());
        System.out.println("  Errors: " + result.getErrorCount());
        System.out.println("  Warnings: " + result.getWarningCount());
        System.out.println("  Info: " + result.getInfoCount());
        
        if (result.hasIssues()) {
            System.out.println("📋 Issues found:");
            result.getAllIssuesSorted().forEach(issue -> 
                System.out.println("  " + issue.toString()));
        }
        
        return result;
    }
}
```

## Conclusion

The GraphQL Validation and Linting System provides a comprehensive, generic, and extensible solution for validating GraphQL documents. Whether you're building an API gateway, development tools, or production monitoring systems, this framework gives you the tools to ensure GraphQL queries are safe, performant, and well-structured.

**Key Benefits:**
- ✅ **Generic & Agnostic**: Works with any GraphQL schema
- ✅ **Comprehensive**: Covers structural, performance, and security validation
- ✅ **Extensible**: Easy to add custom validation rules
- ✅ **Production Ready**: Robust error handling and performance optimization
- ✅ **Easy Integration**: Simple API for any use case

**Ready to validate your GraphQL queries?** 🚀

---

For more information, see the main [README.md](../README.md) or explore the [validation package](../src/main/java/com/intuit/gqlex/validation/) for implementation details. 