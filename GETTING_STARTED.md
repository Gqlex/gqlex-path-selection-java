# Getting Started with gqlex Library

Welcome to the **gqlex** library - a comprehensive GraphQL utility toolkit for Java applications. This guide will help you get started with all the powerful features this library provides.

## 📋 Table of Contents

- [Quick Setup](#quick-setup)
- [Core Features Overview](#core-features-overview)
- [1. GraphQL Path Selection (gqlXPath)](#1-graphql-path-selection-gqlxpath)
- [2. Query Transformation Engine](#2-query-transformation-engine)
- [3. Query Templating System](#3-query-templating-system)
- [4. GraphQL Validation](#4-graphql-validation)
- [5. GraphQL Linting](#5-graphql-linting)
- [6. Security Features](#6-security-features)
- [7. Performance Optimization](#7-performance-optimization)
- [8. Fragment Operations](#8-fragment-operations)
- [Advanced Usage](#advanced-usage)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)

## 🚀 Quick Setup

### Maven Dependency

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.intuit</groupId>
    <artifactId>gqlex</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Build from Source

```bash
# Clone the repository
git clone https://github.com/Gqlex/gqlex-path-selection-java.git
cd gqlex-path-selection-java

# Build and run tests
mvn clean install
```

## 🎯 Core Features Overview

The gqlex library provides these main capabilities:

1. **Path Selection** - Navigate GraphQL queries like XPath
2. **Query Transformation** - Programmatically modify GraphQL queries
3. **Query Templating** - Dynamic query generation with variables
4. **Validation** - Comprehensive GraphQL validation
5. **Linting** - Code quality and best practices enforcement
6. **Security** - Enterprise-grade security features
7. **Performance** - Query analysis and optimization
8. **Fragment Operations** - Advanced fragment manipulation

---

## 1. GraphQL Path Selection (gqlXPath)

Navigate and select nodes in GraphQL queries using XPath-like syntax.

### Basic Usage

```java
import com.intuit.gqlex.common.GqlNodeContext;
import com.intuit.gqlex.gqlxpath.selector.SelectorFacade;

// Initialize the selector
SelectorFacade selectorFacade = new SelectorFacade();

// Your GraphQL query
String query = """
    query {
        hero {
            name
            friends {
                name
                homeWorld {
                    name
                }
            }
        }
    }
    """;

// Select specific nodes
GqlNodeContext heroNode = selectorFacade.select(query, "//query/hero");
GqlNodeContext heroName = selectorFacade.select(query, "//query/hero/name");
GqlNodeContext friends = selectorFacade.select(query, "//query/hero/friends");
```

### Advanced Path Selection

```java
// Select all friend names
List<GqlNodeContext> friendNames = selectorFacade.selectAll(query, "//query/hero/friends/name");

// Select with conditions
GqlNodeContext specificFriend = selectorFacade.select(query, "//query/hero/friends[name='Luke']");

// Select nested fields
GqlNodeContext homeWorld = selectorFacade.select(query, "//query/hero/friends/homeWorld/name");
```

### Working with Arguments

```java
String queryWithArgs = """
    query($episode: Episode) {
        hero(episode: $episode) {
            name
            friends {
                name
            }
        }
    }
    """;

// Select hero with specific episode argument
GqlNodeContext heroWithEpisode = selectorFacade.select(queryWithArgs, "//query/hero[@episode='JEDI']");
```

---

## 2. Query Transformation Engine

Programmatically transform GraphQL queries using a fluent API.

### Basic Transformations

```java
import com.intuit.gqlex.transformation.GraphQLTransformer;
import com.intuit.gqlex.transformation.TransformationResult;

String originalQuery = """
    query {
        hero {
            name
            friends {
                name
            }
        }
    }
    """;

// Create transformer
GraphQLTransformer transformer = new GraphQLTransformer(originalQuery);

// Add a field
TransformationResult result = transformer
    .addField("//query/hero", "id")
    .addField("//query/hero", "height")
    .transform();

System.out.println("Transformed Query:");
System.out.println(result.getTransformedQuery());
```

### Field Operations

```java
// Remove fields
transformer
    .removeField("//query/hero/friends")
    .removeField("//query/hero/height")
    .transform();

// Rename fields
transformer
    .renameField("//query/hero/name", "characterName")
    .transform();

// Set aliases
transformer
    .setAlias("//query/hero", "mainHero")
    .setAlias("//query/hero/name", "heroName")
    .transform();
```

### Argument Operations

```java
// Add arguments
transformer
    .addArgument("//query/hero", "episode", "JEDI")
    .addArgument("//query/hero", "limit", 10)
    .transform();

// Update arguments
transformer
    .updateArgument("//query/hero", "episode", "EMPIRE")
    .transform();

// Remove arguments
transformer
    .removeArgument("//query/hero", "limit")
    .transform();
```

### Complex Transformations

```java
// Chain multiple operations
TransformationResult result = transformer
    .addField("//query/hero", "id")
    .addField("//query/hero", "height")
    .addArgument("//query/hero", "episode", "JEDI")
    .setAlias("//query/hero", "mainHero")
    .removeField("//query/hero/friends/homeWorld")
    .transform();

// Check for errors
if (result.hasErrors()) {
    result.getErrors().forEach(System.err::println);
} else {
    System.out.println("Transformation successful!");
    System.out.println(result.getTransformedQuery());
}
```

---

## 3. Query Templating System

Create dynamic GraphQL queries with variable substitution and conditional logic.

### Basic Templating

```java
import com.intuit.gqlex.querytemplating.QueryTemplate;

// Create a template
String template = """
    query($episode: Episode, $includeFriends: Boolean!) {
        hero(episode: $episode) {
            name
            id
            #if($includeFriends)
            friends {
                name
                id
            }
            #end
        }
    }
    """;

// Create template instance
QueryTemplate queryTemplate = new QueryTemplate(template);

// Set variables
Map<String, Object> variables = new HashMap<>();
variables.put("episode", "JEDI");
variables.put("includeFriends", true);

// Generate query
String generatedQuery = queryTemplate.process(variables);
System.out.println(generatedQuery);
```

### Conditional Fields

```java
String conditionalTemplate = """
    query($includeDetails: Boolean!, $includeMetadata: Boolean!) {
        hero {
            name
            #if($includeDetails)
            height
            mass
            #end
            #if($includeMetadata)
            created
            edited
            #end
        }
    }
    """;

QueryTemplate template = new QueryTemplate(conditionalTemplate);

// Generate with different conditions
Map<String, Object> vars1 = Map.of("includeDetails", true, "includeMetadata", false);
String query1 = template.process(vars1);

Map<String, Object> vars2 = Map.of("includeDetails", false, "includeMetadata", true);
String query2 = template.process(vars2);
```

### Complex Templates

```java
String complexTemplate = """
    query($episode: Episode, $limit: Int, $includeFriends: Boolean!, $includePlanet: Boolean!) {
        hero(episode: $episode) {
            name
            id
            #if($includeFriends)
            friends(first: $limit) {
                name
                id
                #if($includePlanet)
                homeWorld {
                    name
                    population
                }
                #end
            }
            #end
        }
    }
    """;

QueryTemplate template = new QueryTemplate(complexTemplate);

Map<String, Object> variables = Map.of(
    "episode", "JEDI",
    "limit", 5,
    "includeFriends", true,
    "includePlanet", true
);

String query = template.process(variables);
```

---

## 4. GraphQL Validation

Comprehensive validation of GraphQL queries with custom rules.

### Basic Validation

```java
import com.intuit.gqlex.validation.core.GraphQLValidator;
import com.intuit.gqlex.validation.core.ValidationResult;
import com.intuit.gqlex.validation.rules.StructuralRule;
import com.intuit.gqlex.validation.rules.PerformanceRule;
import com.intuit.gqlex.validation.rules.SecurityRule;

String query = """
    query {
        hero {
            name
            friends {
                name
                friends {
                    name
                }
            }
        }
    }
    """;

// Create validator
GraphQLValidator validator = new GraphQLValidator()
    .addRule(new StructuralRule())
    .addRule(new PerformanceRule())
    .addRule(new SecurityRule());

// Validate query
ValidationResult result = validator.validate(query);

// Check results
if (result.hasErrors()) {
    System.out.println("Validation Errors:");
    result.getErrors().forEach(error -> 
        System.out.println("- " + error.getMessage())
    );
}

if (result.hasWarnings()) {
    System.out.println("Validation Warnings:");
    result.getWarnings().forEach(warning -> 
        System.out.println("- " + warning.getMessage())
    );
}
```

### Custom Validation Rules

```java
import com.intuit.gqlex.validation.rules.ValidationRule;
import com.intuit.gqlex.validation.core.ValidationContext;
import com.intuit.gqlex.validation.core.ValidationLevel;

public class CustomValidationRule extends ValidationRule {
    
    @Override
    public void validate(ValidationContext context) {
        // Check for specific field patterns
        context.traverseNodes(node -> {
            if (node instanceof Field) {
                Field field = (Field) node;
                if (field.getName().equals("password")) {
                    context.addError(
                        "SensitiveField",
                        "Password field detected - consider using a more secure approach",
                        ValidationLevel.ERROR,
                        field
                    );
                }
            }
        });
    }
}

// Use custom rule
GraphQLValidator validator = new GraphQLValidator()
    .addRule(new CustomValidationRule());
```

---

## 5. GraphQL Linting

Enforce code quality and best practices in GraphQL queries.

### Basic Linting

```java
import com.intuit.gqlex.linting.core.GraphQLLinter;
import com.intuit.gqlex.linting.core.LintResult;
import com.intuit.gqlex.linting.rules.StyleRule;
import com.intuit.gqlex.linting.rules.BestPracticeRule;
import com.intuit.gqlex.linting.rules.PerformanceRule;
import com.intuit.gqlex.linting.rules.SecurityRule;

String query = """
    query {
        HERO {
            NAME
            friends {
                name
            }
        }
    }
    """;

// Create linter
GraphQLLinter linter = new GraphQLLinter()
    .addRule(new StyleRule())
    .addRule(new BestPracticeRule())
    .addRule(new PerformanceRule())
    .addRule(new SecurityRule());

// Lint query
LintResult result = linter.lint(query);

// Process results
if (result.hasIssues()) {
    System.out.println("Linting Issues:");
    result.getIssues().forEach(issue -> 
        System.out.println("- [" + issue.getLevel() + "] " + issue.getMessage())
    );
}
```

### Using Linting Presets

```java
import com.intuit.gqlex.linting.config.LintPreset;

// Use predefined presets
GraphQLLinter strictLinter = new GraphQLLinter()
    .withPreset(LintPreset.strict());

GraphQLLinter relaxedLinter = new GraphQLLinter()
    .withPreset(LintPreset.relaxed());

GraphQLLinter performanceLinter = new GraphQLLinter()
    .withPreset(LintPreset.performance());

GraphQLLinter securityLinter = new GraphQLLinter()
    .withPreset(LintPreset.security());
```

### Custom Linting Configuration

```java
import com.intuit.gqlex.linting.config.LintConfig;
import com.intuit.gqlex.linting.config.RuleConfig;

// Create custom configuration
LintConfig config = new LintConfig()
    .setMaxLineLength(120)
    .setMaxQueryDepth(10)
    .setMaxFieldCount(50)
    .enableRule("StyleRule", LintLevel.WARNING)
    .disableRule("PerformanceRule");

GraphQLLinter linter = new GraphQLLinter(config);
```

---

## 6. Security Features

Enterprise-grade security validation and access control.

### Security Validation

```java
import com.intuit.gqlex.security.SecurityValidator;
import com.intuit.gqlex.security.SecurityValidationResult;
import com.intuit.gqlex.security.SecurityConfig;

// Configure security
SecurityConfig securityConfig = new SecurityConfig()
    .setMaxQueryDepth(10)
    .setMaxFieldCount(100)
    .setMaxArgumentCount(20)
    .enableIntrospection(false)
    .setRateLimitPerMinute(100)
    .setRateLimitPerHour(1000);

// Create security validator
SecurityValidator validator = new SecurityValidator(securityConfig);

// Validate query security
SecurityValidationResult result = validator.validate(query);

if (result.isSecure()) {
    System.out.println("Query is secure");
} else {
    System.out.println("Security issues detected:");
    result.getSecurityIssues().forEach(issue -> 
        System.out.println("- " + issue.getMessage())
    );
}
```

### Rate Limiting

```java
import com.intuit.gqlex.security.RateLimiter;
import com.intuit.gqlex.security.UserContext;

// Create rate limiter
RateLimiter rateLimiter = new RateLimiter();

// Create user context
UserContext userContext = new UserContext("user123", "premium");

// Check rate limits
if (rateLimiter.isAllowed(userContext)) {
    System.out.println("Request allowed");
    rateLimiter.recordRequest(userContext);
} else {
    System.out.println("Rate limit exceeded");
}
```

### Access Control

```java
import com.intuit.gqlex.security.AccessControlManager;

// Create access control manager
AccessControlManager accessControl = new AccessControlManager();

// Configure field permissions
accessControl.addFieldPermission("hero", "admin", "read");
accessControl.addFieldPermission("hero.password", "admin", "read");
accessControl.addFieldPermission("hero.password", "user", "none");

// Check permissions
UserContext adminUser = new UserContext("admin1", "admin");
UserContext regularUser = new UserContext("user1", "user");

boolean canReadHero = accessControl.canAccessField("hero", adminUser, "read");
boolean canReadPassword = accessControl.canAccessField("hero.password", regularUser, "read");
```

### Audit Logging

```java
import com.intuit.gqlex.security.AuditLogger;
import com.intuit.gqlex.security.AuditLogEntry;

// Create audit logger
AuditLogger auditLogger = new AuditLogger();

// Log query execution
AuditLogEntry entry = new AuditLogEntry()
    .setUserId("user123")
    .setQuery(query)
    .setTimestamp(System.currentTimeMillis())
    .setIpAddress("192.168.1.100")
    .setUserAgent("Mozilla/5.0...");

auditLogger.logQuery(entry);

// Log security events
auditLogger.logSecurityEvent("RATE_LIMIT_EXCEEDED", userContext, query);
auditLogger.logSecurityEvent("UNAUTHORIZED_ACCESS", userContext, "hero.password");
```

---

## 7. Performance Optimization

Analyze and optimize GraphQL query performance.

### Performance Analysis

```java
import com.intuit.gqlex.transformation.optimization.PerformanceOptimizationManager;

// Get performance manager
PerformanceOptimizationManager perfManager = PerformanceOptimizationManager.getInstance();

// Analyze query performance
String query = """
    query {
        hero {
            name
            friends {
                name
                friends {
                    name
                    friends {
                        name
                    }
                }
            }
        }
    }
    """;

// Get performance metrics
int depth = perfManager.analyzeQueryDepth(query);
int fieldCount = perfManager.analyzeFieldCount(query);
double complexity = perfManager.analyzeQueryComplexity(query);

System.out.println("Query Depth: " + depth);
System.out.println("Field Count: " + fieldCount);
System.out.println("Complexity Score: " + complexity);
```

### AST Caching

```java
import com.intuit.gqlex.transformation.optimization.ASTCache;

// Get AST cache
ASTCache astCache = ASTCache.getInstance();

// Parse and cache document
Document document = astCache.getOrParse(query);

// Get cached string representation
String cachedQuery = astCache.getOrPrint(document);
```

---

## 8. Fragment Operations

Advanced fragment manipulation and optimization.

### Fragment Inlining

```java
String queryWithFragments = """
    query {
        hero {
            ...HeroFields
        }
    }
    
    fragment HeroFields on Character {
        name
        id
        friends {
            name
        }
    }
    """;

// Inline all fragments
TransformationResult result = transformer
    .inlineAllFragments()
    .transform();

System.out.println("Inlined Query:");
System.out.println(result.getTransformedQuery());
```

### Fragment Extraction

```java
// Extract a fragment from existing query
TransformationResult result = transformer
    .extractFragment("//query/hero", "HeroFields", "Character")
    .transform();

System.out.println("Extracted Fragment:");
System.out.println(result.getTransformedQuery());
```

---

## 🔧 Advanced Usage

### Combining Multiple Features

```java
// Complete workflow example
public class GraphQLProcessor {
    
    public void processQuery(String query, String userId) {
        // 1. Validate security
        SecurityValidator securityValidator = new SecurityValidator();
        SecurityValidationResult securityResult = securityValidator.validate(query);
        
        if (!securityResult.isSecure()) {
            throw new SecurityException("Query failed security validation");
        }
        
        // 2. Check rate limits
        RateLimiter rateLimiter = new RateLimiter();
        UserContext userContext = new UserContext(userId, "user");
        
        if (!rateLimiter.isAllowed(userContext)) {
            throw new RateLimitException("Rate limit exceeded");
        }
        
        // 3. Transform query
        GraphQLTransformer transformer = new GraphQLTransformer(query);
        TransformationResult transformResult = transformer
            .addField("//query/hero", "id")
            .setAlias("//query/hero", "mainHero")
            .transform();
        
        // 4. Validate transformed query
        GraphQLValidator validator = new GraphQLValidator();
        ValidationResult validationResult = validator.validate(transformResult.getTransformedQuery());
        
        if (validationResult.hasErrors()) {
            throw new ValidationException("Transformed query has validation errors");
        }
        
        // 5. Lint for best practices
        GraphQLLinter linter = new GraphQLLinter();
        LintResult lintResult = linter.lint(transformResult.getTransformedQuery());
        
        // 6. Log for audit
        AuditLogger auditLogger = new AuditLogger();
        auditLogger.logQuery(new AuditLogEntry()
            .setUserId(userId)
            .setQuery(transformResult.getTransformedQuery())
            .setTimestamp(System.currentTimeMillis()));
        
        // 7. Return processed query
        return transformResult.getTransformedQuery();
    }
}
```

### Custom Extensions

```java
// Custom transformation operation
public class AddTimestampOperation implements TransformationOperation {
    
    @Override
    public TransformationResult apply(String query, TransformationContext context) {
        GraphQLTransformer transformer = new GraphQLTransformer(query);
        return transformer
            .addField("//query", "timestamp")
            .transform();
    }
}

// Custom validation rule
public class BusinessRuleValidator extends ValidationRule {
    
    @Override
    public void validate(ValidationContext context) {
        // Implement business-specific validation logic
    }
}

// Custom linting rule
public class CompanyStyleRule extends LintRule {
    
    @Override
    public void lint(LintContext context) {
        // Implement company-specific style rules
    }
}
```

---

## 📚 Best Practices

### 1. Performance
- Use AST caching for frequently processed queries
- Implement rate limiting to prevent abuse
- Monitor query complexity and depth
- Use fragments for reusable field sets

### 2. Security
- Always validate queries before execution
- Implement field-level access control
- Log all query executions for audit
- Use rate limiting to prevent DoS attacks

### 3. Code Quality
- Use linting to enforce consistent style
- Implement custom validation rules for business logic
- Use templates for dynamic query generation
- Document complex transformations

### 4. Error Handling
- Always check for validation errors
- Handle rate limiting gracefully
- Log security events appropriately
- Provide meaningful error messages

---

## 🛠️ Troubleshooting

### Common Issues

**1. Validation Errors**
```java
// Check for specific validation issues
ValidationResult result = validator.validate(query);
if (result.hasErrors()) {
    result.getErrors().forEach(error -> {
        System.out.println("Error: " + error.getMessage());
        System.out.println("Location: " + error.getNode());
    });
}
```

**2. Security Issues**
```java
// Debug security validation
SecurityValidationResult securityResult = securityValidator.validate(query);
if (!securityResult.isSecure()) {
    securityResult.getSecurityIssues().forEach(issue -> {
        System.out.println("Security Issue: " + issue.getMessage());
        System.out.println("Severity: " + issue.getSeverity());
    });
}
```

**3. Performance Issues**
```java
// Analyze performance bottlenecks
PerformanceOptimizationManager perfManager = PerformanceOptimizationManager.getInstance();
int depth = perfManager.analyzeQueryDepth(query);
if (depth > 10) {
    System.out.println("Query depth too high: " + depth);
}
```

### Debug Mode

```java
// Enable debug logging
System.setProperty("gqlex.debug", "true");

// Enable detailed error messages
GraphQLValidator validator = new GraphQLValidator()
    .setDebugMode(true);
```

---

## 📖 Further Reading

For detailed information about each feature, refer to these documentation files:

- **Path Selection**: [`src/main/java/com/intuit/gqlex/gqlxpath/README.md`](src/main/java/com/intuit/gqlex/gqlxpath/README.md)
- **Traversal**: [`src/main/java/com/intuit/gqlex/traversal/README.md`](src/main/java/com/intuit/gqlex/traversal/README.md)
- **Linting System**: [`docs/GRAPHQL_LINTING_SYSTEM.md`](docs/GRAPHQL_LINTING_SYSTEM.md)
- **Validation & Linting**: [`docs/GRAPHQL_VALIDATION_LINTING.md`](docs/GRAPHQL_VALIDATION_LINTING.md)
- **Main Documentation**: [`README.md`](README.md)

## 🤝 Contributing

Please read our [Contributing Guide](CONTRIBUTING.md) for details on how to contribute to this project.

## 📄 License

This project is licensed under the terms specified in the [LICENSE](LICENSE) file.
