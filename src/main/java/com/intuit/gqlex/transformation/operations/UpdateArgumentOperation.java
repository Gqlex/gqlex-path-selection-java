package com.intuit.gqlex.transformation.operations;

import com.intuit.gqlex.transformation.TransformationOperation;
import com.intuit.gqlex.transformation.TransformationContext;
import com.intuit.gqlex.transformation.AstManipulationUtils;
import graphql.language.*;

/**
 * Operation to update an argument in a GraphQL field at a specified path.
 */
public class UpdateArgumentOperation implements TransformationOperation {
    
    private final String path;
    private final String argumentName;
    private final Object value;
    
    public UpdateArgumentOperation(String path, String argumentName, Object value) {
        this.path = path;
        this.argumentName = argumentName;
        this.value = value;
    }
    
    @Override
    public Document apply(Document document, TransformationContext context) {
        try {
            // Use the new AST manipulation method that returns a modified document
            return AstManipulationUtils.updateArgument(document, path, argumentName, value);
        } catch (Exception e) {
            context.addError("Error updating argument: " + e.getMessage());
            return document;
        }
    }
    
    @Override
    public String getDescription() {
        return String.format("Update argument '%s' with value '%s' at path '%s'", argumentName, value, path);
    }
    
    @Override
    public OperationType getOperationType() {
        return OperationType.UPDATE_ARGUMENT;
    }
    
    // Getter methods for accessing operation data
    public String getPath() {
        return path;
    }
    
    public String getArgumentName() {
        return argumentName;
    }
    
    public Object getValue() {
        return value;
    }
    
    // Note: The actual AST manipulation logic is now handled by AstManipulationUtils
    // This provides a cleaner separation of concerns and reusability
}
