package com.intuit.gqlex.validation.schema;

import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLArgument;

/**
 * Abstract interface for accessing GraphQL schema information.
 * This interface is completely generic and works with any GraphQL schema.
 */
public interface SchemaProvider {
    
    /**
     * Gets the GraphQL schema.
     * 
     * @return the GraphQL schema
     */
    GraphQLSchema getSchema();
    
    /**
     * Checks if a type exists in the schema.
     * This method is generic and works with any type name.
     * 
     * @param typeName the name of the type to check
     * @return true if the type exists
     */
    boolean hasType(String typeName);
    
    /**
     * Gets a type from the schema.
     * This method is generic and works with any type name.
     * 
     * @param typeName the name of the type to get
     * @return the GraphQL type, or null if not found
     */
    GraphQLType getType(String typeName);
    
    /**
     * Checks if a field exists on a type.
     * This method is generic and works with any field names.
     * 
     * @param typeName the name of the type
     * @param fieldName the name of the field
     * @return true if the field exists
     */
    boolean hasField(String typeName, String fieldName);
    
    /**
     * Gets a field definition from a type.
     * This method is generic and works with any field names.
     * 
     * @param typeName the name of the type
     * @param fieldName the name of the field
     * @return the field definition, or null if not found
     */
    GraphQLFieldDefinition getField(String typeName, String fieldName);
    
    /**
     * Checks if an argument exists on a field.
     * This method is generic and works with any argument names.
     * 
     * @param typeName the name of the type
     * @param fieldName the name of the field
     * @param argumentName the name of the argument
     * @return true if the argument exists
     */
    boolean hasArgument(String typeName, String fieldName, String argumentName);
    
    /**
     * Gets an argument definition from a field.
     * This method is generic and works with any argument names.
     * 
     * @param typeName the name of the type
     * @param fieldName the name of the field
     * @param argumentName the name of the argument
     * @return the argument definition, or null if not found
     */
    GraphQLArgument getArgument(String typeName, String fieldName, String argumentName);
} 