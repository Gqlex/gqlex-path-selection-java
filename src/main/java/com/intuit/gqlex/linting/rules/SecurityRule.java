package com.intuit.gqlex.linting.rules;

import com.intuit.gqlex.linting.core.LintContext;
import com.intuit.gqlex.linting.core.LintResult;
import graphql.language.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * Identifies potential security vulnerabilities and risks in GraphQL queries.
 * Generic and agnostic to specific field names or schema structures.
 */
public class SecurityRule extends LintRule {
    
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "(?i)(SELECT|INSERT|UPDATE|DELETE|DROP|CREATE|ALTER|EXEC|UNION|OR|AND|'|;|--|/\\*|\\*/)");
    private static final Pattern XSS_PATTERN = Pattern.compile(
        "(?i)(<script|javascript:|onload=|onerror=|onclick=|<iframe|<img|<svg)");
    private static final Pattern PATH_TRAVERSAL_PATTERN = Pattern.compile(
        "(?i)(\\.\\./|\\.\\.\\\\|%2e%2e|%2e%2e%2f|%2e%2e%5c)");
    
    public SecurityRule() {
        super("SECURITY", "Identifies security vulnerabilities and risks");
    }
    
    @Override
    public void lint(LintContext context, LintResult result) {
        Document document = context.getDocument();
        
        // Validate introspection usage
        validateIntrospectionUsage(document, context, result);
        
        // Validate sensitive field access
        validateSensitiveFieldAccess(document, context, result);
        
        // Validate argument validation
        validateArgumentValidation(document, context, result);
        
        // Validate depth limits
        validateDepthLimits(document, context, result);
        
        // Validate query complexity
        validateQueryComplexity(document, context, result);
        
        // Validate input sanitization
        validateInputSanitization(document, context, result);
        
        // Validate directive security
        validateDirectiveSecurity(document, context, result);
        
        // Validate fragment security
        validateFragmentSecurity(document, context, result);
        
        // Validate operation security
        validateOperationSecurity(document, context, result);
        
        // Validate access control patterns
        validateAccessControlPatterns(document, context, result);
    }
    
    private void validateIntrospectionUsage(Document document, LintContext context, LintResult result) {
        boolean forbiddenIntrospection = context.getConfigValue("forbiddenIntrospection", Boolean.class, true);
        
        if (!forbiddenIntrospection) {
            return;
        }
        
        context.traverseNodes(node -> {
            if (node instanceof Field) {
                Field field = (Field) node;
                String fieldName = field.getName();
                
                // Check for introspection fields
                if (fieldName.startsWith("__")) {
                    addWarning(result, 
                        String.format("Introspection field '%s' detected - consider restricting in production", fieldName), 
                        field);
                }
                
                // Check for specific introspection fields
                if (fieldName.equals("__schema") || fieldName.equals("__type")) {
                    addError(result, 
                        String.format("Introspection field '%s' detected - security risk in production", fieldName), 
                        field);
                }
            }
        });
        
        // Check for introspection queries
        if (context.containsIntrospectionQueries()) {
            addError(result, 
                "Introspection queries detected - disable in production for security", 
                document);
        }
    }
    
    private void validateSensitiveFieldAccess(Document document, LintContext context, LintResult result) {
        Set<String> sensitiveFields = context.getConfigValue("sensitiveFields", Set.class, 
            Set.of("password", "ssn", "creditCard", "apiKey", "token", "secret"));
            
        context.traverseNodes(node -> {
            if (node instanceof Field) {
                Field field = (Field) node;
                String fieldName = field.getName().toLowerCase();
                
                // Check for sensitive fields
                for (String sensitiveField : sensitiveFields) {
                    if (fieldName.contains(sensitiveField.toLowerCase())) {
                        addWarning(result, 
                            String.format("Sensitive field '%s' detected - ensure proper access control", field.getName()), 
                            field);
                        break;
                    }
                }
                
                // Check for common sensitive patterns
                if (isSensitiveFieldPattern(fieldName)) {
                    addWarning(result, 
                        String.format("Field '%s' may contain sensitive data - verify access control", field.getName()), 
                        field);
                }
            }
        });
    }
    
    private void validateArgumentValidation(Document document, LintContext context, LintResult result) {
        boolean enforceInputValidation = context.getConfigValue("enforceInputValidation", Boolean.class, true);
        
        if (!enforceInputValidation) {
            return;
        }
        
        context.traverseNodes(node -> {
            if (node instanceof Argument) {
                Argument argument = (Argument) node;
                Value value = argument.getValue();
                
                // Check argument values for potential injection
                if (value instanceof StringValue) {
                    StringValue stringValue = (StringValue) value;
                    String content = stringValue.getValue();
                    
                    // Check for SQL injection patterns
                    if (SQL_INJECTION_PATTERN.matcher(content).find()) {
                        addError(result, 
                            String.format("Argument '%s' contains potential SQL injection pattern", argument.getName()), 
                            argument);
                    }
                    
                    // Check for XSS patterns
                    if (XSS_PATTERN.matcher(content).find()) {
                        addError(result, 
                            String.format("Argument '%s' contains potential XSS pattern", argument.getName()), 
                            argument);
                    }
                    
                    // Check for path traversal patterns
                    if (PATH_TRAVERSAL_PATTERN.matcher(content).find()) {
                        addError(result, 
                            String.format("Argument '%s' contains potential path traversal pattern", argument.getName()), 
                            argument);
                    }
                    
                    // Check for potentially unsafe characters
                    if (content.contains("'") || content.contains("\"") || content.contains(";")) {
                        addWarning(result, 
                            String.format("Argument '%s' contains potentially unsafe characters - ensure proper validation", 
                                argument.getName()), 
                            argument);
                    }
                }
                
                // Check for complex object values
                if (value instanceof ObjectValue) {
                    ObjectValue objValue = (ObjectValue) value;
                    for (ObjectField field : objValue.getObjectFields()) {
                        if (field.getValue() instanceof StringValue) {
                            StringValue stringValue = (StringValue) field.getValue();
                            String content = stringValue.getValue();
                            
                            // Check for injection patterns in object fields
                            if (SQL_INJECTION_PATTERN.matcher(content).find() || 
                                XSS_PATTERN.matcher(content).find() || 
                                PATH_TRAVERSAL_PATTERN.matcher(content).find()) {
                                addError(result, 
                                    String.format("Object field '%s' in argument '%s' contains potential injection pattern", 
                                        field.getName(), argument.getName()), 
                                    field);
                            }
                        }
                    }
                }
            }
        });
    }
    
    private void validateDepthLimits(Document document, LintContext context, LintResult result) {
        int maxSecurityDepth = context.getConfigValue("maxSecurityDepth", Integer.class, 3);
        int actualDepth = context.calculateMaxDepth();
        
        if (actualDepth > maxSecurityDepth) {
            addError(result, 
                String.format("Query depth (%d) exceeds security limit (%d) - potential DoS risk", 
                    actualDepth, maxSecurityDepth), 
                document);
        }
        
        // Check for deep nesting patterns that could cause resource exhaustion
        if (actualDepth > 5) {
            addError(result, 
                String.format("Very deep query structure (%d levels) - high risk of resource exhaustion", 
                    actualDepth), 
                document);
        }
    }
    
    private void validateQueryComplexity(Document document, LintContext context, LintResult result) {
        int maxQueryComplexity = context.getConfigValue("maxQueryComplexity", Integer.class, 50);
        int actualComplexity = calculateQueryComplexity(context);
        
        if (actualComplexity > maxQueryComplexity) {
            addError(result, 
                String.format("Query complexity (%d) exceeds security limit (%d) - potential DoS risk", 
                    actualComplexity, maxQueryComplexity), 
                document);
        }
        
        // Check for exponential complexity patterns
        if (hasExponentialComplexityPattern(context)) {
            addError(result, 
                "Query has exponential complexity pattern - high risk of resource exhaustion", 
                document);
        }
    }
    
    private void validateInputSanitization(Document document, LintContext context, LintResult result) {
        context.traverseNodes(node -> {
            if (node instanceof Argument) {
                Argument argument = (Argument) node;
                
                // Check argument names for potential injection
                String argName = argument.getName();
                if (SQL_INJECTION_PATTERN.matcher(argName).find() || 
                    XSS_PATTERN.matcher(argName).find() || 
                    PATH_TRAVERSAL_PATTERN.matcher(argName).find()) {
                    addError(result, 
                        String.format("Argument name '%s' contains potential injection pattern", argName), 
                        argument);
                }
            }
        });
    }
    
    private void validateDirectiveSecurity(Document document, LintContext context, LintResult result) {
        Set<String> forbiddenDirectives = context.getConfigValue("forbiddenDirectives", Set.class, 
            Set.of("auth", "admin", "internal"));
            
        context.traverseNodes(node -> {
            if (node instanceof Directive) {
                Directive directive = (Directive) node;
                String directiveName = directive.getName();
                
                // Check for forbidden directives
                if (forbiddenDirectives.contains(directiveName)) {
                    addError(result, 
                        String.format("Directive '%s' is forbidden in this context", directiveName), 
                        directive);
                }
                
                // Check directive arguments for security issues
                for (Argument argument : directive.getArguments()) {
                    if (argument.getValue() instanceof StringValue) {
                        StringValue stringValue = (StringValue) argument.getValue();
                        String content = stringValue.getValue();
                        
                        // Check for injection patterns in directive arguments
                        if (SQL_INJECTION_PATTERN.matcher(content).find() || 
                            XSS_PATTERN.matcher(content).find() || 
                            PATH_TRAVERSAL_PATTERN.matcher(content).find()) {
                            addError(result, 
                                String.format("Directive argument '%s' contains potential injection pattern", 
                                    argument.getName()), 
                                argument);
                        }
                    }
                }
            }
        });
    }
    
    private void validateFragmentSecurity(Document document, LintContext context, LintResult result) {
        context.traverseNodes(node -> {
            if (node instanceof FragmentDefinition) {
                FragmentDefinition fragment = (FragmentDefinition) node;
                
                // Check fragment name for potential injection
                String fragmentName = fragment.getName();
                if (SQL_INJECTION_PATTERN.matcher(fragmentName).find() || 
                    XSS_PATTERN.matcher(fragmentName).find() || 
                    PATH_TRAVERSAL_PATTERN.matcher(fragmentName).find()) {
                    addError(result, 
                        String.format("Fragment name '%s' contains potential injection pattern", fragmentName), 
                        fragment);
                }
                
                // Check fragment content for sensitive fields
                validateFragmentContent(fragment, context, result);
            }
        });
    }
    
    private void validateOperationSecurity(Document document, LintContext context, LintResult result) {
        context.traverseNodes(node -> {
            if (node instanceof OperationDefinition) {
                OperationDefinition operation = (OperationDefinition) node;
                
                // Check operation name for potential injection
                if (operation.getName() != null) {
                    String opName = operation.getName();
                    if (SQL_INJECTION_PATTERN.matcher(opName).find() || 
                        XSS_PATTERN.matcher(opName).find() || 
                        PATH_TRAVERSAL_PATTERN.matcher(opName).find()) {
                        addError(result, 
                            String.format("Operation name '%s' contains potential injection pattern", opName), 
                            operation);
                    }
                }
                
                // Check operation type security
                if (operation.getOperation() == OperationDefinition.Operation.MUTATION) {
                    // Check for multiple mutations in single operation
                    int mutationCount = countMutationsInOperation(operation, context);
                    if (mutationCount > 1) {
                        addWarning(result, 
                            String.format("Operation contains %d mutations - consider splitting for atomicity", 
                                mutationCount), 
                            operation);
                    }
                }
            }
        });
    }
    
    private void validateAccessControlPatterns(Document document, LintContext context, LintResult result) {
        boolean enforceAccessControl = context.getConfigValue("enforceAccessControl", Boolean.class, true);
        
        if (!enforceAccessControl) {
            return;
        }
        
        context.traverseNodes(node -> {
            if (node instanceof Field) {
                Field field = (Field) node;
                String fieldName = field.getName().toLowerCase();
                
                // Check for admin/privileged field patterns
                if (isAdminFieldPattern(fieldName)) {
                    addWarning(result, 
                        String.format("Field '%s' may require admin privileges - verify access control", 
                            field.getName()), 
                        field);
                }
                
                // Check for internal/system field patterns
                if (isInternalFieldPattern(fieldName)) {
                    addWarning(result, 
                        String.format("Field '%s' may be internal/system field - verify access control", 
                            field.getName()), 
                        field);
                }
            }
        });
    }
    
    private boolean isSensitiveFieldPattern(String fieldName) {
        String[] sensitivePatterns = {
            "password", "passwd", "pwd", "secret", "key", "token", "auth", "credential",
            "ssn", "social", "credit", "card", "account", "bank", "financial", "payment",
            "personal", "private", "confidential", "sensitive", "internal", "admin"
        };
        
        for (String pattern : sensitivePatterns) {
            if (fieldName.contains(pattern)) {
                return true;
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
                
                // Add complexity for nested fields
                if (field.getSelectionSet() != null) {
                    complexity.addAndGet(field.getSelectionSet().getSelections().size());
                }
            }
        });
        
        return complexity.get();
    }
    
    private boolean hasExponentialComplexityPattern(LintContext context) {
        // Check for patterns that could lead to exponential complexity
        AtomicInteger maxDepth = new AtomicInteger(0);
        AtomicInteger maxBreadth = new AtomicInteger(0);
        
        context.traverseNodes(node -> {
            if (node instanceof Field) {
                maxDepth.incrementAndGet();
                
                Field field = (Field) node;
                if (field.getSelectionSet() != null) {
                    int breadth = field.getSelectionSet().getSelections().size();
                    maxBreadth.updateAndGet(current -> Math.max(current, breadth));
                }
            }
        });
        
        // Check for exponential patterns (high depth + high breadth)
        return maxDepth.get() > 5 && maxBreadth.get() > 10;
    }
    
    private void validateFragmentContent(FragmentDefinition fragment, LintContext context, LintResult result) {
        Set<String> sensitiveFields = context.getConfigValue("sensitiveFields", Set.class, 
            Set.of("password", "ssn", "creditCard", "apiKey", "token", "secret"));
            
        context.traverseNodes(node -> {
            if (node instanceof Field) {
                Field field = (Field) node;
                String fieldName = field.getName().toLowerCase();
                
                for (String sensitiveField : sensitiveFields) {
                    if (fieldName.contains(sensitiveField.toLowerCase())) {
                        addWarning(result, 
                            String.format("Fragment '%s' contains sensitive field '%s' - verify access control", 
                                fragment.getName(), field.getName()), 
                            field);
                        break;
                    }
                }
            }
        });
    }
    
    private int countMutationsInOperation(OperationDefinition operation, LintContext context) {
        AtomicInteger count = new AtomicInteger(0);
        
        context.traverseNodes(node -> {
            if (node instanceof Field) {
                Field field = (Field) node;
                // This is a simplified check - in practice, you'd need to check the schema
                // to determine if a field is actually a mutation
                if (field.getName().toLowerCase().contains("create") || 
                    field.getName().toLowerCase().contains("update") || 
                    field.getName().toLowerCase().contains("delete") || 
                    field.getName().toLowerCase().contains("remove")) {
                    count.incrementAndGet();
                }
            }
        });
        
        return count.get();
    }
    
    private boolean isAdminFieldPattern(String fieldName) {
        String[] adminPatterns = {
            "admin", "administrator", "superuser", "root", "privileged", "elevated",
            "system", "internal", "private", "confidential", "restricted"
        };
        
        for (String pattern : adminPatterns) {
            if (fieldName.contains(pattern)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean isInternalFieldPattern(String fieldName) {
        String[] internalPatterns = {
            "internal", "system", "private", "hidden", "secret", "confidential",
            "restricted", "protected", "secure", "encrypted", "hashed"
        };
        
        for (String pattern : internalPatterns) {
            if (fieldName.contains(pattern)) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public String getCategory() {
        return "SECURITY";
    }
} 