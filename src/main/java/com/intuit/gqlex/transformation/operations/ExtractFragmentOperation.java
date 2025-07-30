package com.intuit.gqlex.transformation.operations;

import com.intuit.gqlex.transformation.TransformationOperation;
import com.intuit.gqlex.transformation.TransformationContext;
import com.intuit.gqlex.transformation.AstManipulationUtils;
import graphql.language.*;
import graphql.parser.Parser;
import graphql.language.AstPrinter;

/**
 * Operation to extract a fragment from a specified path in a GraphQL document.
 * This operation takes a selection set at a specified path and converts it into
 * a reusable fragment definition.
 * 
 * Features:
 * - Generic and agnostic to any GraphQL schema
 * - Extracts selection sets from any field
 * - Creates reusable fragment definitions
 * - Preserves query structure
 * - Error-safe with fallback to original query
 * 
 * @author gqlex-team
 * @version 1.0.0
 */
public class ExtractFragmentOperation implements TransformationOperation {
    
    private final String path;
    private final String fragmentName;
    private final String typeCondition;
    
    /**
     * Creates a new ExtractFragmentOperation.
     * 
     * @param path The path to the field whose selection set should be extracted
     * @param fragmentName The name for the new fragment
     * @param typeCondition The type condition for the fragment (e.g., "User", "Post")
     */
    public ExtractFragmentOperation(String path, String fragmentName, String typeCondition) {
        this.path = path;
        this.fragmentName = fragmentName;
        this.typeCondition = typeCondition;
    }
    
    @Override
    public Document apply(Document document, TransformationContext context) {
        try {
            // Convert document to string for string-based manipulation
            String queryString = AstPrinter.printAst(document);
            
            // Extract fragment using string manipulation
            String extractedQueryString = AstManipulationUtils.extractFragmentFromQueryString(
                queryString, path, fragmentName, typeCondition);
            

            
            // Parse back to document
            Document extractedDocument = Parser.parse(extractedQueryString);
            
            // Add success message to context
            context.setMetadata("extractFragment", 
                String.format("Successfully extracted fragment '%s' from path '%s'", fragmentName, path));
            
            return extractedDocument;
        } catch (Exception e) {
            context.addError("Error extracting fragment: " + e.getMessage());
            return document; // Return original document on error
        }
    }
    
    @Override
    public String getDescription() {
        return String.format("Extract fragment '%s' with type condition '%s' from path '%s'", 
            fragmentName, typeCondition, path);
    }
    
    @Override
    public OperationType getOperationType() {
        return OperationType.EXTRACT_FRAGMENT;
    }
    
    /**
     * Gets the path where the fragment will be extracted from.
     */
    public String getPath() {
        return path;
    }
    
    /**
     * Gets the name of the fragment to be created.
     */
    public String getFragmentName() {
        return fragmentName;
    }
    
    /**
     * Gets the type condition for the fragment.
     */
    public String getTypeCondition() {
        return typeCondition;
    }
} 