package com.intuit.gqlex.validation.rules;

import com.intuit.gqlex.validation.core.ValidationContext;
import com.intuit.gqlex.validation.core.ValidationResult;
import com.intuit.gqlex.validation.core.ValidationLevel;
import graphql.language.Document;
import graphql.language.Field;
import graphql.language.Argument;
import graphql.language.OperationDefinition;
import graphql.language.FragmentDefinition;

/**
 * Validates the basic structural integrity of GraphQL documents.
 * This rule is completely generic and works with any GraphQL document structure.
 */
public class StructuralRule extends ValidationRule {
    
    /**
     * Creates a structural validation rule.
     */
    public StructuralRule() {
        super("STRUCTURAL", ValidationLevel.ERROR, 
            "Validates basic GraphQL document structure and syntax");
    }
    
    @Override
    public String getCategory() {
        return "STRUCTURAL";
    }
    
    @Override
    public void validate(ValidationContext context, ValidationResult result) {
        Document document = context.getDocument();
        
        if (document == null) {
            result.addError(getName(), "Document cannot be null");
            return;
        }
        
        // Generic structural validation - works with any GraphQL document
        validateDocumentStructure(document, result);
        validateFieldStructure(document, result);
        validateArgumentStructure(document, result);
        validateFragmentStructure(document, result);
    }
    
    /**
     * Validates basic document structure.
     * This method is generic and works with any GraphQL document.
     * 
     * @param document the document to validate
     * @param result the validation result
     */
    private void validateDocumentStructure(Document document, ValidationResult result) {
        // Generic document validation - no field-specific logic
        if (document.getDefinitions().isEmpty()) {
            result.addError(getName(), "Document must contain at least one definition", document);
        }
        
        // Generic operation validation
        long operationCount = document.getDefinitions().stream()
            .filter(def -> def instanceof OperationDefinition)
            .count();
            
        if (operationCount == 0) {
            result.addError(getName(), "Document must contain at least one operation", document);
        }
    }
    
    /**
     * Validates field structure.
     * This method is generic and works with any field names.
     * 
     * @param document the document to validate
     * @param result the validation result
     */
    private void validateFieldStructure(Document document, ValidationResult result) {
        // Generic field validation - works with any field names
        ValidationContext context = new ValidationContext(document);
        context.traverseNodes(node -> {
            if (node instanceof Field) {
                Field field = (Field) node;
                validateField(field, result);
            }
        });
    }
    
    /**
     * Validates a single field.
     * This method is generic and works with any field names.
     * 
     * @param field the field to validate
     * @param result the validation result
     */
    private void validateField(Field field, ValidationResult result) {
        // Generic field validation - no hardcoded field names
        if (field.getName() == null || field.getName().isEmpty()) {
            result.addError(getName(), "Field name cannot be empty", field);
        }
        
        // Generic argument validation
        if (field.getArguments() != null) {
            for (Argument arg : field.getArguments()) {
                validateArgument(arg, result);
            }
        }
    }
    
    /**
     * Validates argument structure.
     * This method is generic and works with any argument names.
     * 
     * @param document the document to validate
     * @param result the validation result
     */
    private void validateArgumentStructure(Document document, ValidationResult result) {
        // Generic argument validation - works with any argument names
        ValidationContext context = new ValidationContext(document);
        context.traverseNodes(node -> {
            if (node instanceof Argument) {
                Argument argument = (Argument) node;
                validateArgument(argument, result);
            }
        });
    }
    
    /**
     * Validates a single argument.
     * This method is generic and works with any argument names.
     * 
     * @param argument the argument to validate
     * @param result the validation result
     */
    private void validateArgument(Argument argument, ValidationResult result) {
        // Generic argument validation - no hardcoded argument names
        if (argument.getName() == null || argument.getName().isEmpty()) {
            result.addError(getName(), "Argument name cannot be empty", argument);
        }
        
        if (argument.getValue() == null) {
            result.addError(getName(), "Argument value cannot be null", argument);
        }
    }
    
    /**
     * Validates fragment structure.
     * This method is generic and works with any fragment names.
     * 
     * @param document the document to validate
     * @param result the validation result
     */
    private void validateFragmentStructure(Document document, ValidationResult result) {
        // Generic fragment validation - works with any fragment names
        ValidationContext context = new ValidationContext(document);
        context.traverseNodes(node -> {
            if (node instanceof FragmentDefinition) {
                FragmentDefinition fragment = (FragmentDefinition) node;
                validateFragment(fragment, result);
            }
        });
    }
    
    /**
     * Validates a single fragment.
     * This method is generic and works with any fragment names.
     * 
     * @param fragment the fragment to validate
     * @param result the validation result
     */
    private void validateFragment(FragmentDefinition fragment, ValidationResult result) {
        // Generic fragment validation - no hardcoded fragment names
        if (fragment.getName() == null || fragment.getName().isEmpty()) {
            result.addError(getName(), "Fragment name cannot be empty", fragment);
        }
        
        if (fragment.getTypeCondition() == null) {
            result.addError(getName(), "Fragment must have a type condition", fragment);
        }
        
        if (fragment.getSelectionSet() == null) {
            result.addError(getName(), "Fragment must have a selection set", fragment);
        }
    }
} 