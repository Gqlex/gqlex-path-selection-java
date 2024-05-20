# gqlex

## Summary
gqlex is a powerful library that offers a unique path-selection solution for GraphQL, called gqlXpath. With this ability, developers can easily navigate the GraphQL data structure and select the information they need. In addition, the library includes an advanced transformation tool that allows for complex data manipulation and conversion. 

## Introduction

I embarked on a project that demanded the manipulation of GraphQL documents and the querying of a target service using the manipulated GraphQL.

To achieve this, I realized that I needed to use some of the techniques that I had previously used to deal with JSON and XML. 

The simplicity of JSONPath and xPath, along with complementary technologies like XSD, XSLT, and JavaScript, have contributed to the widespread use of XML and JSON over the years. These tools provide developers with extensive capabilities to select and manipulate JSON or XML documents.

GraphQL and JSON are two separate schema files that serve different purposes and are not always used for externalizing or retrieving service data. Unlike JSON, which is primarily used for data storage and exchange, GraphQL documents are used only for sending queries and mutations to the GraphQL service and not for any other purpose, such as data formats like in JSON or XML.

To modify a GraphQL document, the following steps need to be followed:

1. Traverse through the GraphQL document.
2. Identify the relevant GraphQL node or nodes.
3. Manipulate the GraphQL node or nodes as required.
4. Create a new GraphQL document with the manipulated node or nodes.
5. Pass the new GraphQL document to the GraphQL server to execute the query or mutation.

# Features
## Support for GraphGL traversal
Traverse over graphql document, For more details:
[Traverse on GraphQL document in details](src/main/java/com/intuit/gqlex/traversal/readme.md)
## Support for gqlXPath
Support the path-selection, For more details:
[gqlXPath in details](src/main/java/com/intuit/gqlex/gqlxpath/readme.md)