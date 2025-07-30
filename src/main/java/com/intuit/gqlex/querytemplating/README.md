# GraphQL Query Templating System

## TL;DR

A **generic and agnostic** GraphQL query templating engine that allows you to create **dynamic, reusable GraphQL queries** with variable substitution and conditional blocks. Works with **any GraphQL query, mutation, or subscription** without schema assumptions.

```java
// Create template with variables and conditions
QueryTemplate template = QueryTemplate.fromString(templateString);
template.setVariable("userId", "12345")
       .setCondition("includeProfile", true);

// Render the final query
String query = template.render();
```

---

## 📋 Table of Contents

- [What is Query Templating?](#what-is-query-templating)
- [Features](#features)
- [Quick Start](#quick-start)
- [Usage Examples](#usage-examples)
- [Advanced Usage](#advanced-usage)
- [API Reference](#api-reference)
- [Best Practices](#best-practices)
- [FAQ & Troubleshooting](#faq--troubleshooting)

---

## 🎯 What is Query Templating?

**Query Templating** is a system that allows you to create **reusable GraphQL query templates** with **dynamic placeholders** that can be filled in at runtime. Think of it like a "fill-in-the-blank" system for GraphQL queries.

### Key Benefits:
- ✅ **Reusability** - One template for multiple scenarios
- ✅ **Dynamic Behavior** - Runtime decisions about what data to fetch
- ✅ **Maintainability** - Single source of truth for query structure
- ✅ **Performance Optimization** - Only fetch needed data based on conditions
- ✅ **Generic & Agnostic** - Works with any GraphQL schema

---

## 🚀 Features

### Core Features:
- **Variable Substitution** - Replace `${variableName}` with runtime values
- **Conditional Blocks** - Include/exclude sections with `#if($condition) ... #end`
- **Type-Safe Variables** - Automatic formatting for strings, numbers, booleans
- **File Loading** - Load templates from `.gql` files
- **Validation** - Validate template syntax before rendering
- **Fluent API** - Chain operations for easy usage

### Supported Operations:
- ✅ **Queries** - Any GraphQL query structure
- ✅ **Mutations** - Any GraphQL mutation structure  
- ✅ **Subscriptions** - Any GraphQL subscription structure
- ✅ **Fragments** - Support for GraphQL fragments
- ✅ **Directives** - Support for GraphQL directives
- ✅ **Complex Nesting** - Deep nested structures and conditionals

---

## 🚀 Quick Start

### 1. Basic Variable Substitution

```java
// Create a template with variables
String templateString = "query GetUser($userId: ID!) {\n" +
    "  user(id: ${userId}) {\n" +
    "    id\n" +
    "    name\n" +
    "    email\n" +
    "  }\n" +
    "}";

QueryTemplate template = QueryTemplate.fromString(templateString);
template.setVariable("userId", "12345");

String query = template.render();
// Result:
// query GetUser($userId: ID!) {
//   user(id: "12345") {
//     id
//     name
//     email
//   }
// }
```

### 2. Conditional Blocks

```java
String templateString = "query GetUser($userId: ID!) {\n" +
    "  user(id: ${userId}) {\n" +
    "    id\n" +
    "    name\n" +
    "    email\n" +
    "    #if($includeProfile)\n" +
    "    profile {\n" +
    "      bio\n" +
    "      avatar\n" +
    "    }\n" +
    "    #end\n" +
    "  }\n" +
    "}";

QueryTemplate template = QueryTemplate.fromString(templateString);
template.setVariable("userId", "12345")
       .setCondition("includeProfile", true);

String query = template.render();
// Result includes profile fields when includeProfile is true
```

---

## 📝 Usage Examples

### 1. Query Templates

```java
// User query with optional fields
String userTemplate = "query GetUser($userId: ID!) {\n" +
    "  user(id: ${userId}) {\n" +
    "    id\n" +
    "    name\n" +
    "    email\n" +
    "    #if($includeProfile)\n" +
    "    profile {\n" +
    "      bio\n" +
    "      avatar\n" +
    "      #if($includeLocation)\n" +
    "      location {\n" +
    "        city\n" +
    "        country\n" +
    "      }\n" +
    "      #end\n" +
    "    }\n" +
    "    #end\n" +
    "    #if($includePosts)\n" +
    "    posts {\n" +
    "      id\n" +
    "      title\n" +
    "      content\n" +
    "    }\n" +
    "    #end\n" +
    "  }\n" +
    "}";

QueryTemplate template = QueryTemplate.fromString(userTemplate);
template.setVariable("userId", "12345")
       .setCondition("includeProfile", true)
       .setCondition("includeLocation", false)
       .setCondition("includePosts", true);

String query = template.render();
```

### 2. Mutation Templates

```java
// Update user mutation
String mutationTemplate = "mutation UpdateUser($userId: ID!, $input: UserInput!) {\n" +
    "  updateUser(id: ${userId}, input: ${input}) {\n" +
    "    id\n" +
    "    name\n" +
    "    email\n" +
    "    #if($includeProfile)\n" +
    "    profile {\n" +
    "      bio\n" +
    "      avatar\n" +
    "    }\n" +
    "    #end\n" +
    "  }\n" +
    "}";

QueryTemplate template = QueryTemplate.fromString(mutationTemplate);
template.setVariable("userId", "12345")
       .setVariable("input", "{name: \"John Doe\", email: \"john@example.com\"}")
       .setCondition("includeProfile", true);

String mutation = template.render();
```

### 3. Subscription Templates

```java
// User activity subscription
String subscriptionTemplate = "subscription UserUpdates($userId: ID!) {\n" +
    "  userUpdates(userId: ${userId}) {\n" +
    "    id\n" +
    "    name\n" +
    "    email\n" +
    "    #if($includeActivity)\n" +
    "    activity {\n" +
    "      type\n" +
    "      timestamp\n" +
    "      details\n" +
    "    }\n" +
    "    #end\n" +
    "  }\n" +
    "}";

QueryTemplate template = QueryTemplate.fromString(subscriptionTemplate);
template.setVariable("userId", "12345")
       .setCondition("includeActivity", true);

String subscription = template.render();
```

### 4. File-Based Templates

```java
// Load template from file
QueryTemplate template = QueryTemplate.fromFile("templates/user_query.gql");
template.setVariable("userId", "12345")
       .setCondition("includeProfile", true);

String query = template.render();
```

### 5. Batch Variable Setting

```java
// Set multiple variables at once
Map<String, Object> variables = new HashMap<>();
variables.put("userId", "12345");
variables.put("includeProfile", true);
variables.put("limit", 10);

Map<String, Boolean> conditions = new HashMap<>();
conditions.put("includePosts", true);
conditions.put("includeComments", false);

QueryTemplate template = QueryTemplate.fromString(templateString);
template.setVariables(variables)
       .setConditions(conditions);

String query = template.render();
```

---

## 🔧 Advanced Usage

### 1. Complex Nested Conditionals

```java
String complexTemplate = "query ComplexQuery($userId: ID!) {\n" +
    "  user(id: ${userId}) {\n" +
    "    id\n" +
    "    name\n" +
    "    email\n" +
    "    #if($includeProfile)\n" +
    "    profile {\n" +
    "      bio\n" +
    "      avatar\n" +
    "      #if($includeLocation)\n" +
    "      location {\n" +
    "        city\n" +
    "        country\n" +
    "        #if($includeCoordinates)\n" +
    "        coordinates {\n" +
    "          lat\n" +
    "          lng\n" +
    "        }\n" +
    "        #end\n" +
    "      }\n" +
    "      #end\n" +
    "    }\n" +
    "    #end\n" +
    "    #if($includePosts)\n" +
    "    posts {\n" +
    "      id\n" +
    "      title\n" +
    "      #if($includeComments)\n" +
    "      comments {\n" +
    "        id\n" +
    "        text\n" +
    "        author {\n" +
    "          name\n" +
    "        }\n" +
    "      }\n" +
    "      #end\n" +
    "    }\n" +
    "    #end\n" +
    "  }\n" +
    "}";
```

### 2. Dynamic Dashboard Queries

```java
// Dashboard with user preferences
String dashboardTemplate = "query Dashboard($dateRange: DateRange!) {\n" +
    "  analytics(dateRange: ${dateRange}) {\n" +
    "    #if($showRevenue)\n" +
    "    revenue {\n" +
    "      total\n" +
    "      growth\n" +
    "      #if($showDetailedRevenue)\n" +
    "      breakdown {\n" +
    "        byProduct\n" +
    "        byRegion\n" +
    "      }\n" +
    "      #end\n" +
    "    }\n" +
    "    #end\n" +
    "    #if($showUsers)\n" +
    "    users {\n" +
    "      active\n" +
    "      new\n" +
    "      #if($showUserDetails)\n" +
    "      demographics {\n" +
    "        age\n" +
    "        location\n" +
    "      }\n" +
    "      #end\n" +
    "    }\n" +
    "    #end\n" +
    "    #if($showMetrics)\n" +
    "    metrics {\n" +
    "      conversion\n" +
    "      retention\n" +
    "    }\n" +
    "    #end\n" +
    "  }\n" +
    "}";

// Apply user preferences
UserPreferences prefs = userService.getPreferences(userId);
QueryTemplate template = QueryTemplate.fromString(dashboardTemplate);
template.setVariable("dateRange", prefs.getDateRange())
       .setCondition("showRevenue", prefs.isShowRevenue())
       .setCondition("showDetailedRevenue", prefs.isShowDetailedRevenue())
       .setCondition("showUsers", prefs.isShowUsers())
       .setCondition("showUserDetails", prefs.isShowUserDetails())
       .setCondition("showMetrics", prefs.isShowMetrics());
```

### 3. A/B Testing Queries

```java
// A/B test different query structures
ABTestConfig testConfig = abTestService.getConfig("query_optimization");

String productTemplate = "query GetProduct($productId: ID!) {\n" +
    "  product(id: ${productId}) {\n" +
    "    id\n" +
    "    name\n" +
    "    price\n" +
    "    #if($includeDescription)\n" +
    "    description\n" +
    "    #end\n" +
    "    #if($includeReviews)\n" +
    "    reviews {\n" +
    "      rating\n" +
    "      comment\n" +
    "    }\n" +
    "    #end\n" +
    "    #if($includeSpecifications)\n" +
    "    specifications {\n" +
    "      dimensions\n" +
    "      weight\n" +
    "      materials\n" +
    "    }\n" +
    "    #end\n" +
    "  }\n" +
    "}";

QueryTemplate template = QueryTemplate.fromString(productTemplate);
template.setVariable("productId", "12345");

if (testConfig.getVariant() == "minimal") {
    template.setCondition("includeDescription", false)
           .setCondition("includeReviews", false)
           .setCondition("includeSpecifications", false);
} else if (testConfig.getVariant() == "detailed") {
    template.setCondition("includeDescription", true)
           .setCondition("includeReviews", true)
           .setCondition("includeSpecifications", true);
}
```

---

## 📚 API Reference

### QueryTemplate Class

#### Static Factory Methods

```java
// Create from string
public static QueryTemplate fromString(String template)

// Create from file
public static QueryTemplate fromFile(String filePath) throws TemplateException
```

#### Variable Methods

```java
// Set single variable
public QueryTemplate setVariable(String name, Object value)

// Set multiple variables
public QueryTemplate setVariables(Map<String, Object> variables)
```

#### Condition Methods

```java
// Set single condition
public QueryTemplate setCondition(String name, boolean value)

// Set multiple conditions
public QueryTemplate setConditions(Map<String, Boolean> conditions)
```

#### Rendering & Validation

```java
// Render the template
public String render() throws TemplateException

// Validate template syntax
public boolean validate() throws TemplateException

// Clear all variables and conditions
public QueryTemplate clear()
```

#### Utility Methods

```java
// Get template string
public String getTemplateString()

// Get all variables
public Map<String, Object> getVariables()

// Get all conditions
public Map<String, Boolean> getConditions()
```

### TemplateException Class

```java
// Exception for template-related errors
public class TemplateException extends Exception {
    public TemplateException(String message)
    public TemplateException(String message, Throwable cause)
    public TemplateException(Throwable cause)
}
```

---

## 🎯 Best Practices

### 1. Template Organization

```java
// ✅ Good: Organize templates by feature
String userTemplates = "templates/users/";
String productTemplates = "templates/products/";
String analyticsTemplates = "templates/analytics/";

// ✅ Good: Use descriptive template names
QueryTemplate.fromFile("templates/users/get_user_with_profile.gql");
QueryTemplate.fromFile("templates/products/get_product_with_reviews.gql");
```

### 2. Variable Naming

```java
// ✅ Good: Use descriptive variable names
template.setVariable("userId", "12345");
template.setVariable("includeUserProfile", true);
template.setVariable("maxResults", 100);

// ❌ Avoid: Generic names
template.setVariable("id", "12345");
template.setVariable("include", true);
template.setVariable("limit", 100);
```

### 3. Conditional Logic

```java
// ✅ Good: Use meaningful condition names
template.setCondition("includeProfile", true);
template.setCondition("includePosts", false);
template.setCondition("showSensitiveData", user.isAdmin());

// ❌ Avoid: Generic condition names
template.setCondition("flag1", true);
template.setCondition("flag2", false);
```

### 4. Error Handling

```java
try {
    QueryTemplate template = QueryTemplate.fromString(templateString);
    template.setVariable("userId", "12345");
    
    // Validate before rendering
    if (template.validate()) {
        String query = template.render();
        // Use the query
    }
} catch (TemplateException e) {
    // Handle template errors
    logger.error("Template error: " + e.getMessage(), e);
}
```

### 5. Performance Optimization

```java
// ✅ Good: Reuse templates for similar queries
QueryTemplate userTemplate = QueryTemplate.fromFile("user_template.gql");

// Different user queries with same template
for (String userId : userIds) {
    userTemplate.clear()
               .setVariable("userId", userId)
               .setCondition("includeProfile", true);
    String query = userTemplate.render();
    // Execute query
}
```

---

## ❓ FAQ & Troubleshooting

### Q: What happens if a variable is not set?
**A:** A `TemplateException` is thrown with a clear error message indicating which variable is missing.

### Q: Can I use nested conditionals?
**A:** Yes! You can nest `#if($condition)` blocks as deeply as needed.

### Q: What types of values can I use for variables?
**A:** Any Java object. The system automatically formats:
- **Strings**: Wrapped in quotes
- **Numbers**: Used as-is
- **Booleans**: Used as-is
- **Null**: Rendered as "null"
- **Other objects**: Converted to string representation

### Q: How do I handle complex GraphQL input objects?
**A:** Pass them as formatted strings:
```java
template.setVariable("input", "{name: \"John\", age: 30, active: true}");
```

### Q: Can I use this with any GraphQL schema?
**A:** Yes! The templating system is completely generic and agnostic to any specific GraphQL schema.

### Q: What if my template has syntax errors?
**A:** Use the `validate()` method to check for errors before rendering:
```java
if (template.validate()) {
    String query = template.render();
} else {
    // Handle validation errors
}
```

### Q: How do I debug template rendering issues?
**A:** Check the template string and variables:
```java
System.out.println("Template: " + template.getTemplateString());
System.out.println("Variables: " + template.getVariables());
System.out.println("Conditions: " + template.getConditions());
```

---

## 🔗 Integration Examples

### With GraphQL Client

```java
// Apollo Client (JavaScript)
const template = QueryTemplate.fromString(templateString);
template.setVariable("userId", "12345");
const query = template.render();

const { data } = await client.query({
  query: gql`${query}`,
  variables: { userId: "12345" }
});
```

### With Spring Boot

```java
@Service
public class GraphQLService {
    
    @Autowired
    private GraphQLTemplate graphQLTemplate;
    
    public User getUser(String userId, boolean includeProfile) {
        QueryTemplate template = QueryTemplate.fromFile("templates/user.gql");
        template.setVariable("userId", userId)
               .setCondition("includeProfile", includeProfile);
        
        String query = template.render();
        
        return graphQLTemplate.query(query, User.class);
    }
}
```

### With React/Apollo

```java
// Backend: Generate dynamic queries
QueryTemplate template = QueryTemplate.fromString(templateString);
template.setVariable("userId", userId)
       .setCondition("includeProfile", includeProfile);

String query = template.render();
// Send query to frontend or execute directly
```

---

## 🚀 Getting Started Checklist

- [ ] **Install the library** in your project
- [ ] **Create your first template** with variables
- [ ] **Test variable substitution** with simple values
- [ ] **Add conditional blocks** for dynamic behavior
- [ ] **Organize templates** in a dedicated folder structure
- [ ] **Implement error handling** for template exceptions
- [ ] **Add validation** before rendering templates
- [ ] **Optimize performance** by reusing templates
- [ ] **Document your templates** with clear naming conventions

---

**Ready to create dynamic, reusable GraphQL queries?** Start with the basic examples above and gradually adopt more advanced features as your needs grow. The Query Templating system is designed to scale with your application and provide immediate value while enabling future enhancements! 🚀 