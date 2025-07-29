# gqlex - GraphQL Path Selection Library

[![Maven Central](https://img.shields.io/maven-central/v/com.intuit.gqlex/gqlex-path-selection-java)](https://search.maven.org/artifact/com.intuit.gqlex/gqlex-path-selection-java)
[![Java](https://img.shields.io/badge/Java-11+-blue.svg)](https://openjdk.java.net/)
[![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)](LICENSE)

## Overview

**gqlex** is a powerful Java library that provides **gqlXPath** - a path-selection solution for GraphQL documents, similar to XPath for XML or JSONPath for JSON. The library enables developers to navigate and select specific nodes within GraphQL queries, mutations, and fragments using a path-like syntax.

## Features

### 🚀 GraphQL Traversal Engine
- **Observable Design Pattern**: Uses an observer pattern to traverse GraphQL documents
- **Comprehensive Coverage**: Traverses all GraphQL elements including queries, mutations, fields, arguments, directives, variables, and fragments
- **Context-Aware**: Provides rich context for each visited element including current node, parent node, depth level, and node stack

### 🎯 gqlXPath Path Selection Language
- **XPath-like Syntax**: Familiar path expressions for GraphQL document navigation
- **Flexible Selection**: Support for exact paths, wildcards, and range-based selection
- **Type-Safe**: Strong typing with comprehensive element type support
- **Performance Optimized**: Tunable traversal for specific element types

### 🔧 Advanced Capabilities
- **Programmatic Path Building**: Fluent API for constructing path expressions
- **Multi-Select Support**: Select multiple nodes with range expressions
- **Wildcard Navigation**: Use `...` for flexible path matching
- **Attribute-Based Selection**: Select by name, alias, or element type

## Quick Start

### Maven Dependency

```xml
<dependency>
    <groupId>com.intuit.gqlex</groupId>
    <artifactId>gqlex-path-selection-java</artifactId>
    <version>2.0.1</version>
</dependency>
```

### Basic Usage

```java
import com.intuit.gqlex.common.GqlNodeContext;
import com.intuit.gqlex.gqlxpath.selector.SelectorFacade;

// Create selector facade
SelectorFacade selectorFacade = new SelectorFacade();

// Your GraphQL query
String queryString = """
    query Hero($episode: Episode) {
        hero(episode: $episode) {
            name
            friends {
                name
            }
        }
    }
    """;

// Select specific field using gqlXPath
GqlNodeContext result = selectorFacade.select(queryString, "//query/hero/name");
```

## gqlXPath Syntax

### Path Expressions

| Expression | Description |
|------------|-------------|
| `//` | Select all nodes from root (multi-select) |
| `/` | Select first node from root (single select) |
| `{x:y}/` | Range selection (from index x to y, inclusive) |
| `{:y}//` | Range from start to y |
| `{x:}/` | Range from x to end |
| `...` | Wildcard/any path element |

### Element Selection

| Attribute | Description | Example |
|-----------|-------------|---------|
| `type=` | Select by element type | `type=arg` for arguments |
| `name=` | Select by name | `name=hero` |
| `alias=` | Select by alias | `alias=leftComparison` |

### Supported Element Types

| Type | Description | Abbreviation |
|------|-------------|--------------|
| Document | GraphQL document root | `doc` |
| Operation Definition | Query operations | `query` |
| Mutation Definition | Mutation operations | `mutation` |
| Field | GraphQL fields | `fld` |
| Argument | Field arguments | `arg` |
| Directive | GraphQL directives | `direc` |
| Variable Definition | Query variables | `var` |
| Fragment Definition | Named fragments | `frag` |
| Inline Fragment | Inline fragments | `infrag` |

## Usage Examples

### Text-Based Path Selection

```java
// Select hero field
GqlNodeContext hero = selectorFacade.select(queryString, "//query/hero");

// Select friends field under hero
GqlNodeContext friends = selectorFacade.select(queryString, "//query/hero/friends");

// Select episode argument
GqlNodeContext episodeArg = selectorFacade.select(queryString, "//query/hero/episode[type=arg]");

// Select directive argument
GqlNodeContext directiveArg = selectorFacade.select(queryString, "//query/hero/friends/include[type=direc]/if[type=arg]");
```

### Programmatic Path Building

```java
import com.intuit.gqlex.gqlxpath.syntax.SyntaxBuilder;

SyntaxBuilder builder = new SyntaxBuilder();
builder.appendQuery()
       .appendField("hero")
       .appendField("name");
SyntaxPath path = builder.build();

GqlNodeContext result = selectorFacade.select(queryString, path);
```

### Advanced Selection Patterns

```java
// Select any name field under hero (using wildcard)
GqlNodeContext anyName = selectorFacade.select(queryString, "//query/hero/.../name");

// Select multiple friends fields (range selection)
List<GqlNodeContext> friendsRange = selectorFacade.selectMany(queryString, "{0:2}//query/hero/friends");

// Select by alias
GqlNodeContext aliasedField = selectorFacade.select(queryString, "//query/hero[alias=leftComparison]");

// Select variable definition
GqlNodeContext variable = selectorFacade.select(queryString, "//query/episode[type=var]");
```

### Working with Fragments

```java
// Select fragment definition
GqlNodeContext fragment = selectorFacade.select(queryString, "//comparisonFields[type=frag]");

// Select field within fragment
GqlNodeContext fragmentField = selectorFacade.select(queryString, "//comparisonFields[type=frag]/name");

// Select inline fragment
GqlNodeContext inlineFragment = selectorFacade.select(queryString, "//query/hero/Droid[type=infrag]");
```

## GraphQL Traversal

### Custom Traversal Observer

```java
import com.intuit.gqlex.traversal.*;

public class CustomTraversalObserver implements TraversalObserver {
    
    @Override
    public void updateNodeEntry(Node node, Node parentNode, Context context, ObserverAction observerAction) {
        DocumentElementType type = context.getDocumentElementType();
        int level = context.getLevel();
        
        // Process the node based on type and level
        switch (type) {
            case FIELD:
                Field field = (Field) node;
                System.out.println("Field: " + field.getName() + " at level " + level);
                break;
            case ARGUMENT:
                Argument arg = (Argument) node;
                System.out.println("Argument: " + arg.getName() + " at level " + level);
                break;
            // Handle other types...
        }
    }
    
    @Override
    public void updateNodeExit(Node node, Node parentNode, Context context, ObserverAction observerAction) {
        // Handle node exit if needed
    }
}

// Usage
GqlTraversal traversal = new GqlTraversal();
traversal.getGqlTraversalObservable().addObserver(new CustomTraversalObserver());
traversal.traverse(queryString);
```

## Architecture

### Core Components

- **`GqlTraversal`**: Main traversal engine that walks through GraphQL documents
- **`SelectorFacade`**: Main API for path selection operations
- **`SearchNodesObserver`**: Observer that matches nodes against path expressions
- **`SyntaxBuilder`**: Fluent API for building path expressions programmatically
- **`DocumentElementType`**: Enum defining all GraphQL element types

### Design Patterns

- **Observer Pattern**: For traversal notifications
- **Builder Pattern**: For constructing path expressions
- **Facade Pattern**: For simplified API access

## Use Cases

1. **GraphQL Query Analysis**: Extract specific fields or arguments
2. **Query Transformation**: Modify GraphQL queries programmatically
3. **Validation**: Check for specific patterns in GraphQL documents
4. **Testing**: Verify GraphQL query structure
5. **Documentation Generation**: Extract field information
6. **Query Optimization**: Analyze and optimize GraphQL queries

## Requirements

- **Java**: 11 or higher
- **GraphQL-Java**: 22.1 (automatically included)
- **Maven**: For dependency management

## Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Documentation

For detailed documentation on specific features:

- [GraphQL Traversal Details](src/main/java/com/intuit/gqlex/traversal/readme.md)
- [gqlXPath Language Reference](src/main/java/com/intuit/gqlex/gqlxpath/readme.md)

## Support

If you encounter any issues or have questions, please:

1. Check the [documentation](src/main/java/com/intuit/gqlex/gqlxpath/readme.md)
2. Review existing [issues](https://github.com/gqlex/gqlex-path-selection-java/issues)
3. Create a new issue with detailed information

---

**gqlex** brings the power and flexibility of XPath-style navigation to GraphQL, making it easier to programmatically work with GraphQL documents in Java applications.


