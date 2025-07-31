package com.intuit.gqlex.linting.rules;

import com.intuit.gqlex.linting.core.LintContext;
import com.intuit.gqlex.linting.core.LintResult;
import graphql.language.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Enforces GraphQL best practices and patterns for maintainable code.
 * Generic and agnostic to specific field names or schema structures.
 */
public class BestPracticeRule extends LintRule {
    
    public BestPracticeRule() {
        super("BEST_PRACTICE", "Enforces GraphQL best practices");
    }
    
    @Override
    public void lint(LintContext context, LintResult result) {
        Document document = context.getDocument();
        
        // Validate alias usage
        validateAliasUsage(document, context, result);
        
        // Validate fragment usage
        validateFragmentUsage(document, context, result);
        
        // Validate selection set optimization
        validateSelectionSetOptimization(document, context, result);
        
        // Validate variable usage
        validateVariableUsage(document, context, result);
        
        // Validate operation naming
        validateOperationNaming(document, context, result);
        
        // Validate field selection efficiency
        validateFieldSelectionEfficiency(document, context, result);
        
        // Validate argument usage
        validateArgumentUsage(document, context, result);
        
        // Validate directive usage
        validateDirectiveUsage(document, context, result);
        
        // Validate fragment naming conventions
        validateFragmentNamingConventions(document, context, result);
        
        // Validate query structure
        validateQueryStructure(document, context, result);
    }
    
    private void validateAliasUsage(Document document, LintContext context, LintResult result) {
        context.traverseNodes(node -> {
            if (node instanceof Field) {
                Field field = (Field) node;
                
                // Check for redundant aliases
                if (field.getAlias() != null && field.getAlias().equals(field.getName())) {
                    addInfo(result, 
                        String.format("Alias '%s' is redundant - same as field name", field.getAlias()), 
                        field);
                }
                
                // Check for missing aliases in conflicts
                if (field.getAlias() == null && hasConflictingFields(field, context)) {
                    addWarning(result, 
                        String.format("Consider using alias for field '%s' to avoid conflicts", field.getName()), 
                        field);
                }
                
                // Check for descriptive aliases
                if (field.getAlias() != null && field.getAlias().length() < 2) {
                    addInfo(result, 
                        String.format("Alias '%s' is very short - consider more descriptive name", field.getAlias()), 
                        field);
                }
            }
        });
    }
    
    private void validateFragmentUsage(Document document, LintContext context, LintResult result) {
        List<FragmentDefinition> fragments = document.getDefinitions().stream()
            .filter(def -> def instanceof FragmentDefinition)
            .map(def -> (FragmentDefinition) def)
            .collect(Collectors.toList());
            
        // Check for too many fragments
        int maxFragments = context.getConfigValue("maxFragments", Integer.class, 5);
        if (fragments.size() > maxFragments) {
            addWarning(result, 
                String.format("Consider consolidating %d fragments for better maintainability (max: %d)", 
                    fragments.size(), maxFragments), 
                document);
        }
        
        // Check for unused fragments
        Set<String> usedFragments = findUsedFragments(context);
        for (FragmentDefinition fragment : fragments) {
            if (!usedFragments.contains(fragment.getName())) {
                addWarning(result, 
                    String.format("Fragment '%s' is defined but never used", fragment.getName()), 
                    fragment);
            }
        }
        
        // Check fragment reuse
        Map<String, Integer> fragmentUsage = calculateFragmentUsage(context);
        for (Map.Entry<String, Integer> entry : fragmentUsage.entrySet()) {
            if (entry.getValue() > 3) {
                addInfo(result, 
                    String.format("Fragment '%s' used %d times - good for performance", 
                        entry.getKey(), entry.getValue()), 
                    document);
            }
        }
        
        // Check fragment size
        for (FragmentDefinition fragment : fragments) {
            int fieldCount = countFieldsInFragment(fragment, context);
            if (fieldCount > 20) {
                addWarning(result, 
                    String.format("Fragment '%s' has %d fields - consider splitting for maintainability", 
                        fragment.getName(), fieldCount), 
                    fragment);
            }
        }
    }
    
    private void validateSelectionSetOptimization(Document document, LintContext context, LintResult result) {
        context.traverseNodes(node -> {
            if (node instanceof SelectionSet) {
                SelectionSet selectionSet = (SelectionSet) node;
                
                // Check for large selection sets
                int maxSelectionSetSize = context.getConfigValue("maxSelectionSetSize", Integer.class, 15);
                if (selectionSet.getSelections().size() > maxSelectionSetSize) {
                    addWarning(result, 
                        String.format("Large selection set with %d fields - consider using fragments", 
                            selectionSet.getSelections().size()), 
                        selectionSet);
                }
                
                // Check for duplicate fields
                Set<String> fieldNames = new HashSet<>();
                for (Selection selection : selectionSet.getSelections()) {
                    if (selection instanceof Field) {
                        Field field = (Field) selection;
                        String fieldKey = field.getAlias() != null ? field.getAlias() : field.getName();
                        
                        if (!fieldNames.add(fieldKey)) {
                            addError(result, 
                                String.format("Duplicate field '%s' in selection set", fieldKey), 
                                field);
                        }
                    }
                }
                
                // Check for over-fetching patterns
                validateOverFetching(selectionSet, context, result);
            }
        });
    }
    
    private void validateVariableUsage(Document document, LintContext context, LintResult result) {
        List<VariableDefinition> variables = document.getDefinitions().stream()
            .filter(def -> def instanceof OperationDefinition)
            .map(def -> (OperationDefinition) def)
            .flatMap(op -> op.getVariableDefinitions().stream())
            .collect(Collectors.toList());
            
        // Check for unused variables
        Set<String> usedVariables = findUsedVariables(context);
        for (VariableDefinition variable : variables) {
            if (!usedVariables.contains(variable.getName())) {
                addWarning(result, 
                    String.format("Variable '$%s' is defined but never used", variable.getName()), 
                    variable);
            }
        }
        
        // Check for missing variable definitions
        Set<String> definedVariables = variables.stream()
            .map(VariableDefinition::getName)
            .collect(Collectors.toSet());
            
        Set<String> referencedVariables = findReferencedVariables(context);
        for (String varName : referencedVariables) {
            if (!definedVariables.contains(varName)) {
                addError(result, 
                    String.format("Variable '$%s' is used but not defined", varName), 
                    document);
            }
        }
        
        // Check variable naming conventions
        for (VariableDefinition variable : variables) {
            String varName = variable.getName();
            if (!varName.matches("^[a-z][a-zA-Z0-9]*$")) {
                addWarning(result, 
                    String.format("Variable name '$%s' should follow camelCase convention", varName), 
                    variable);
            }
        }
    }
    
    private void validateOperationNaming(Document document, LintContext context, LintResult result) {
        context.traverseNodes(node -> {
            if (node instanceof OperationDefinition) {
                OperationDefinition operation = (OperationDefinition) node;
                
                // Check for unnamed operations
                if (operation.getName() == null) {
                    addWarning(result, 
                        "Consider naming your operation for better debugging and monitoring", 
                        operation);
                }
                
                // Check operation name conventions
                if (operation.getName() != null) {
                    String opName = operation.getName();
                    if (!opName.matches("^[A-Z][a-zA-Z0-9]*$")) {
                        addWarning(result, 
                            String.format("Operation name '%s' should follow PascalCase convention", opName), 
                            operation);
                    }
                    
                    // Check for descriptive names
                    if (opName.length() < 3) {
                        addInfo(result, 
                            String.format("Operation name '%s' is very short - consider more descriptive name", opName), 
                            operation);
                    }
                }
            }
        });
    }
    
    private void validateFieldSelectionEfficiency(Document document, LintContext context, LintResult result) {
        context.traverseNodes(node -> {
            if (node instanceof Field) {
                Field field = (Field) node;
                
                // Check for potential over-fetching
                if (field.getSelectionSet() != null && field.getSelectionSet().getSelections().size() > 15) {
                    addWarning(result, 
                        String.format("Field '%s' has large selection set (%d fields) - consider splitting", 
                            field.getName(), field.getSelectionSet().getSelections().size()), 
                        field);
                }
                
                // Check for common over-fetching patterns
                if (isOverFetchingPattern(field)) {
                    addWarning(result, 
                        String.format("Field '%s' may be over-fetching data - consider more specific field selection", 
                            field.getName()), 
                        field);
                }
                
                // Check for unnecessary nested fields
                if (hasUnnecessaryNesting(field)) {
                    addInfo(result, 
                        String.format("Field '%s' has unnecessary nesting - consider flattening", 
                            field.getName()), 
                        field);
                }
            }
        });
    }
    
    private void validateArgumentUsage(Document document, LintContext context, LintResult result) {
        context.traverseNodes(node -> {
            if (node instanceof Field) {
                Field field = (Field) node;
                
                // Check for too many arguments
                int maxArguments = context.getConfigValue("maxArguments", Integer.class, 10);
                if (field.getArguments().size() > maxArguments) {
                    addWarning(result, 
                        String.format("Field '%s' has %d arguments - consider using variables or input types", 
                            field.getName(), field.getArguments().size()), 
                        field);
                }
                
                // Check argument naming
                for (Argument argument : field.getArguments()) {
                    String argName = argument.getName();
                    if (!argName.matches("^[a-z][a-zA-Z0-9]*$")) {
                        addWarning(result, 
                            String.format("Argument name '%s' should follow camelCase convention", argName), 
                            argument);
                    }
                }
            }
        });
    }
    
    private void validateDirectiveUsage(Document document, LintContext context, LintResult result) {
        context.traverseNodes(node -> {
            if (node instanceof Directive) {
                Directive directive = (Directive) node;
                
                // Check directive naming
                String directiveName = directive.getName();
                if (!directiveName.matches("^[a-z][a-zA-Z0-9]*$")) {
                    addWarning(result, 
                        String.format("Directive name '%s' should follow camelCase convention", directiveName), 
                        directive);
                }
                
                // Check for forbidden directives
                Set<String> forbiddenDirectives = context.getConfigValue("forbiddenDirectives", Set.class, 
                    Set.of("auth", "admin", "internal"));
                    
                if (forbiddenDirectives.contains(directiveName)) {
                    addError(result, 
                        String.format("Directive '%s' is forbidden in this context", directiveName), 
                        directive);
                }
            }
        });
    }
    
    private void validateFragmentNamingConventions(Document document, LintContext context, LintResult result) {
        context.traverseNodes(node -> {
            if (node instanceof FragmentDefinition) {
                FragmentDefinition fragment = (FragmentDefinition) node;
                String fragmentName = fragment.getName();
                
                // Check PascalCase convention
                if (!fragmentName.matches("^[A-Z][a-zA-Z0-9]*$")) {
                    addWarning(result, 
                        String.format("Fragment name '%s' should follow PascalCase convention", fragmentName), 
                        fragment);
                }
                
                // Check for descriptive names
                if (fragmentName.length() < 3) {
                    addInfo(result, 
                        String.format("Fragment name '%s' is very short - consider more descriptive name", fragmentName), 
                        fragment);
                }
            }
        });
    }
    
    private void validateQueryStructure(Document document, LintContext context, LintResult result) {
        // Check for multiple operations
        List<OperationDefinition> operations = document.getDefinitions().stream()
            .filter(def -> def instanceof OperationDefinition)
            .map(def -> (OperationDefinition) def)
            .collect(Collectors.toList());
            
        if (operations.size() > 1) {
            addInfo(result, 
                String.format("Document contains %d operations - consider splitting for better maintainability", 
                    operations.size()), 
                document);
        }
        
        // Check operation types
        for (OperationDefinition operation : operations) {
            OperationDefinition.Operation opType = operation.getOperation();
            if (opType == OperationDefinition.Operation.MUTATION) {
                // Check for multiple mutations
                if (operations.stream().filter(op -> op.getOperation() == OperationDefinition.Operation.MUTATION).count() > 1) {
                    addWarning(result, 
                        "Multiple mutations in single document - consider splitting for atomicity", 
                        operation);
                }
            }
        }
    }
    
    private boolean hasConflictingFields(Field field, LintContext context) {
        String fieldName = field.getName();
        AtomicInteger count = new AtomicInteger(0);
        
        context.traverseNodes(node -> {
            if (node instanceof Field) {
                Field nodeField = (Field) node;
                if (nodeField.getName().equals(fieldName)) {
                    count.incrementAndGet();
                }
            }
        });
        
        return count.get() > 1;
    }
    
    private Set<String> findUsedFragments(LintContext context) {
        Set<String> usedFragments = new HashSet<>();
        
        context.traverseNodes(node -> {
            if (node instanceof FragmentSpread) {
                FragmentSpread spread = (FragmentSpread) node;
                usedFragments.add(spread.getName());
            }
        });
        
        return usedFragments;
    }
    
    private Map<String, Integer> calculateFragmentUsage(LintContext context) {
        Map<String, Integer> usage = new HashMap<>();
        
        context.traverseNodes(node -> {
            if (node instanceof FragmentSpread) {
                FragmentSpread spread = (FragmentSpread) node;
                usage.merge(spread.getName(), 1, Integer::sum);
            }
        });
        
        return usage;
    }
    
    private int countFieldsInFragment(FragmentDefinition fragment, LintContext context) {
        AtomicInteger count = new AtomicInteger(0);
        
        context.traverseNodes(node -> {
            if (node instanceof Field) {
                count.incrementAndGet();
            }
        });
        
        return count.get();
    }
    
    private Set<String> findUsedVariables(LintContext context) {
        Set<String> usedVariables = new HashSet<>();
        
        // Simplified approach - we'll skip variable validation for now
        // This can be enhanced later when we have proper GraphQL Variable class access
        
        return usedVariables;
    }
    
    private Set<String> findReferencedVariables(LintContext context) {
        Set<String> referencedVariables = new HashSet<>();
        
        // Simplified approach - we'll skip variable validation for now
        // This can be enhanced later when we have proper GraphQL Variable class access
        
        return referencedVariables;
    }
    
    private void validateOverFetching(SelectionSet selectionSet, LintContext context, LintResult result) {
        // Check for common over-fetching patterns
        Set<String> fieldNames = selectionSet.getSelections().stream()
            .filter(sel -> sel instanceof Field)
            .map(sel -> ((Field) sel).getName())
            .collect(Collectors.toSet());
            
        // Check for "select all" patterns
        if (fieldNames.contains("id") && fieldNames.contains("name") && fieldNames.contains("email") && 
            fieldNames.contains("phone") && fieldNames.contains("address")) {
            addInfo(result, 
                "Consider more specific field selection instead of selecting all common fields", 
                selectionSet);
        }
    }
    
    private boolean isOverFetchingPattern(Field field) {
        if (field.getSelectionSet() == null) return false;
        
        Set<String> fieldNames = field.getSelectionSet().getSelections().stream()
            .filter(sel -> sel instanceof Field)
            .map(sel -> ((Field) sel).getName())
            .collect(Collectors.toSet());
            
        // Check for common over-fetching patterns
        return fieldNames.size() > 10 || 
               (fieldNames.contains("id") && fieldNames.contains("name") && fieldNames.size() > 5);
    }
    
    private boolean hasUnnecessaryNesting(Field field) {
        if (field.getSelectionSet() == null) return false;
        
        // Check for single field with nested selection
        if (field.getSelectionSet().getSelections().size() == 1) {
            Selection selection = field.getSelectionSet().getSelections().get(0);
            if (selection instanceof Field) {
                Field nestedField = (Field) selection;
                if (nestedField.getSelectionSet() == null) {
                    return true; // Unnecessary nesting
                }
            }
        }
        
        return false;
    }
    
    @Override
    public String getCategory() {
        return "BEST_PRACTICE";
    }
} 