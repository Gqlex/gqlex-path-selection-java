# GraphQL Query Transformation Engine

## Overview

The GraphQL Query Transformation Engine is a powerful addition to the gqlex library that enables programmatic modification, templating, and management of GraphQL queries. It provides a fluent API for dynamically constructing and transforming GraphQL queries, making your GraphQL applications more flexible and maintainable.

## Features

### 1. Programmatic Query Modification

- **Field Operations**: Add, remove, rename fields
- **Argument Operations**: Add, update, remove, rename arguments
- **Alias Management**: Set field aliases
- **Fragment Operations**: Inline fragments, extract fragments

### 2. Query Templating System

- **Variable Substitution**: Replace placeholders with actual values
- **Conditional Logic**: Include/exclude fields based on conditions
- **Template Validation**: Ensure all required variables are set

### 3. Query Optimization

- **Field Sorting**: Sort fields for consistent caching
- **Argument Normalization**: Normalize arguments for consistent caching
- **Fragment Management**: Optimize fragment usage

## Core Classes

### GraphQLTransformer

The main entry point for query transformations. Provides a fluent API for programmatically modifying GraphQL queries.

```java
GraphQLTransformer transformer = new GraphQLTransformer(queryString);

TransformationResult result = transformer
    .addField("//query/user", "age")
    .removeField("//query/user/email")
    .addArgument("//query/user", "includeArchived", true)
    .setAlias("//query/user", "currentUser")
    .transform();
```

### QueryTemplate

Template system for GraphQL queries with variable substitution and conditional logic.

```java
QueryTemplate template = new QueryTemplate(templateString);

String result = template
    .setVariable("userId", "123")
    .setVariable("includeProfile", true)
    .setCondition("showRevenue", true)
    .setCondition("showUsers", false)
    .render();
```

### TransformationContext

Holds state and metadata during query transformations.

### TransformationResult

Contains the result of a transformation operation, including success status, errors, and the transformed document.

### TransformationOperation

Interface for all transformation operations. Each operation represents a single transformation step.

## Usage Examples

### Basic Field Operations

```java
// Add fields
GraphQLTransformer transformer = new GraphQLTransformer(query);
transformer.addField("//query/user", "age");
transformer.addField("//query/user", "phone");

// Remove fields
transformer.removeField("//query/user/email");

// Rename fields
transformer.renameField("//query/user/email", "emailAddress");
```

### Argument Operations

```java
// Add arguments
transformer.addArgument("//query/user", "limit", 10);
transformer.addArgument("//query/user", "includeArchived", true);

// Update arguments
transformer.updateArgument("//query/user", "episode", "EMPIRE");

// Remove arguments
transformer.removeArgument("//query/user", "oldArg");
```

### Alias Management

```java
// Set aliases
transformer.setAlias("//query/user", "currentUser");
transformer.setAlias("//query/hero", "mainHero");
```

### Fragment Operations

```java
// Inline all fragments
transformer.inlineAllFragments();

// Extract fragments
transformer.extractFragment("//query/user", "UserFields", "User");
```

### Template Usage

```java
// Create template with variables
String template = "query User($userId: ID!) {\n" +
    "    user(id: ${userId}) {\n" +
    "        name\n" +
    "        #if($includeProfile)\n" +
    "        profile {\n" +
    "            bio\n" +
    "        }\n" +
    "        #end\n" +
    "    }\n" +
    "}";

QueryTemplate queryTemplate = new QueryTemplate(template);
String result = queryTemplate
    .setVariable("userId", "123")
    .setCondition("includeProfile", true)
    .render();
```

### Optimization Operations

```java
// Sort fields for consistent caching
transformer.sortFields("//query/user");

// Normalize arguments for consistent caching
transformer.normalizeArguments("//query/user");
```

## Error Handling

The transformation engine provides comprehensive error handling:

```java
TransformationResult result = transformer.transform();

if (result.isSuccess()) {
    String transformedQuery = result.getQueryString();
    // Use the transformed query
} else {
    // Handle errors
    result.getErrors().forEach(System.err::println);
}
```

## Integration with Existing gqlex Features

The transformation engine integrates seamlessly with existing gqlex features:

```java
// Use gqlXPath for precise targeting
transformer.addField("//query/user[name=hero]", "newField");

// Combine with traversal
GqlTraversal traversal = new GqlTraversal();
GraphQLTransformer transformer = new GraphQLTransformer(traversal.getDocument());
```

## Best Practices

1. **Validate Templates**: Always validate templates before rendering
2. **Handle Errors**: Check transformation results for errors
3. **Use Meaningful Paths**: Use descriptive gqlXPath expressions
4. **Optimize for Caching**: Use sorting and normalization for better cache performance
5. **Test Transformations**: Test your transformations with various inputs

## Performance Considerations

- The transformation engine is designed for efficiency
- Operations are applied in sequence for predictable results
- Large documents may require optimization for better performance
- Consider caching transformed queries when appropriate

## Future Enhancements

- Schema-aware transformations with automatic validation
- Query optimization suggestions based on performance metrics
- Visual query builder for non-technical users
- Advanced caching strategies with intelligent invalidation
- Real-time query monitoring and analytics

## Contributing

When contributing to the transformation engine:

1. Follow the existing code style
2. Add comprehensive tests for new features
3. Update documentation for API changes
4. Ensure backward compatibility
5. Consider performance implications

## Support

For issues and questions related to the transformation engine:

1. Check the existing tests for usage examples
2. Review the main gqlex documentation
3. Open an issue on the project repository
4. Contribute improvements and fixes 