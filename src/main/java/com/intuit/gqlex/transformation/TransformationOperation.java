package com.intuit.gqlex.transformation;

import graphql.language.Document;

/**
 * Interface for all transformation operations.
 * Each operation represents a single transformation step that can be applied to a GraphQL document.
 */
public interface TransformationOperation {
    
    /**
     * Applies the transformation operation to the given document.
     * 
     * @param document The document to transform
     * @param context The transformation context containing state and metadata
     * @return The transformed document
     */
    Document apply(Document document, TransformationContext context);
    
    /**
     * Gets a description of this operation for debugging and logging purposes.
     * 
     * @return A human-readable description of the operation
     */
    String getDescription();
    
    /**
     * Gets the operation type for categorization.
     * 
     * @return The operation type
     */
    OperationType getOperationType();
    
    /**
     * Enumeration of operation types for categorization.
     */
    enum OperationType {
        ADD_FIELD,
        REMOVE_FIELD,
        RENAME_FIELD,
        ADD_ARGUMENT,
        UPDATE_ARGUMENT,
        REMOVE_ARGUMENT,
        RENAME_ARGUMENT,
        SET_ALIAS,
        INLINE_FRAGMENTS,
        EXTRACT_FRAGMENT,
        SORT_FIELDS,
        NORMALIZE_ARGUMENTS,
        TEMPLATE_SUBSTITUTION,
        CONDITIONAL_LOGIC
    }
} 