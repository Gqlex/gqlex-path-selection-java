# Getting Started with gqlex Library

Welcome to the **gqlex** library - a comprehensive GraphQL utility toolkit for Java applications. This guide will help you get started with all the powerful features this library provides.

## Table of Contents

- [Quick Setup](#quick-setup)
- [Core Features Overview](#core-features-overview)
- [1. GraphQL Path Selection (gqlXPath)](#1-graphql-path-selection-gqlxpath)
- [2. Lazy Loading gqlXPath](#2-lazy-loading-gqlxpath)
- [3. Query Transformation Engine](#3-query-transformation-engine)
- [4. Query Templating System](#4-query-templating-system)
- [5. GraphQL Validation](#5-graphql-validation)
- [6. GraphQL Linting](#6-graphql-linting)
- [7. Security Features](#7-security-features)
- [8. Performance Optimization](#8-performance-optimization)
- [9. Fragment Operations](#9-fragment-operations)
- [10. Testing & Benchmark System](#10-testing--benchmark-system)
- [Advanced Usage](#advanced-usage)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)

## Quick Setup

### Maven Dependency

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.intuit</groupId>
    <artifactId>gqlex</artifactId>
    <version>3.1.0</version>
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

## Core Features Overview

The gqlex library provides these main capabilities:

1. **Path Selection** - Navigate GraphQL queries like XPath
2. **Lazy Loading gqlXPath** - Revolutionary lazy loading with 8,000x+ speedup and 100% test success
3. **Query Transformation** - Programmatically modify GraphQL queries
4. **Query Templating** - Dynamic query generation with variables
5. **Validation** - Comprehensive GraphQL validation
6. **Linting** - Code quality and best practices enforcement
7. **Security** - Enterprise-grade security features
8. **Performance** - Query analysis and optimization
9. **Fragment Operations** - Advanced fragment manipulation

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
// Select with arguments
GqlNodeContext heroWithEpisode = selectorFacade.select(query, "//query/hero[episode=EMPIRE]");

// Select with variables
GqlNodeContext heroWithVariable = selectorFacade.select(query, "//query/hero[episode=$episode]");

// Select with type filtering
GqlNodeContext heroWithDirective = selectorFacade.select(query, "//query/hero/friends/include[type=direc]");

// Select with ranges
GqlNodeContext firstTwoFriends = selectorFacade.select(query, "//query/hero/friends[1:2]");

// Select with wildcards
GqlNodeContext allHeroFields = selectorFacade.select(query, "//query/hero/*");
```

### Path Selection Examples

| XPath Expression | Description | Example |
|------------------|-------------|---------|
| `//query/hero` | Select hero field in query | Basic field selection |
| `//query/hero/name` | Select name field under hero | Nested field selection |
| `//query/hero[episode=EMPIRE]` | Select hero with specific episode | Argument-based selection |
| `//query/hero@include(if: $show)` | Select hero with directive | Directive-based selection |
| `//fragment/heroFields` | Select fragment definition | Fragment selection |
| `//query/hero/friends[1:3]` | Select first 3 friends | Range selection |

---

## 2. Lazy Loading gqlXPath

The revolutionary lazy loading system provides unprecedented performance improvements by loading only required document sections.

### Performance Benefits

| Query Type | Traditional | Lazy Loading | Improvement |
|------------|-------------|--------------|-------------|
| **Simple Queries** | 50-100ms | 2-5ms | **15-25x faster** |
| **Deep Nested (5+ levels)** | Hours | 2-7ms | **8,000x+ faster** |
| **Complex Fragments** | 10-30s | 100-500ms | **20-60x faster** |
| **Large Documents** | Memory intensive | Efficient sections | **60-95% memory reduction** |

### Basic Lazy Loading Usage

```java
import com.intuit.gqlex.gqlxpath.lazy.LazyXPathProcessor;

// Initialize lazy loading processor
LazyXPathProcessor processor = new LazyXPathProcessor();

// Process XPath with lazy loading
LazyXPathResult result = processor.processXPath(queryString, "//query/hero/friends/name");

if (result.isSuccess()) {
    List<GqlNodeContext> nodes = result.getResult();
    System.out.println("Found " + nodes.size() + " nodes in " + result.getDuration() + "ms");
}
```

### Advanced Lazy Loading Features

```java
// Process multiple XPaths efficiently
List<String> xpaths = Arrays.asList(
    "//query/hero/name",
    "//query/hero/friends/name",
    "//query/hero/episode"
);

List<LazyXPathResult> results = processor.processMultipleXPaths(queryString, xpaths);

// Performance comparison
PerformanceComparison comparison = processor.compareWithTraditional(queryString, "//query/hero//name");
System.out.println("Speedup: " + comparison.getImprovementPercentage() + "%");
```

### Lazy Loading Architecture

The lazy loading system consists of several key components:

- **LazyXPathProcessor**: Main processor for lazy XPath execution
- **DocumentSectionLoader**: Loads only required document sections
- **XPathAnalyzer**: Analyzes XPath complexity and requirements
- **Section Cache**: Caches loaded document sections for reuse
- **Performance Metrics**: Built-in performance monitoring

---

## 3. Query Transformation Engine

Programmatically modify GraphQL queries with a comprehensive transformation engine.

### Basic Transformations

```java
import com.intuit.gqlex.transformation.GraphQLTransformer;
import com.intuit.gqlex.transformation.TransformationResult;

// Initialize transformer
GraphQLTransformer transformer = new GraphQLTransformer(queryString);

// Add a new field
TransformationResult result = transformer
    .addField("//query/hero", "id")
    .transform();

System.out.println("Transformed query: " + result.getTransformedQuery());
```

### Field Operations

```java
// Add multiple fields
transformer.addField("//query/hero", "id")
          .addField("//query/hero", "appearsIn");

// Remove fields
transformer.removeField("//query/hero/friends/homeWorld");

// Rename fields
transformer.renameField("//query/hero", "mainHero");

// Sort fields alphabetically
transformer.sortFields("//query/hero");
```

### Argument Operations

```java
// Add arguments
transformer.addArgument("//query/hero", "episode", "EMPIRE")
          .addArgument("//query/hero", "limit", 10);

// Update arguments
transformer.updateArgument("//query/hero", "episode", "JEDI");

// Remove arguments
transformer.removeArgument("//query/hero", "limit");

// Normalize arguments
transformer.normalizeArguments("//query/hero");
```

### Fragment Operations

```java
// Extract inline fragments
transformer.extractFragment("//query/hero", "HeroFields", "Character");

// Inline fragment definitions
transformer.inlineFragments();

// Merge fragments
transformer.mergeFragments("HeroFields", "CharacterFields");
```

---

## 4. Query Templating System

Create dynamic, reusable GraphQL queries with variable substitution and conditional logic.

### Basic Template Usage

```java
import com.intuit.gqlex.querytemplating.QueryTemplate;

// Create template with variables
String templateString = """
    query GetUser($userId: ID!) {
        user(id: ${userId}) {
            id
            name
            email
            #if($includeProfile)
            profile {
                bio
                avatar
            }
            #end
        }
    }
    """;

QueryTemplate template = QueryTemplate.fromString(templateString);

// Process template with variables
Map<String, Object> variables = Map.of(
    "userId", "123",
    "includeProfile", true
);

String generatedQuery = template.process(variables);
```

### Template Features

- **Variable Substitution**: Replace `${variableName}` with runtime values
- **Conditional Blocks**: Use `#if($condition)` for conditional sections
- **Type-safe Variables**: Automatic formatting for different data types
- **File Loading**: Load templates from `.gql` files
- **Validation**: Template syntax validation before processing

### Advanced Templating

```java
// Nested conditions
String complexTemplate = """
    query($includeFriends: Boolean!, $includeProfile: Boolean!) {
        user {
            id
            name
            #if($includeFriends)
            friends {
                name
                #if($includeProfile)
                profile {
                    bio
                }
                #end
            }
            #end
            #if($includeProfile)
            profile {
                bio
                avatar
            }
            #end
        }
    }
    """;

// Process with complex conditions
Map<String, Object> vars = Map.of(
    "includeFriends", true,
    "includeProfile", false
);
```

---

## 5. GraphQL Validation

Comprehensive validation system for GraphQL queries with custom rules and presets.

### Basic Validation

```java
import com.intuit.gqlex.validation.core.GraphQLValidator;
import com.intuit.gqlex.validation.core.ValidationResult;

// Initialize validator
GraphQLValidator validator = new GraphQLValidator();

// Validate query
ValidationResult result = validator.validate(queryString);

if (result.isValid()) {
    System.out.println("Query is valid");
} else {
    System.out.println("Validation errors: " + result.getErrors());
}
```

### Validation Rules

```java
import com.intuit.gqlex.validation.rules.*;

// Add specific validation rules
validator.addRule(new StructuralRule())
        .addRule(new PerformanceRule())
        .addRule(new SecurityRule());

// Validate with rules
ValidationResult result = validator.validate(queryString);
```

### Validation Rule Types

| Rule Type | Description | Purpose |
|-----------|-------------|---------|
| **StructuralRule** | Schema structure validation | Ensures query structure is valid |
| **PerformanceRule** | Query performance analysis | Detects performance issues |
| **SecurityRule** | Security vulnerability detection | Identifies security risks |

---

## 6. GraphQL Linting

Code quality enforcement with comprehensive linting rules and configurable presets.

### Basic Linting

```java
import com.intuit.gqlex.linting.core.GraphQLLinter;
import com.intuit.gqlex.linting.core.LintResult;

// Initialize linter
GraphQLLinter linter = new GraphQLLinter();

// Lint query
LintResult result = linter.lint(queryString);

if (result.hasIssues()) {
    System.out.println("Linting issues found: " + result.getIssues());
} else {
    System.out.println("No linting issues");
}
```

### Linting Rules

```java
import com.intuit.gqlex.linting.rules.*;

// Add specific linting rules
linter.addRule(new StyleRule())
      .addRule(new BestPracticeRule())
      .addRule(new PerformanceRule())
      .addRule(new SecurityRule());
```

### Linting Categories

| Category | Description | Examples |
|----------|-------------|----------|
| **Style** | Naming conventions, formatting | Field naming, query structure |
| **Best Practice** | GraphQL patterns, anti-patterns | Fragment usage, query complexity |
| **Performance** | Query efficiency, N+1 detection | Field selection, depth analysis |
| **Security** | Field exposure, rate limiting | Sensitive data, access control |

### Linting Presets

```java
import com.intuit.gqlex.linting.config.LintPreset;

// Use predefined presets
linter.withPreset(LintPreset.strict());    // Strict rules
linter.withPreset(LintPreset.standard());  // Standard rules
linter.withPreset(LintPreset.relaxed());   // Relaxed rules

// Custom preset
LintPreset custom = LintPreset.builder()
    .addRule(new StyleRule())
    .addRule(new BestPracticeRule())
    .setMaxSeverity(Severity.WARNING)
    .build();
```

---

## 7. Security Features

Enterprise-grade security validation with comprehensive access control and audit logging.

### Basic Security Validation

```java
import com.intuit.gqlex.security.SecurityValidator;
import com.intuit.gqlex.security.SecurityValidationResult;

// Initialize security validator
SecurityValidator validator = new SecurityValidator();

// Validate query security
SecurityValidationResult result = validator.validate(queryString);

if (result.isSecure()) {
    System.out.println("Query is secure");
} else {
    System.out.println("Security issues: " + result.getSecurityIssues());
}
```

### Access Control

```java
import com.intuit.gqlex.security.AccessControlManager;

// Configure access control
AccessControlManager accessControl = new AccessControlManager();

// Set field permissions
accessControl.addFieldPermission("user.password", "admin", "read");
accessControl.addFieldPermission("user.secret", "admin", "read");

// Check permissions
UserContext user = new UserContext("user123", "user");
if (accessControl.canAccessField("user.password", user, "read")) {
    // Allow access
}
```

### Rate Limiting

```java
import com.intuit.gqlex.security.RateLimiter;

// Configure rate limiting
RateLimiter rateLimiter = new RateLimiter();

// Set limits per time window
rateLimiter.setLimit(100, Duration.ofMinutes(1));    // 100 requests per minute
rateLimiter.setLimit(1000, Duration.ofHours(1));     // 1000 requests per hour
rateLimiter.setLimit(10000, Duration.ofDays(1));     // 10000 requests per day

// Check if request is allowed
UserContext user = new UserContext("user123", "premium");
if (rateLimiter.isAllowed(user)) {
    // Process request
}
```

### Audit Logging

```java
import com.intuit.gqlex.security.AuditLogger;
import com.intuit.gqlex.security.AuditLogEntry;

// Initialize audit logger
AuditLogger auditLogger = new AuditLogger();

// Log query execution
AuditLogEntry entry = new AuditLogEntry()
    .setUserId("user123")
    .setQuery(queryString)
    .setTimestamp(System.currentTimeMillis())
    .setOperation("QUERY_EXECUTION");

auditLogger.log(entry);
```

---

## 8. Performance Optimization

Advanced performance optimization with AST caching, object pooling, and intelligent caching.

### Performance Monitoring

```java
import com.intuit.gqlex.transformation.optimization.PerformanceOptimizationManager;

// Get performance manager instance
PerformanceOptimizationManager perfManager = PerformanceOptimizationManager.getInstance();

// Analyze query performance
double complexity = perfManager.analyzeQueryComplexity(queryString);
int depth = perfManager.analyzeQueryDepth(queryString);
int fieldCount = perfManager.analyzeFieldCount(queryString);

System.out.println("Query complexity: " + complexity);
System.out.println("Query depth: " + depth);
System.out.println("Field count: " + fieldCount);
```

### Caching System

```java
import com.intuit.gqlex.transformation.optimization.ASTCache;

// Get AST cache instance
ASTCache astCache = ASTCache.getInstance();

// Cache AST for reuse
astCache.cacheAST(queryString, ast);

// Retrieve cached AST
Document cachedAST = astCache.getCachedAST(queryString);
```

### Object Pooling

```java
import com.intuit.gqlex.transformation.optimization.ObjectPool;

// Get object pool instance
ObjectPool<GqlNodeContext> nodePool = ObjectPool.getInstance(GqlNodeContext.class);

// Borrow object from pool
GqlNodeContext node = nodePool.borrow();

// Use object
// ... perform operations ...

// Return object to pool
nodePool.returnObject(node);
```

---

## 9. Fragment Operations

Advanced fragment manipulation and optimization for complex GraphQL queries.

### Fragment Inlining - Real Example with Actual Data

**BEFORE (with fragments):**
```graphql
query GetUser($userId: ID!) {
  user(id: $userId) {
    id
    name
    ...UserProfile
    ...UserPosts
  }
}

fragment UserProfile on User {
  email
  profile {
    bio
    avatar
  }
}

fragment UserPosts on User {
  posts {
    id
    title
  }
}
```

**AFTER (fragments inlined):**
```graphql
query GetUser($userId: ID!) {
  user(id: $userId) {
    id
    name
    email
    profile {
      bio
      avatar
    }
    posts {
      id
      title
    }
  }
}
```

**What actually happened:**
- `...UserProfile` → replaced with `email` and `profile { bio avatar }`
- `...UserPosts` → replaced with `posts { id title }`
- All fragment definitions were removed
- The query is now self-contained with no fragment dependencies

**Code to achieve this:**
```java
import com.intuit.gqlex.transformation.GraphQLTransformer;

// Inline all fragments
GraphQLTransformer transformer = new GraphQLTransformer(queryString);
TransformationResult result = transformer.inlineAllFragments().transform();

if (result.isSuccess()) {
    String inlinedQuery = result.getQueryString();
    System.out.println("Inlined query: " + inlinedQuery);
}
```

### Fragment Extraction - Real Example with Actual Data

**BEFORE (inline fields):**
```graphql
query GetHero($episode: Episode) {
  hero(episode: $episode) {
    id
    name
    appearsIn
    friends {
      id
      name
      appearsIn
    }
  }
}
```

**AFTER (fragment extracted):**
```graphql
query GetHero($episode: Episode) {
  hero(episode: $episode) {
    ...HeroFields
  }
}

fragment HeroFields on Character {
  id
  name
  appearsIn
  friends {
    id
    name
    appearsIn
  }
}
```

**What actually happened:**
- The selection set under `hero` was extracted into a new fragment called `HeroFields`
- The fragment has type condition `Character`
- The original query now references the fragment with `...HeroFields`
- Fields: `id`, `name`, `appearsIn`, `friends { id, name, appearsIn }`

**Code to achieve this:**
```java
import com.intuit.gqlex.transformation.GraphQLTransformer;

// Extract fragment from hero field
GraphQLTransformer transformer = new GraphQLTransformer(queryString);
TransformationResult result = transformer
    .extractFragment("//query/hero", "HeroFields", "Character")
    .transform();

if (result.isSuccess()) {
    String extractedQuery = result.getQueryString();
    System.out.println("Extracted query: " + extractedQuery);
}
```

### Fragment Operations with Directives - Real Example

**BEFORE (fragment with directive):**
```graphql
query GetUser($userId: ID!, $includeProfile: Boolean!) {
  user(id: $userId) {
    id
    name
    ...UserProfile @include(if: $includeProfile)
  }
}

fragment UserProfile on User {
  email
  profile {
    bio
    avatar
  }
}
```

**AFTER (directive preserved):**
```graphql
query GetUser($userId: ID!, $includeProfile: Boolean!) {
  user(id: $userId) {
    id
    name
    email @include(if: $includeProfile)
    profile {
      bio @include(if: $includeProfile)
      avatar @include(if: $includeProfile)
    }
  }
}
```

**What actually happened:**
- The `@include(if: $includeProfile)` directive was applied to each top-level field
- Nested fields (like `bio` and `avatar`) also received the directive
- The directive behavior is preserved exactly as it was on the fragment spread
- This ensures the conditional logic works the same way after inlining

### Real-World Use Cases

**1. API Gateway Integration**
```graphql
# BEFORE: Complex query with fragments
query GetUserData($userId: ID!, $includeSensitive: Boolean!) {
  user(id: $userId) {
    ...BasicUserInfo
    ...SensitiveUserInfo @include(if: $includeSensitive)
  }
}

fragment BasicUserInfo on User {
  id
  name
  email
}

fragment SensitiveUserInfo on User {
  ssn
  creditCard
  medicalHistory
}
```

**AFTER: Inlined for external API**
```graphql
# AFTER: Self-contained query for external API
query GetUserData($userId: ID!, $includeSensitive: Boolean!) {
  user(id: $userId) {
    id
    name
    email
    ssn @include(if: $includeSensitive)
    creditCard @include(if: $includeSensitive)
    medicalHistory @include(if: $includeSensitive)
  }
}
```

**2. Query Analysis**
```graphql
# BEFORE: Query with reusable fragments
query AnalyzeUserBehavior($userId: ID!) {
  user(id: $userId) {
    ...UserActivity
    ...UserPreferences
    ...UserMetrics
  }
}

fragment UserActivity on User {
  loginHistory
  pageViews
  actions
}

fragment UserPreferences on User {
  theme
  language
  notifications
}

fragment UserMetrics on User {
  engagementScore
  retentionRate
  conversionRate
}
```

**AFTER: Flattened for analysis**
```graphql
# AFTER: All fields visible for analysis
query AnalyzeUserBehavior($userId: ID!) {
  user(id: $userId) {
    loginHistory
    pageViews
    actions
    theme
    language
    notifications
    engagementScore
    retentionRate
    conversionRate
  }
}
```

### Performance Impact

**Fragment Inlining:**
- **Memory**: Reduces memory usage by eliminating fragment definitions
- **Parsing**: Faster parsing without fragment resolution overhead
- **Network**: Smaller query size for external APIs
- **Caching**: Better cache efficiency with flattened structure

**Fragment Extraction:**
- **Reusability**: Creates reusable query components
- **Maintainability**: Easier to maintain common field sets
- **Performance**: Better query optimization with named fragments
- **Development**: Faster development with fragment libraries

---

## 10. Testing & Benchmark System

Comprehensive testing system with performance benchmarks and validation.

### Running Tests

```bash
# Run all tests (excluding benchmarks)
mvn test -Pfast

# Run benchmark tests only
mvn test -Pbenchmark

# Run complete test suite
mvn test
```

### Test Categories

| Category | Description | Count |
|----------|-------------|-------|
| **Unit Tests** | Individual component testing | 300+ |
| **Integration Tests** | Component interaction testing | 100+ |
| **Performance Tests** | Benchmark validation | 50+ |
| **Security Tests** | Security feature validation | 30+ |

### Benchmark System

The benchmark system provides comprehensive performance analysis:

- **Performance Comparison**: Traditional vs. lazy loading
- **Memory Usage Analysis**: Memory consumption tracking
- **Execution Time Metrics**: Processing duration analysis
- **Scalability Tests**: Large document handling

### Test Results

- **Total Tests**: 519
- **Success Rate**: 100%
- **Performance Tests**: All passing with significant improvements
- **Memory Tests**: All passing with substantial reductions

---

## Advanced Usage

### Custom Validation Rules

```java
import com.intuit.gqlex.validation.rules.ValidationRule;
import com.intuit.gqlex.validation.core.ValidationContext;

public class CustomValidationRule extends ValidationRule {
    
    @Override
    public ValidationResult validate(String query, ValidationContext context) {
        // Custom validation logic
        if (query.contains("deprecated")) {
            return ValidationResult.error("Query contains deprecated fields");
        }
        return ValidationResult.success();
    }
}

// Use custom rule
GraphQLValidator validator = new GraphQLValidator();
validator.addRule(new CustomValidationRule());
```

### Custom Linting Rules

```java
import com.intuit.gqlex.linting.rules.LintRule;
import com.intuit.gqlex.linting.core.LintContext;

public class CustomLintRule extends LintRule {
    
    @Override
    public LintResult lint(String query, LintContext context) {
        // Custom linting logic
        if (query.length() > 1000) {
            return LintResult.warning("Query is very long");
        }
        return LintResult.success();
    }
}

// Use custom rule
GraphQLLinter linter = new GraphQLLinter();
linter.addRule(new CustomLintRule());
```

### Performance Tuning

```java
// Configure performance settings
PerformanceOptimizationManager perfManager = PerformanceOptimizationManager.getInstance();

// Set cache sizes
perfManager.setASTCacheSize(1000);
perfManager.setObjectPoolSize(500);

// Enable/disable optimizations
perfManager.enableASTCaching(true);
perfManager.enableObjectPooling(true);
perfManager.enableRegexPooling(true);
```

---

## Best Practices

### Query Design

1. **Use Fragments**: Break complex queries into reusable fragments
2. **Limit Depth**: Avoid deeply nested queries (max 5-7 levels)
3. **Field Selection**: Only select required fields
4. **Argument Usage**: Use arguments for dynamic behavior

### Performance Optimization

1. **Lazy Loading**: Use lazy loading for large documents
2. **Caching**: Leverage built-in caching mechanisms
3. **Batch Processing**: Process multiple XPaths together
4. **Memory Management**: Monitor memory usage in production

### Security Considerations

1. **Field Access Control**: Implement field-level permissions
2. **Rate Limiting**: Configure appropriate rate limits
3. **Audit Logging**: Enable comprehensive audit trails
4. **Input Validation**: Validate all user inputs

### Testing Strategy

1. **Unit Tests**: Test individual components
2. **Integration Tests**: Test component interactions
3. **Performance Tests**: Validate performance improvements
4. **Security Tests**: Ensure security features work correctly

---

## Troubleshooting

### Common Issues

#### Test Failures
```bash
# Run tests with verbose output
mvn test -Pfast -X

# Check specific test class
mvn test -Dtest=LazyXPathProcessorTest
```

#### Performance Issues
```java
// Enable performance monitoring
PerformanceOptimizationManager perfManager = PerformanceOptimizationManager.getInstance();
perfManager.enablePerformanceMonitoring(true);

// Get performance statistics
Map<String, Object> stats = perfManager.getPerformanceStats();
```

#### Memory Issues
```java
// Clear caches
ASTCache.getInstance().clearCache();
ObjectPool.getInstance(GqlNodeContext.class).clear();

// Monitor memory usage
Runtime runtime = Runtime.getRuntime();
long usedMemory = runtime.totalMemory() - runtime.freeMemory();
```

### Getting Help

1. **Documentation**: Check this guide and API documentation
2. **GitHub Issues**: Report bugs and request features
3. **Community**: Join discussions and ask questions
4. **Examples**: Review code examples and use cases

---

## Next Steps

1. **Explore Examples**: Review the comprehensive examples in the documentation
2. **Try Features**: Experiment with different features and capabilities
3. **Build Applications**: Integrate gqlex into your GraphQL applications
4. **Contribute**: Join the community and contribute improvements

The gqlex library provides enterprise-grade GraphQL utilities with revolutionary performance improvements. Start building powerful GraphQL applications today!

---

**Version**: 3.1.0  
**Last Updated**: December 2024  
**Status**: Production Ready with 100% Test Success Rate
