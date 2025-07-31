# gqlex - GraphQL Path Selection & Transformation Library

[![Maven Central](https://img.shields.io/maven-central/v/com.intuit.gqlex/gqlex-path-selection-java)](https://search.maven.org/artifact/com.intuit.gqlex/gqlex-path-selection-java)
[![Java](https://img.shields.io/badge/Java-11+-blue.svg)](https://openjdk.java.net/)
[![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)](LICENSE)
[![Tests](https://img.shields.io/badge/Tests-248%20passed-brightgreen.svg)](https://github.com/Gqlex/gqlex-path-selection-java/actions)
[![Coverage](https://img.shields.io/badge/Coverage-100%25-brightgreen.svg)](https://github.com/Gqlex/gqlex-path-selection-java)

## 📋 Table of Contents

- [🚀 Overview](#-overview)
- [⚡ Quick Start](#-quick-start)
- [🎯 Core Features](#-core-features)
- [🏗️ Architecture](#️-architecture)
- [📚 Documentation](#-documentation)
- [🚀 Use Cases](#-use-cases)
- [⚡ Performance](#-performance)
- [🔧 Advanced Features](#-advanced-features)
- [🎯 Quick Examples](#-quick-examples)
- [🔗 Integration](#-integration)
- [📊 Benchmarks](#-benchmarks)
- [🛡️ Security](#️-security)
- [🤝 Contributing](#-contributing)
- [📄 License](#-license)
- [🆘 Support](#-support)

## 🚀 Overview

**gqlex** is a comprehensive Java library that provides **XPath-style navigation**, **programmatic transformation**, and **validation** for GraphQL documents. Think of it as **XPath for GraphQL** with enterprise-grade capabilities.

### ⚡ Quick Start

```xml
<dependency>
    <groupId>com.intuit.gqlex</groupId>
    <artifactId>gqlex-path-selection-java</artifactId>
    <version>2.0.1</version>
</dependency>
```

```java
import com.intuit.gqlex.common.GqlNodeContext;
import com.intuit.gqlex.gqlxpath.selector.SelectorFacade;
import com.intuit.gqlex.transformation.GraphQLTransformer;
import com.intuit.gqlex.validation.core.GraphQLValidator;
import com.intuit.gqlex.validation.rules.StructuralRule;
import com.intuit.gqlex.validation.rules.PerformanceRule;
import com.intuit.gqlex.validation.rules.SecurityRule;

// Navigate GraphQL like XPath
GqlNodeContext hero = selectorFacade.select(queryString, "//query/hero/name");

// Transform GraphQL queries programmatically
TransformationResult result = new GraphQLTransformer(queryString)
    .addField("//query/hero", "id")
    .removeField("//query/hero/friends")
    .addArgument("//query/hero", "limit", 10)
    .transform();

// Validate GraphQL queries comprehensively
ValidationResult validation = new GraphQLValidator()
    .addRule(new StructuralRule())
    .addRule(new PerformanceRule())
    .addRule(new SecurityRule())
    .validate(queryString);
```

## 🎯 Core Features

| Feature | Description | Documentation |
|---------|-------------|---------------|
| **🔍 gqlXPath Navigation** | XPath-style path selection for GraphQL | [📖 gqlXPath Guide](https://github.com/Gqlex/gqlex-path-selection-java/blob/documentation_fixing/src/main/java/com/intuit/gqlex/gqlxpath/README.md) |
| **🔧 Query Transformation** | Programmatic query modification | [📖 Transformation Guide](https://github.com/Gqlex/gqlex-path-selection-java/blob/documentation_fixing/src/main/java/com/intuit/gqlex/transformation/README.md) |
| **✅ Validation & Linting** | Comprehensive GraphQL validation | [📖 Validation Guide](docs/GRAPHQL_VALIDATION_LINTING.md) |
| **🚀 Performance Optimization** | AST caching and optimization | [📖 Performance Guide](https://github.com/Gqlex/gqlex-path-selection-java/tree/documentation_fixing/src/main/java/com/intuit/gqlex/transformation/optimization) |
| **🎨 Query Templating** | Dynamic query generation | [📖 Templating Guide](https://github.com/Gqlex/gqlex-path-selection-java/blob/documentation_fixing/src/main/java/com/intuit/gqlex/querytemplating/README.md) |

## 🏗️ Architecture

```
gqlex/
├── 🎯 gqlXPath Engine          # Path selection & navigation
├── 🔧 Transformation Engine    # Query modification & manipulation  
├── ✅ Validation System        # Security, performance, structural validation
├── 🚀 Optimization Engine     # Performance & caching
└── 🎨 Templating Engine       # Dynamic query generation
```

## 📚 Documentation

### 🎯 **Core Features**
- **[gqlXPath Navigation](https://github.com/Gqlex/gqlex-path-selection-java/blob/documentation_fixing/src/main/java/com/intuit/gqlex/gqlxpath/README.md)** - XPath-style path selection for GraphQL documents
- **[Query Transformation](https://github.com/Gqlex/gqlex-path-selection-java/blob/documentation_fixing/src/main/java/com/intuit/gqlex/transformation/README.md)** - Programmatic query modification with fluent API
- **[Validation & Linting](docs/GRAPHQL_VALIDATION_LINTING.md)** - Comprehensive GraphQL validation system
- **[Performance Optimization](https://github.com/Gqlex/gqlex-path-selection-java/tree/documentation_fixing/src/main/java/com/intuit/gqlex/transformation/optimization)** - AST caching and optimization strategies
- **[Query Templating](https://github.com/Gqlex/gqlex-path-selection-java/blob/documentation_fixing/src/main/java/com/intuit/gqlex/querytemplating/README.md)** - Dynamic query generation with variables and conditions

### 🛠️ **Advanced Topics**
- **[GraphQL Traversal](https://github.com/Gqlex/gqlex-path-selection-java/blob/documentation_fixing/src/main/java/com/intuit/gqlex/traversal/README.md)** - Document traversal and observer patterns
- **[Integration Examples](docs/GRAPHQL_VALIDATION_LINTING.md#integration-patterns)** - API Gateway, CI/CD, Development Tools
- **[Best Practices](docs/GRAPHQL_VALIDATION_LINTING.md#best-practices)** - Production deployment and optimization
- **[Examples & Use Cases](docs/GRAPHQL_VALIDATION_LINTING.md#usage-examples)** - Real-world implementation examples

## 🚀 Use Cases

### **Enterprise Applications**
- **API Gateways**: Validate and transform incoming GraphQL requests
- **Multi-Tenant Systems**: Customize queries per tenant
- **Microservices**: Route and transform queries between services
- **Security Auditing**: Detect vulnerabilities and security issues

### **Development Tools**
- **IDE Plugins**: Real-time validation and linting
- **CI/CD Pipelines**: Automated validation in build processes
- **Testing Frameworks**: Generate test queries and validate responses
- **Documentation Generators**: Extract field information and structure

### **Performance & Optimization**
- **Query Optimization**: Remove unnecessary fields and optimize structure
- **Caching Strategies**: Create cache-specific query variations
- **Load Balancing**: Distribute queries based on complexity
- **Monitoring**: Track query performance and usage patterns

## ⚡ Performance

- **🚀 Sub-millisecond** transformation times
- **💾 Memory efficient** string-based processing
- **🔄 Thread-safe** for concurrent environments
- **📈 Scalable** to handle complex enterprise schemas
- **🎯 100% test coverage** with 248 passing tests

## 🔧 Advanced Features

### **Generic & Schema-Agnostic**
```java
// Works with ANY GraphQL schema - no hardcoded assumptions
transformer.addField("//query/user", "email")           // camelCase
          .addField("//query/user_data", "user_phone")  // snake_case
          .addField("//query/UserData", "UserPhone");   // PascalCase
```

### **Complex Nested Structures**
```java
// Handle 10+ levels of nesting with ease
transformer.addField("//query/company/departments/teams/members/projects/phases/tasks/subtasks/assignee/profile/contact", "address");
```

### **Security & Validation**
```java
// Comprehensive security validation
validator.addRule(new SecurityRule())      // SQL injection, XSS, DoS
         .addRule(new PerformanceRule())    // Complexity analysis
         .addRule(new StructuralRule());    // Document integrity
```

## 🎯 Quick Examples

### **Path Selection**
```java
import com.intuit.gqlex.common.GqlNodeContext;
import com.intuit.gqlex.gqlxpath.selector.SelectorFacade;

SelectorFacade selectorFacade = new SelectorFacade();

// Select specific fields using gqlXPath
GqlNodeContext hero = selectorFacade.select(queryString, "//query/hero/name");
GqlNodeContext friends = selectorFacade.select(queryString, "//query/hero/friends");
GqlNodeContext episode = selectorFacade.select(queryString, "//query/hero/episode[type=arg]");
```

### **Query Transformation**
```java
import com.intuit.gqlex.transformation.GraphQLTransformer;

// Transform queries programmatically
TransformationResult result = transformer
    .addField("//query/hero", "id")
    .removeField("//query/hero/friends")
    .addArgument("//query/hero", "limit", 10)
    .setAlias("//query/hero", "mainHero")
    .transform();
```

### **Validation**
```java
import com.intuit.gqlex.validation.core.GraphQLValidator;
import com.intuit.gqlex.validation.rules.*;

// Validate queries comprehensively
ValidationResult result = validator.validate(queryString);
if (result.isValid()) {
    System.out.println("✅ Query is valid!");
} else {
    System.out.println("❌ Errors: " + result.getErrorCount());
    System.out.println("⚠️  Warnings: " + result.getWarningCount());
}
```

## 🔗 Integration

### **Spring Boot**
```java
import org.springframework.stereotype.Component;
import org.springframework.web.server.WebFilter;
import com.intuit.gqlex.validation.core.GraphQLValidator;
import com.intuit.gqlex.validation.rules.*;

@Component
public class GraphQLValidationFilter implements WebFilter {
    private final GraphQLValidator validator = new GraphQLValidator()
        .addRule(new StructuralRule())
        .addRule(new PerformanceRule())
        .addRule(new SecurityRule());
    
    public boolean isRequestAllowed(String query) {
        return validator.validate(query).isValid();
    }
}
```

### **API Gateway**
```java
import com.intuit.gqlex.transformation.GraphQLTransformer;

public class GraphQLGateway {
    private final GraphQLTransformer transformer = new GraphQLTransformer();
    
    public String routeQuery(String query, String targetService) {
        return transformer
            .removeField("//query/company")  // Remove service-specific fields
            .transform()
            .getQueryString();
    }
}
```

## 📊 Benchmarks

| Operation | Performance | Complexity |
|-----------|-------------|------------|
| **Path Selection** | < 1ms | O(n) where n = document size |
| **Query Transformation** | < 5ms | O(n) with string manipulation |
| **Validation** | < 10ms | O(n) with rule evaluation |
| **Complex Queries** | < 50ms | Handles 10+ levels nesting |

## 🛡️ Security

- **✅ SQL Injection Detection**: Pattern-based detection
- **✅ XSS Prevention**: Script injection detection
- **✅ DoS Protection**: Query complexity limits
- **✅ Introspection Control**: Monitor introspection usage
- **✅ Path Traversal**: Directory traversal detection

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

### **Development Setup**
```bash
git clone https://github.com/Gqlex/gqlex-path-selection-java.git
cd gqlex-path-selection-java
mvn clean install
```

### **Running Tests**
```bash
mvn test                    # Run all tests
mvn test -Dtest=ValidationRulesTest  # Run specific test
```

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

- **📖 Documentation**: [Complete Guide](docs/)
- **🐛 Issues**: [GitHub Issues](https://github.com/Gqlex/gqlex-path-selection-java/issues)
- **💬 Discussions**: [GitHub Discussions](https://github.com/Gqlex/gqlex-path-selection-java/discussions)
- **📧 Contact**: [Project Maintainers](https://github.com/Gqlex/gqlex-path-selection-java/blob/main/README.md#support)

---

**gqlex** brings enterprise-grade GraphQL manipulation capabilities to Java, making it easier to work with GraphQL documents in production environments. 🚀


