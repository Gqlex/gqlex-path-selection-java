package com.intuit.gqlex.transformation.operations;

import com.intuit.gqlex.transformation.TransformationOperation;
import com.intuit.gqlex.transformation.TransformationContext;
import com.intuit.gqlex.transformation.AstManipulationUtils;
import graphql.language.*;

/**
 * Operation to remove a field from a GraphQL query at a specified path.
 */
public class RemoveFieldOperation implements TransformationOperation {
    
    private final String path;
    
    public RemoveFieldOperation(String path) {
        this.path = path;
    }
    
    @Override
    public Document apply(Document document, TransformationContext context) {
        try {
            // Use the new AST manipulation method that returns a modified document
            return AstManipulationUtils.removeField(document, path);
        } catch (Exception e) {
            context.addError("Error removing field: " + e.getMessage());
            return document;
        }
    }
    
    @Override
    public String getDescription() {
        return String.format("Remove field at path '%s'", path);
    }
    
    @Override
    public OperationType getOperationType() {
        return OperationType.REMOVE_FIELD;
    }
    
    // Getter methods for accessing operation data
    public String getPath() {
        return path;
    }
    
    // Note: The actual AST manipulation logic is now handled by AstManipulationUtils
    // This provides a cleaner separation of concerns and reusability
}
