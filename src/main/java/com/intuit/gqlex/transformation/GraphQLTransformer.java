package com.intuit.gqlex.transformation;

import com.intuit.gqlex.common.GqlNode;
import com.intuit.gqlex.common.DocumentElementType;
import com.intuit.gqlex.traversal.GqlTraversal;
import com.intuit.gqlex.traversal.GqlTraversalObservable;
import com.intuit.gqlex.traversal.Context;
import com.intuit.gqlex.transformation.operations.*;
import graphql.language.*;
import graphql.parser.Parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Main entry point for GraphQL query transformations.
 * Provides a fluent API for programmatically modifying GraphQL queries.
 */
public class GraphQLTransformer {
    
    private final Document originalDocument;
    private final TransformationContext context;
    private final List<TransformationOperation> operations;
    private final Map<String, Object> variables;
    private final Map<String, Boolean> conditions;
    
    /**
     * Creates a transformer from a GraphQL query string.
     */
    public GraphQLTransformer(String queryString) {
        this.originalDocument = parseQuery(queryString);
        this.context = new TransformationContext(originalDocument);
        this.operations = new ArrayList<>();
        this.variables = new HashMap<>();
        this.conditions = new HashMap<>();
    }
    
    /**
     * Creates a transformer from a GraphQL document file.
     */
    public GraphQLTransformer(File queryFile) throws IOException {
        this(Files.readString(queryFile.toPath()));
    }
    
    /**
     * Creates a transformer from a GraphQL Document object.
     */
    public GraphQLTransformer(Document document) {
        this.originalDocument = document;
        this.context = new TransformationContext(originalDocument);
        this.operations = new ArrayList<>();
        this.variables = new HashMap<>();
        this.conditions = new HashMap<>();
    }
    
    /**
     * Adds a field to the specified path.
     */
    public GraphQLTransformer addField(String path, String fieldName) {
        return addField(path, fieldName, Collections.emptyMap());
    }
    
    /**
     * Adds a field to the specified path with arguments.
     */
    public GraphQLTransformer addField(String path, String fieldName, Map<String, Object> arguments) {
        operations.add(new AddFieldOperation(path, fieldName, arguments));
        return this;
    }
    
    /**
     * Removes a field from the specified path.
     */
    public GraphQLTransformer removeField(String path) {
        operations.add(new RemoveFieldOperation(path));
        return this;
    }
    
    /**
     * Adds an argument to the specified path.
     */
    public GraphQLTransformer addArgument(String path, String argumentName, Object value) {
        operations.add(new AddArgumentOperation(path, argumentName, value));
        return this;
    }
    
    /**
     * Updates an argument at the specified path.
     */
    public GraphQLTransformer updateArgument(String path, String argumentName, Object value) {
        operations.add(new UpdateArgumentOperation(path, argumentName, value));
        return this;
    }
    
    /**
     * Removes an argument from the specified path.
     */
    public GraphQLTransformer removeArgument(String path, String argumentName) {
        operations.add(new RemoveArgumentOperation(path, argumentName));
        return this;
    }
    
    /**
     * Sets an alias for the specified path.
     */
    public GraphQLTransformer setAlias(String path, String alias) {
        operations.add(new SetAliasOperation(path, alias));
        return this;
    }
    
    /**
     * Renames a field at the specified path.
     */
    public GraphQLTransformer renameField(String path, String newName) {
        operations.add(new RenameFieldOperation(path, newName));
        return this;
    }
    
    /**
     * Renames an argument at the specified path.
     */
    public GraphQLTransformer renameArgument(String path, String oldName, String newName) {
        operations.add(new RenameArgumentOperation(path, oldName, newName));
        return this;
    }
    
    /**
     * Sets a variable for template substitution.
     */
    public GraphQLTransformer setVariable(String name, Object value) {
        variables.put(name, value);
        return this;
    }
    
    /**
     * Sets a condition for conditional field inclusion.
     */
    public GraphQLTransformer setCondition(String name, boolean value) {
        conditions.put(name, value);
        return this;
    }
    
    /**
     * Inlines all fragments in the query.
     */
    public GraphQLTransformer inlineAllFragments() {
        operations.add(new InlineFragmentsOperation());
        return this;
    }
    
    /**
     * Sorts fields at the specified path for consistent caching.
     */
    public GraphQLTransformer sortFields(String path) {
        operations.add(new SortFieldsOperation(path));
        return this;
    }
    
    /**
     * Normalizes arguments at the specified path for consistent caching.
     */
    public GraphQLTransformer normalizeArguments(String path) {
        operations.add(new NormalizeArgumentsOperation(path));
        return this;
    }
    
    /**
     * Extracts a fragment from the specified path.
     */
    public GraphQLTransformer extractFragment(String path, String fragmentName, String typeCondition) {
        operations.add(new ExtractFragmentOperation(path, fragmentName, typeCondition));
        return this;
    }
    
    /**
     * Applies all transformations and returns the result.
     */
    public TransformationResult transform() {
        try {
            Document transformedDocument = originalDocument;
            
            // Apply all operations in sequence
            for (TransformationOperation operation : operations) {
                transformedDocument = operation.apply(transformedDocument, context);
            }
            
            // Apply template substitutions
            transformedDocument = applyTemplateSubstitutions(transformedDocument);
            
            // Apply conditional logic
            transformedDocument = applyConditionalLogic(transformedDocument);
            
            return new TransformationResult(transformedDocument, true, Collections.emptyList(), operations);
            
        } catch (Exception e) {
            List<String> errors = Arrays.asList("Transformation failed: " + e.getMessage());
            return new TransformationResult(originalDocument, false, errors);
        }
    }
    
    /**
     * Parses a GraphQL query string into a Document.
     */
    private Document parseQuery(String queryString) {
        Parser parser = new Parser();
        return parser.parseDocument(queryString);
    }
    
    /**
     * Applies template variable substitutions.
     */
    private Document applyTemplateSubstitutions(Document document) {
        // This is a simplified implementation
        // In a full implementation, you would traverse the document
        // and replace variables like $variableName with actual values
        return document;
    }
    
    /**
     * Applies conditional logic for field inclusion.
     */
    private Document applyConditionalLogic(Document document) {
        // This is a simplified implementation
        // In a full implementation, you would traverse the document
        // and include/exclude fields based on conditions
        return document;
    }
    
    /**
     * Gets the original document.
     */
    public Document getOriginalDocument() {
        return originalDocument;
    }
    
    /**
     * Gets the transformation context.
     */
    public TransformationContext getContext() {
        return context;
    }
    
    /**
     * Gets the list of pending operations.
     */
    public List<TransformationOperation> getOperations() {
        return Collections.unmodifiableList(operations);
    }
} 