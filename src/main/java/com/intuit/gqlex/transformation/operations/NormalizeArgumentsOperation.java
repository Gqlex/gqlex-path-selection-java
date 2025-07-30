package com.intuit.gqlex.transformation.operations;

import com.intuit.gqlex.transformation.TransformationOperation;
import com.intuit.gqlex.transformation.TransformationContext;
import com.intuit.gqlex.transformation.AstManipulationUtils;
import graphql.language.*;

/**
 * Operation to normalize arguments at a specified path for consistent caching.
 */
public class NormalizeArgumentsOperation implements TransformationOperation {
    
    private final String path;
    
    public NormalizeArgumentsOperation(String path) {
        this.path = path;
    }
    
    @Override
    public Document apply(Document document, TransformationContext context) {
        try {
            Node targetNode = AstManipulationUtils.findNodeAtPath(document, path);
            if (targetNode == null) {
                context.addError("Target node not found at path: " + path);
                return document;
            }
            if (targetNode instanceof Field) {
                AstManipulationUtils.normalizeArguments((Field) targetNode);
            } else {
                context.addError("Target node is not a field: " + targetNode.getClass().getSimpleName());
            }
            return document;
        } catch (Exception e) {
            context.addError("Error normalizing arguments: " + e.getMessage());
            return document;
        }
    }
    
    @Override
    public String getDescription() {
        return String.format("Normalize arguments at path '%s'", path);
    }
    
    @Override
    public OperationType getOperationType() {
        return OperationType.NORMALIZE_ARGUMENTS;
    }
    
    // Note: The actual AST manipulation logic is now handled by AstManipulationUtils
    // This provides a cleaner separation of concerns and reusability
} 