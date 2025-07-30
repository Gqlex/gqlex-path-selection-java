package com.intuit.gqlex.transformation.operations;

import com.intuit.gqlex.transformation.TransformationOperation;
import com.intuit.gqlex.transformation.TransformationContext;
import com.intuit.gqlex.transformation.AstManipulationUtils;
import graphql.language.*;
import graphql.parser.Parser;
import graphql.language.AstPrinter;

/**
 * Operation to inline all fragments in a GraphQL document.
 * This operation replaces all fragment spreads with their actual content
 * and removes fragment definitions from the query.
 * 
 * Features:
 * - Generic and agnostic to any GraphQL schema
 * - Handles nested fragments
 * - Preserves query structure
 * - Error-safe with fallback to original query
 * 
 * @author gqlex-team
 * @version 1.0.0
 */
public class InlineFragmentsOperation implements TransformationOperation {
    
    @Override
    public Document apply(Document document, TransformationContext context) {
        try {
            // Convert document to string for string-based manipulation
            String queryString = AstPrinter.printAst(document);
            
            // Inline fragments using string manipulation
            String inlinedQueryString = AstManipulationUtils.inlineFragmentsInQueryString(queryString);
            
            // Parse back to document
            Document inlinedDocument = Parser.parse(inlinedQueryString);
            
            // Add success message to context
            context.setMetadata("inlineFragments", "Successfully inlined all fragments in the document");
            
            return inlinedDocument;
        } catch (Exception e) {
            context.addError("Error inlining fragments: " + e.getMessage());
            return document; // Return original document on error
        }
    }
    
    @Override
    public String getDescription() {
        return "Inline all fragments in the document by replacing fragment spreads with their content";
    }
    
    @Override
    public OperationType getOperationType() {
        return OperationType.INLINE_FRAGMENTS;
    }
} 