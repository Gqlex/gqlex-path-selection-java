package com.intuit.gqlex.transformation.operations;

import com.intuit.gqlex.transformation.TransformationOperation;
import com.intuit.gqlex.transformation.TransformationContext;
import com.intuit.gqlex.transformation.AstManipulationUtils;
import graphql.language.*;

/**
 * Operation to set an alias on a GraphQL field at a specified path.
 */
public class SetAliasOperation implements TransformationOperation {
    
    private final String path;
    private final String alias;
    
    public SetAliasOperation(String path, String alias) {
        this.path = path;
        this.alias = alias;
    }
    
    @Override
    public Document apply(Document document, TransformationContext context) {
        try {
            // Use the new AST manipulation method that returns a modified document
            return AstManipulationUtils.setAlias(document, path, alias);
        } catch (Exception e) {
            context.addError("Error setting alias: " + e.getMessage());
            return document;
        }
    }
    
    @Override
    public String getDescription() {
        return String.format("Set alias '%s' at path '%s'", alias, path);
    }
    
    @Override
    public OperationType getOperationType() {
        return OperationType.SET_ALIAS;
    }
    
    // Getter methods for accessing operation data
    public String getPath() {
        return path;
    }
    
    public String getAlias() {
        return alias;
    }
    
    // Note: The actual AST manipulation logic is now handled by AstManipulationUtils
    // This provides a cleaner separation of concerns and reusability
}
