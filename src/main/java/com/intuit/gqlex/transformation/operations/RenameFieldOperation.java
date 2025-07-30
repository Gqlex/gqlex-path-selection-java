package com.intuit.gqlex.transformation.operations;

import com.intuit.gqlex.transformation.TransformationOperation;
import com.intuit.gqlex.transformation.TransformationContext;
import com.intuit.gqlex.transformation.AstManipulationUtils;
import graphql.language.*;

/**
 * Operation to rename a field in a GraphQL query at a specified path.
 */
public class RenameFieldOperation implements TransformationOperation {
    
    private final String path;
    private final String newName;
    
    public RenameFieldOperation(String path, String newName) {
        this.path = path;
        this.newName = newName;
    }
    
    @Override
    public Document apply(Document document, TransformationContext context) {
        try {
            // Use the new AST manipulation method that returns a modified document
            return AstManipulationUtils.renameField(document, path, newName);
            
        } catch (Exception e) {
            context.addError("Error renaming field: " + e.getMessage());
            return document;
        }
    }
    
    @Override
    public String getDescription() {
        return String.format("Rename field to '%s' at path '%s'", newName, path);
    }
    
    @Override
    public OperationType getOperationType() {
        return OperationType.RENAME_FIELD;
    }
    
    // Getter methods for accessing operation data
    public String getPath() {
        return path;
    }
    
    public String getNewName() {
        return newName;
    }
    
    // Note: The actual AST manipulation logic is now handled by AstManipulationUtils
    // This provides a cleaner separation of concerns and reusability
}
