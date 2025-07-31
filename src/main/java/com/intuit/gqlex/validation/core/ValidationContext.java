package com.intuit.gqlex.validation.core;

import graphql.language.Document;
import graphql.language.Node;
import graphql.language.Field;
import graphql.language.Argument;
import graphql.language.FragmentDefinition;
import graphql.language.SelectionSet;
import graphql.language.OperationDefinition;
import com.intuit.gqlex.validation.schema.SchemaProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Provides context and utilities for GraphQL validation operations.
 * This class is completely generic and works with any GraphQL validation scenario.
 */
public class ValidationContext {
    private final Document document;
    private final SchemaProvider schemaProvider;
    private final java.util.Map<String, Object> metadata = new java.util.HashMap<>();

    /**
     * Creates a validation context with a document and optional schema provider.
     * 
     * @param document the GraphQL document to validate
     * @param schemaProvider the schema provider (can be null for schema-independent validation)
     */
    public ValidationContext(Document document, SchemaProvider schemaProvider) {
        this.document = document;
        this.schemaProvider = schemaProvider;
    }

    /**
     * Creates a validation context with just a document.
     * 
     * @param document the GraphQL document to validate
     */
    public ValidationContext(Document document) {
        this(document, null);
    }

    /**
     * Gets the document being validated.
     * 
     * @return the GraphQL document
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Gets the schema provider.
     * 
     * @return the schema provider (can be null)
     */
    public SchemaProvider getSchemaProvider() {
        return schemaProvider;
    }

    /**
     * Checks if a schema provider is available.
     * 
     * @return true if schema provider is available
     */
    public boolean hasSchemaProvider() {
        return schemaProvider != null;
    }

    /**
     * Sets metadata for the validation context.
     * 
     * @param key the metadata key
     * @param value the metadata value
     */
    public void setMetadata(String key, Object value) {
        metadata.put(key, value);
    }

    /**
     * Gets metadata from the validation context.
     * 
     * @param key the metadata key
     * @return the metadata value (can be null)
     */
    public Object getMetadata(String key) {
        return metadata.get(key);
    }

    /**
     * Gets metadata with a default value if not present.
     * 
     * @param key the metadata key
     * @param defaultValue the default value
     * @return the metadata value or default value
     */
    public Object getMetadata(String key, Object defaultValue) {
        return metadata.getOrDefault(key, defaultValue);
    }

    /**
     * Traverses all nodes in the document using a visitor pattern.
     * This method is generic and works with any GraphQL document structure.
     * 
     * @param visitor the node visitor
     */
    public void traverseNodes(NodeVisitor visitor) {
        if (document != null) {
            traverseNode(document, visitor);
        }
    }

    /**
     * Recursively traverses a node and its children.
     * 
     * @param node the node to traverse
     * @param visitor the node visitor
     */
    private void traverseNode(Node node, NodeVisitor visitor) {
        if (node == null) {
            return;
        }

        // Visit the current node
        visitor.visit(node);

        // Recursively visit children based on node type
        if (node instanceof Document) {
            Document doc = (Document) node;
            for (Node definition : doc.getDefinitions()) {
                traverseNode(definition, visitor);
            }
        } else if (node instanceof OperationDefinition) {
            OperationDefinition operation = (OperationDefinition) node;
            if (operation.getSelectionSet() != null) {
                traverseNode(operation.getSelectionSet(), visitor);
            }
        } else if (node instanceof SelectionSet) {
            SelectionSet selectionSet = (SelectionSet) node;
            for (Node selection : selectionSet.getSelections()) {
                traverseNode(selection, visitor);
            }
        } else if (node instanceof Field) {
            Field field = (Field) node;
            if (field.getArguments() != null) {
                for (Argument argument : field.getArguments()) {
                    traverseNode(argument, visitor);
                }
            }
            if (field.getSelectionSet() != null) {
                traverseNode(field.getSelectionSet(), visitor);
            }
        }
        // Add more node types as needed for comprehensive traversal
    }

    /**
     * Finds all fields that match a predicate.
     * This method is generic and works with any field names or structures.
     * 
     * @param predicate the field predicate
     * @return list of matching fields
     */
    public List<Field> findFields(Predicate<Field> predicate) {
        List<Field> fields = new ArrayList<>();
        traverseNodes(node -> {
            if (node instanceof Field && predicate.test((Field) node)) {
                fields.add((Field) node);
            }
        });
        return fields;
    }

    /**
     * Finds all arguments that match a predicate.
     * This method is generic and works with any argument names or structures.
     * 
     * @param predicate the argument predicate
     * @return list of matching arguments
     */
    public List<Argument> findArguments(Predicate<Argument> predicate) {
        List<Argument> arguments = new ArrayList<>();
        traverseNodes(node -> {
            if (node instanceof Argument && predicate.test((Argument) node)) {
                arguments.add((Argument) node);
            }
        });
        return arguments;
    }

    /**
     * Finds all fragment definitions in the document.
     * 
     * @return list of fragment definitions
     */
    public List<FragmentDefinition> findFragmentDefinitions() {
        if (document == null) {
            return new ArrayList<>();
        }
        
        return document.getDefinitions().stream()
            .filter(def -> def instanceof FragmentDefinition)
            .map(def -> (FragmentDefinition) def)
            .collect(Collectors.toList());
    }

    /**
     * Finds all operation definitions in the document.
     * 
     * @return list of operation definitions
     */
    public List<OperationDefinition> findOperationDefinitions() {
        if (document == null) {
            return new ArrayList<>();
        }
        
        return document.getDefinitions().stream()
            .filter(def -> def instanceof OperationDefinition)
            .map(def -> (OperationDefinition) def)
            .collect(Collectors.toList());
    }

    /**
     * Calculates the maximum depth of the document.
     * This method is generic and works with any GraphQL structure.
     * 
     * @return the maximum depth
     */
    public int calculateMaxDepth() {
        final int[] maxDepth = {0};
        final int[] currentDepth = {0};
        
        traverseNodes(node -> {
            if (node instanceof SelectionSet) {
                currentDepth[0]++;
                maxDepth[0] = Math.max(maxDepth[0], currentDepth[0]);
            } else if (node instanceof Field && ((Field) node).getSelectionSet() == null) {
                // Leaf field - depth calculation complete for this branch
                currentDepth[0]--;
            }
        });
        
        return maxDepth[0];
    }

    /**
     * Counts the total number of fields in the document.
     * This method is generic and works with any field names.
     * 
     * @return the total field count
     */
    public int calculateFieldCount() {
        final int[] count = {0};
        traverseNodes(node -> {
            if (node instanceof Field) {
                count[0]++;
            }
        });
        return count[0];
    }

    /**
     * Counts the total number of arguments in the document.
     * This method is generic and works with any argument names.
     * 
     * @return the total argument count
     */
    public int calculateArgumentCount() {
        final int[] count = {0};
        traverseNodes(node -> {
            if (node instanceof Argument) {
                count[0]++;
            }
        });
        return count[0];
    }

    /**
     * Checks if the document contains any introspection queries.
     * This method is generic and works with any introspection field names.
     * 
     * @return true if introspection queries are found
     */
    public boolean containsIntrospectionQueries() {
        return !findFields(field -> field.getName().startsWith("__")).isEmpty();
    }

    /**
     * Gets all unique field names in the document.
     * This method is generic and works with any field names.
     * 
     * @return set of unique field names
     */
    public java.util.Set<String> getUniqueFieldNames() {
        return findFields(field -> true).stream()
            .map(Field::getName)
            .collect(Collectors.toSet());
    }

    /**
     * Gets all unique argument names in the document.
     * This method is generic and works with any argument names.
     * 
     * @return set of unique argument names
     */
    public java.util.Set<String> getUniqueArgumentNames() {
        return findArguments(arg -> true).stream()
            .map(Argument::getName)
            .collect(Collectors.toSet());
    }

    /**
     * Interface for node visitors during traversal.
     */
    @FunctionalInterface
    public interface NodeVisitor {
        /**
         * Visits a node during traversal.
         * 
         * @param node the node being visited
         */
        void visit(Node node);
    }
} 