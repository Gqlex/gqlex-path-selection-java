package com.intuit.gqlex.linting.rules;

import com.intuit.gqlex.linting.core.LintContext;
import com.intuit.gqlex.linting.core.LintResult;
import graphql.language.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Identifies potential performance issues and optimization opportunities in GraphQL queries.
 * Generic and agnostic to specific field names or schema structures.
 */
public class PerformanceRule extends LintRule {
    
    public PerformanceRule() {
        super("PERFORMANCE", "Identifies performance optimization opportunities");
    }
    
    @Override
    public void lint(LintContext context, LintResult result) {
        Document document = context.getDocument();
        
        // Validate query depth
        validateQueryDepth(document, context, result);
        
        // Validate field count
        validateFieldCount(document, context, result);
        
        // Validate fragment optimization
        validateFragmentOptimization(document, context, result);
        
        // Validate selection optimization
        validateSelectionOptimization(document, context, result);
        
        // Validate query complexity
        validateQueryComplexity(document, context, result);
        
        // Validate argument count
        validateArgumentCount(document, context, result);
        
        // Validate nested field optimization
        validateNestedFieldOptimization(document, context, result);
        
        // Validate alias usage for performance
        validateAliasUsageForPerformance(document, context, result);
        
        // Validate selection set size
        validateSelectionSetSize(document, context, result);
        
        // Validate operation efficiency
        validateOperationEfficiency(document, context, result);
    }
    
    private void validateQueryDepth(Document document, LintContext context, LintResult result) {
        int maxDepth = context.getConfigValue("maxDepth", Integer.class, 5);
        int actualDepth = context.calculateMaxDepth();
        
        if (actualDepth > maxDepth) {
            addWarning(result, 
                String.format("Query depth (%d) exceeds recommended limit (%d) - consider flattening", 
                    actualDepth, maxDepth), 
                document);
        }
        
        // Check for deep nesting patterns
        if (actualDepth > 3) {
            addInfo(result, 
                String.format("Deep query structure (%d levels) - consider using fragments for better performance", 
                    actualDepth), 
                document);
        }
    }
    
    private void validateFieldCount(Document document, LintContext context, LintResult result) {
        int maxFields = context.getConfigValue("maxFields", Integer.class, 50);
        int actualFields = context.calculateFieldCount();
        
        if (actualFields > maxFields) {
            addWarning(result, 
                String.format("Field count (%d) exceeds recommended limit (%d) - consider using fragments", 
                    actualFields, maxFields), 
                document);
        }
        
        // Check for field count distribution
        Map<Integer, Integer> depthFieldCount = calculateDepthFieldCount(context);
        for (Map.Entry<Integer, Integer> entry : depthFieldCount.entrySet()) {
            if (entry.getValue() > 20) {
                addWarning(result, 
                    String.format("Depth %d has %d fields - consider splitting for better performance", 
                        entry.getKey(), entry.getValue()), 
                    document);
            }
        }
    }
    
    private void validateFragmentOptimization(Document document, LintContext context, LintResult result) {
        boolean enforceFragmentUsage = context.getConfigValue("enforceFragmentUsage", Boolean.class, true);
        
        if (!enforceFragmentUsage) {
            return;
        }
        
        Map<String, Integer> fragmentUsage = calculateFragmentUsage(context);
        
        // Check fragment reuse
        for (Map.Entry<String, Integer> entry : fragmentUsage.entrySet()) {
            if (entry.getValue() > 3) {
                addInfo(result, 
                    String.format("Fragment '%s' used %d times - good for performance", 
                        entry.getKey(), entry.getValue()), 
                    document);
            }
        }
        
        // Check for missing fragment opportunities
        List<SelectionSet> largeSelectionSets = findLargeSelectionSets(context, 10);
        for (SelectionSet selectionSet : largeSelectionSets) {
            if (!hasFragmentUsage(selectionSet)) {
                addInfo(result, 
                    String.format("Large selection set with %d fields - consider using fragments for reusability", 
                        selectionSet.getSelections().size()), 
                    selectionSet);
            }
        }
        
        // Check fragment size
        List<FragmentDefinition> fragments = document.getDefinitions().stream()
            .filter(def -> def instanceof FragmentDefinition)
            .map(def -> (FragmentDefinition) def)
            .collect(Collectors.toList());
            
        for (FragmentDefinition fragment : fragments) {
            int fieldCount = countFieldsInFragment(fragment, context);
            if (fieldCount > 30) {
                addWarning(result, 
                    String.format("Fragment '%s' has %d fields - consider splitting for better performance", 
                        fragment.getName(), fieldCount), 
                    fragment);
            }
        }
    }
    
    private void validateSelectionOptimization(Document document, LintContext context, LintResult result) {
        boolean enforceFieldSelection = context.getConfigValue("enforceFieldSelection", Boolean.class, true);
        
        if (!enforceFieldSelection) {
            return;
        }
        
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
    
    private void validateQueryComplexity(Document document, LintContext context, LintResult result) {
        int maxQueryComplexity = context.getConfigValue("maxQueryComplexity", Integer.class, 50);
        int actualComplexity = calculateQueryComplexity(context);
        
        if (actualComplexity > maxQueryComplexity) {
            addWarning(result, 
                String.format("Query complexity (%d) exceeds recommended limit (%d) - consider optimization", 
                    actualComplexity, maxQueryComplexity), 
                document);
        }
        
        // Check complexity distribution
        Map<Integer, Integer> depthComplexity = calculateDepthComplexity(context);
        for (Map.Entry<Integer, Integer> entry : depthComplexity.entrySet()) {
            if (entry.getValue() > 20) {
                addWarning(result, 
                    String.format("Depth %d has high complexity (%d) - consider optimization", 
                        entry.getKey(), entry.getValue()), 
                    document);
            }
        }
    }
    
    private void validateArgumentCount(Document document, LintContext context, LintResult result) {
        int maxArguments = context.getConfigValue("maxArguments", Integer.class, 10);
        
        context.traverseNodes(node -> {
            if (node instanceof Field) {
                Field field = (Field) node;
                
                if (field.getArguments().size() > maxArguments) {
                    addWarning(result, 
                        String.format("Field '%s' has %d arguments - consider using variables or input types", 
                            field.getName(), field.getArguments().size()), 
                        field);
                }
                
                // Check for complex argument patterns
                for (Argument argument : field.getArguments()) {
                    if (argument.getValue() instanceof ObjectValue) {
                        ObjectValue objValue = (ObjectValue) argument.getValue();
                        if (objValue.getObjectFields().size() > 5) {
                            addInfo(result, 
                                String.format("Argument '%s' has complex object value - consider using variables", 
                                    argument.getName()), 
                                argument);
                        }
                    }
                }
            }
        });
    }
    
    private void validateNestedFieldOptimization(Document document, LintContext context, LintResult result) {
        context.traverseNodes(node -> {
            if (node instanceof Field) {
                Field field = (Field) node;
                
                // Check for deep nesting patterns
                int nestingDepth = calculateNestingDepth(field, context);
                if (nestingDepth > 4) {
                    addWarning(result, 
                        String.format("Field '%s' has deep nesting (%d levels) - consider flattening", 
                            field.getName(), nestingDepth), 
                        field);
                }
                
                // Check for repeated field patterns
                if (hasRepeatedFieldPattern(field)) {
                    addInfo(result, 
                        String.format("Field '%s' has repeated field pattern - consider using fragments", 
                            field.getName()), 
                        field);
                }
            }
        });
    }
    
    private void validateAliasUsageForPerformance(Document document, LintContext context, LintResult result) {
        boolean enforceAliasUsage = context.getConfigValue("enforceAliasUsage", Boolean.class, true);
        
        if (!enforceAliasUsage) {
            return;
        }
        
        context.traverseNodes(node -> {
            if (node instanceof Field) {
                Field field = (Field) node;
                
                // Check for missing aliases in repeated fields
                if (field.getAlias() == null && hasRepeatedFieldName(field, context)) {
                    addInfo(result, 
                        String.format("Field '%s' appears multiple times - consider using aliases for clarity", 
                            field.getName()), 
                        field);
                }
                
                // Check for descriptive aliases
                if (field.getAlias() != null && field.getAlias().length() < 2) {
                    addInfo(result, 
                        String.format("Alias '%s' is very short - consider more descriptive name", 
                            field.getAlias()), 
                        field);
                }
            }
        });
    }
    
    private void validateSelectionSetSize(Document document, LintContext context, LintResult result) {
        int maxSelectionSetSize = context.getConfigValue("maxSelectionSetSize", Integer.class, 15);
        
        context.traverseNodes(node -> {
            if (node instanceof SelectionSet) {
                SelectionSet selectionSet = (SelectionSet) node;
                
                if (selectionSet.getSelections().size() > maxSelectionSetSize) {
                    addWarning(result, 
                        String.format("Large selection set with %d fields - consider using fragments", 
                            selectionSet.getSelections().size()), 
                        selectionSet);
                }
                
                // Check for selection set distribution
                Map<String, Integer> fieldTypeCount = calculateFieldTypeCount(selectionSet);
                for (Map.Entry<String, Integer> entry : fieldTypeCount.entrySet()) {
                    if (entry.getValue() > 10) {
                        addInfo(result, 
                            String.format("Selection set has %d %s fields - consider optimization", 
                                entry.getValue(), entry.getKey()), 
                            selectionSet);
                    }
                }
            }
        });
    }
    
    private void validateOperationEfficiency(Document document, LintContext context, LintResult result) {
        context.traverseNodes(node -> {
            if (node instanceof OperationDefinition) {
                OperationDefinition operation = (OperationDefinition) node;
                
                // Check for multiple operations
                List<OperationDefinition> operations = document.getDefinitions().stream()
                    .filter(def -> def instanceof OperationDefinition)
                    .map(def -> (OperationDefinition) def)
                    .collect(Collectors.toList());
                    
                if (operations.size() > 1) {
                    addInfo(result, 
                        String.format("Document contains %d operations - consider splitting for better performance", 
                            operations.size()), 
                        operation);
                }
                
                // Check operation type efficiency
                if (operation.getOperation() == OperationDefinition.Operation.MUTATION) {
                    // Check for multiple mutations
                    long mutationCount = operations.stream()
                        .filter(op -> op.getOperation() == OperationDefinition.Operation.MUTATION)
                        .count();
                        
                    if (mutationCount > 1) {
                        addWarning(result, 
                            "Multiple mutations in single document - consider splitting for atomicity", 
                            operation);
                    }
                }
            }
        });
    }
    
    private Map<Integer, Integer> calculateDepthFieldCount(LintContext context) {
        Map<Integer, Integer> depthFieldCount = new HashMap<>();
        
        context.traverseNodes(node -> {
            if (node instanceof Field) {
                Field field = (Field) node;
                // Simplified depth calculation
                int depth = 1; // This is a simplified approach
                depthFieldCount.merge(depth, 1, Integer::sum);
            }
        });
        
        return depthFieldCount;
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
    
    private List<SelectionSet> findLargeSelectionSets(LintContext context, int minSize) {
        List<SelectionSet> largeSets = new ArrayList<>();
        
        context.traverseNodes(node -> {
            if (node instanceof SelectionSet) {
                SelectionSet selectionSet = (SelectionSet) node;
                if (selectionSet.getSelections().size() >= minSize) {
                    largeSets.add(selectionSet);
                }
            }
        });
        
        return largeSets;
    }
    
    private boolean hasFragmentUsage(SelectionSet selectionSet) {
        return selectionSet.getSelections().stream()
            .anyMatch(sel -> sel instanceof FragmentSpread);
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
    
    private int calculateQueryComplexity(LintContext context) {
        AtomicInteger complexity = new AtomicInteger(0);
        
        context.traverseNodes(node -> {
            if (node instanceof Field) {
                complexity.incrementAndGet();
                
                // Add complexity for arguments
                Field field = (Field) node;
                complexity.addAndGet(field.getArguments().size());
                
                // Add complexity for directives
                complexity.addAndGet(field.getDirectives().size());
            }
        });
        
        return complexity.get();
    }
    
    private Map<Integer, Integer> calculateDepthComplexity(LintContext context) {
        Map<Integer, Integer> depthComplexity = new HashMap<>();
        
        context.traverseNodes(node -> {
            if (node instanceof Field) {
                Field field = (Field) node;
                int fieldComplexity = 1 + field.getArguments().size() + field.getDirectives().size();
                int depth = 1; // Simplified depth calculation
                depthComplexity.merge(depth, fieldComplexity, Integer::sum);
            }
        });
        
        return depthComplexity;
    }
    
    private int calculateNestingDepth(Field field, LintContext context) {
        AtomicInteger depth = new AtomicInteger(0);
        
        context.traverseNodes(node -> {
            if (node instanceof Field) {
                depth.incrementAndGet();
            }
        });
        
        return depth.get();
    }
    
    private boolean hasRepeatedFieldPattern(Field field) {
        if (field.getSelectionSet() == null) return false;
        
        Set<String> fieldNames = field.getSelectionSet().getSelections().stream()
            .filter(sel -> sel instanceof Field)
            .map(sel -> ((Field) sel).getName())
            .collect(Collectors.toSet());
            
        // Check for repeated patterns
        return fieldNames.size() > 5 && fieldNames.contains("id") && fieldNames.contains("name");
    }
    
    private boolean hasRepeatedFieldName(Field field, LintContext context) {
        AtomicInteger count = new AtomicInteger(0);
        
        context.traverseNodes(node -> {
            if (node instanceof Field) {
                Field nodeField = (Field) node;
                if (nodeField.getName().equals(field.getName())) {
                    count.incrementAndGet();
                }
            }
        });
        
        return count.get() > 1;
    }
    
    private Map<String, Integer> calculateFieldTypeCount(SelectionSet selectionSet) {
        Map<String, Integer> fieldTypeCount = new HashMap<>();
        
        for (Selection selection : selectionSet.getSelections()) {
            if (selection instanceof Field) {
                Field field = (Field) selection;
                String fieldType = field.getSelectionSet() != null ? "object" : "scalar";
                fieldTypeCount.merge(fieldType, 1, Integer::sum);
            }
        }
        
        return fieldTypeCount;
    }
    
    @Override
    public String getCategory() {
        return "PERFORMANCE";
    }
} 