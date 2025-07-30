package com.intuit.gqlex.transformation;

import graphql.language.Document;
import graphql.language.AstPrinter;

import java.util.Collections;
import java.util.List;

/**
 * Encapsulates the result of a GraphQL transformation operation.
 */
public class TransformationResult {
    
    private final Document document;
    private final boolean success;
    private final List<String> errors;
    private final String queryString;
    private final List<TransformationOperation> appliedOperations;
    
    public TransformationResult(Document document, boolean success, List<String> errors) {
        this(document, success, errors, Collections.emptyList());
    }
    
    public TransformationResult(Document document, boolean success, List<String> errors, List<TransformationOperation> appliedOperations) {
        this.document = document;
        this.success = success;
        this.errors = errors != null ? errors : Collections.emptyList();
        this.appliedOperations = appliedOperations != null ? appliedOperations : Collections.emptyList();
        this.queryString = success ? printDocument(document) : null;
    }
    
    /**
     * Returns the transformed GraphQL document.
     */
    public Document getDocument() {
        return document;
    }
    
    /**
     * Returns true if the transformation was successful.
     */
    public boolean isSuccess() {
        return success;
    }
    
    /**
     * Returns true if the transformation failed.
     */
    public boolean isFailure() {
        return !success;
    }
    
    /**
     * Returns a list of error messages if the transformation failed.
     */
    public List<String> getErrors() {
        return errors;
    }
    
    /**
     * Returns the transformed GraphQL query as a string.
     */
    public String getQueryString() {
        return queryString;
    }
    
    /**
     * Returns the list of operations that were applied during the transformation.
     */
    public List<TransformationOperation> getAppliedOperations() {
        return appliedOperations;
    }
    
    /**
     * Prints the document using AstPrinter.
     */
    private String printDocument(Document document) {
        try {
            return AstPrinter.printAst(document);
        } catch (Exception e) {
            return "Error printing document: " + e.getMessage();
        }
    }
} 