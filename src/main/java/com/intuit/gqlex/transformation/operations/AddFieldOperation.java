package com.intuit.gqlex.transformation.operations;

import com.intuit.gqlex.transformation.TransformationOperation;
import com.intuit.gqlex.transformation.TransformationContext;
import com.intuit.gqlex.transformation.AstManipulationUtils;
import graphql.language.*;

import java.util.*;

/**
 * Operation to add a field to a GraphQL query at a specified path.
 */
public class AddFieldOperation implements TransformationOperation {
    
    private final String path;
    private final String fieldName;
    private final Map<String, Object> arguments;
    
    public AddFieldOperation(String path, String fieldName, Map<String, Object> arguments) {
        this.path = path;
        this.fieldName = fieldName;
        this.arguments = arguments != null ? arguments : Collections.emptyMap();
    }
    
    @Override
    public Document apply(Document document, TransformationContext context) {
        try {
            // Use the new AST manipulation method that returns a modified document
            return AstManipulationUtils.addField(document, path, fieldName);
        } catch (Exception e) {
            context.addError("Error adding field: " + e.getMessage());
            return document;
        }
    }
    
    @Override
    public String getDescription() {
        return String.format("Add field '%s' at path '%s' with arguments: %s", fieldName, path, arguments);
    }
    
    @Override
    public OperationType getOperationType() {
        return OperationType.ADD_FIELD;
    }
    
    // Getter methods for accessing operation data
    public String getPath() {
        return path;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public Map<String, Object> getArguments() {
        return arguments;
    }
    
    // Note: The actual AST manipulation logic is now handled by AstManipulationUtils
    // This provides a cleaner separation of concerns and reusability
} 