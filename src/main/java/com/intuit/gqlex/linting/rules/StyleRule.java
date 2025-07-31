package com.intuit.gqlex.linting.rules;

import com.intuit.gqlex.linting.core.LintContext;
import com.intuit.gqlex.linting.core.LintResult;
import graphql.language.*;
import java.util.regex.Pattern;

/**
 * Enforces GraphQL code style conventions and formatting rules.
 * Generic and agnostic to specific field names or schema structures.
 */
public class StyleRule extends LintRule {
    
    private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("^[a-z][a-zA-Z0-9]*$");
    private static final Pattern PASCAL_CASE_PATTERN = Pattern.compile("^[A-Z][a-zA-Z0-9]*$");
    
    public StyleRule() {
        super("STYLE", "Enforces GraphQL code style conventions");
    }
    
    @Override
    public void lint(LintContext context, LintResult result) {
        Document document = context.getDocument();
        
        // Validate naming conventions
        validateNamingConventions(document, context, result);
        
        // Validate consistent spacing
        validateConsistentSpacing(document, context, result);
        
        // Validate indentation
        validateIndentation(document, context, result);
        
        // Validate line length
        validateLineLength(document, context, result);
        
        // Validate field naming consistency
        validateFieldNamingConsistency(document, context, result);
        
        // Validate argument naming
        validateArgumentNaming(document, context, result);
        
        // Validate directive naming
        validateDirectiveNaming(document, context, result);
        
        // Validate fragment naming
        validateFragmentNaming(document, context, result);
        
        // Validate operation naming
        validateOperationNaming(document, context, result);
    }
    
    private void validateNamingConventions(Document document, LintContext context, LintResult result) {
        boolean enforceCamelCase = context.getConfigValue("enforceCamelCase", Boolean.class, true);
        
        if (!enforceCamelCase) {
            return;
        }
        
        context.traverseNodes(node -> {
            if (node instanceof Field) {
                Field field = (Field) node;
                String fieldName = field.getName();
                
                // Skip introspection fields
                if (fieldName.startsWith("__")) {
                    return;
                }
                
                // Check camelCase convention
                if (!CAMEL_CASE_PATTERN.matcher(fieldName).matches()) {
                    addWarning(result, 
                        String.format("Field name '%s' should follow camelCase convention", fieldName), 
                        field);
                }
            }
            
            if (node instanceof Argument) {
                Argument argument = (Argument) node;
                String argName = argument.getName();
                
                // Check camelCase convention
                if (!CAMEL_CASE_PATTERN.matcher(argName).matches()) {
                    addWarning(result, 
                        String.format("Argument name '%s' should follow camelCase convention", argName), 
                        argument);
                }
            }
            
            if (node instanceof Directive) {
                Directive directive = (Directive) node;
                String directiveName = directive.getName();
                
                // Check camelCase convention
                if (!CAMEL_CASE_PATTERN.matcher(directiveName).matches()) {
                    addWarning(result, 
                        String.format("Directive name '%s' should follow camelCase convention", directiveName), 
                        directive);
                }
            }
        });
    }
    
    private void validateConsistentSpacing(Document document, LintContext context, LintResult result) {
        boolean enforceConsistentSpacing = context.getConfigValue("enforceConsistentSpacing", Boolean.class, true);
        
        if (!enforceConsistentSpacing) {
            return;
        }
        
        String queryString = AstPrinter.printAst(document);
        
        // Check for multiple consecutive spaces
        if (queryString.contains("  ")) {
            addInfo(result, 
                "Multiple consecutive spaces detected - use consistent indentation", 
                document);
        }
        
        // Check for missing spaces around operators
        if (queryString.contains(":") && !queryString.contains(" : ")) {
            addWarning(result, 
                "Missing spaces around colon - use consistent spacing", 
                document);
        }
        
        // Check for inconsistent spacing around braces
        if (queryString.contains("{") && !queryString.contains(" { ")) {
            addInfo(result, 
                "Consider consistent spacing around braces", 
                document);
        }
    }
    
    private void validateIndentation(Document document, LintContext context, LintResult result) {
        boolean enforceIndentation = context.getConfigValue("enforceIndentation", Boolean.class, true);
        int maxIndentationLevel = context.getConfigValue("maxIndentationLevel", Integer.class, 4);
        
        if (!enforceIndentation) {
            return;
        }
        
        String queryString = AstPrinter.printAst(document);
        String[] lines = queryString.split("\n");
        
        int currentIndent = 0;
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            
            int actualIndent = line.length() - line.trim().length();
            
            // Check if indentation is consistent (multiples of 2)
            if (actualIndent % 2 != 0) {
                addWarning(result, 
                    "Inconsistent indentation - use 2 spaces per level", 
                    document);
                break;
            }
            
            // Check indentation level
            int indentLevel = actualIndent / 2;
            if (indentLevel > maxIndentationLevel) {
                addWarning(result, 
                    String.format("Indentation level (%d) exceeds recommended limit (%d)", 
                        indentLevel, maxIndentationLevel), 
                    document);
                break;
            }
        }
    }
    
    private void validateLineLength(Document document, LintContext context, LintResult result) {
        int maxLineLength = context.getConfigValue("maxLineLength", Integer.class, 80);
        
        String queryString = AstPrinter.printAst(document);
        String[] lines = queryString.split("\n");
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.length() > maxLineLength) {
                addWarning(result, 
                    String.format("Line %d length (%d) exceeds recommended limit (%d)", 
                        i + 1, line.length(), maxLineLength), 
                    document);
                break;
            }
        }
    }
    
    private void validateFieldNamingConsistency(Document document, LintContext context, LintResult result) {
        context.traverseNodes(node -> {
            if (node instanceof Field) {
                Field field = (Field) node;
                String fieldName = field.getName();
                
                // Skip introspection fields
                if (fieldName.startsWith("__")) {
                    return;
                }
                
                // Check for common naming issues
                if (fieldName.contains("_")) {
                    addInfo(result, 
                        String.format("Field name '%s' contains underscores - consider camelCase", fieldName), 
                        field);
                }
                
                if (fieldName.matches(".*[A-Z]{2,}.*")) {
                    addInfo(result, 
                        String.format("Field name '%s' contains consecutive uppercase letters - consider camelCase", fieldName), 
                        field);
                }
                
                // Check for reserved words
                if (isReservedWord(fieldName)) {
                    addWarning(result, 
                        String.format("Field name '%s' is a reserved word - consider alternative naming", fieldName), 
                        field);
                }
            }
        });
    }
    
    private void validateArgumentNaming(Document document, LintContext context, LintResult result) {
        context.traverseNodes(node -> {
            if (node instanceof Argument) {
                Argument argument = (Argument) node;
                String argName = argument.getName();
                
                // Check for common naming issues
                if (argName.contains("_")) {
                    addInfo(result, 
                        String.format("Argument name '%s' contains underscores - consider camelCase", argName), 
                        argument);
                }
                
                // Check for reserved words
                if (isReservedWord(argName)) {
                    addWarning(result, 
                        String.format("Argument name '%s' is a reserved word - consider alternative naming", argName), 
                        argument);
                }
            }
        });
    }
    
    private void validateDirectiveNaming(Document document, LintContext context, LintResult result) {
        context.traverseNodes(node -> {
            if (node instanceof Directive) {
                Directive directive = (Directive) node;
                String directiveName = directive.getName();
                
                // Check for common naming issues
                if (directiveName.contains("_")) {
                    addInfo(result, 
                        String.format("Directive name '%s' contains underscores - consider camelCase", directiveName), 
                        directive);
                }
                
                // Check for reserved words
                if (isReservedWord(directiveName)) {
                    addWarning(result, 
                        String.format("Directive name '%s' is a reserved word - consider alternative naming", directiveName), 
                        directive);
                }
            }
        });
    }
    
    private void validateFragmentNaming(Document document, LintContext context, LintResult result) {
        context.traverseNodes(node -> {
            if (node instanceof FragmentDefinition) {
                FragmentDefinition fragment = (FragmentDefinition) node;
                String fragmentName = fragment.getName();
                
                // Check PascalCase convention for fragments
                if (!PASCAL_CASE_PATTERN.matcher(fragmentName).matches()) {
                    addWarning(result, 
                        String.format("Fragment name '%s' should follow PascalCase convention", fragmentName), 
                        fragment);
                }
                
                // Check for common naming issues
                if (fragmentName.contains("_")) {
                    addInfo(result, 
                        String.format("Fragment name '%s' contains underscores - consider PascalCase", fragmentName), 
                        fragment);
                }
            }
        });
    }
    
    private void validateOperationNaming(Document document, LintContext context, LintResult result) {
        context.traverseNodes(node -> {
            if (node instanceof OperationDefinition) {
                OperationDefinition operation = (OperationDefinition) node;
                String operationName = operation.getName();
                
                if (operationName != null) {
                    // Check PascalCase convention for operations
                    if (!PASCAL_CASE_PATTERN.matcher(operationName).matches()) {
                        addWarning(result, 
                            String.format("Operation name '%s' should follow PascalCase convention", operationName), 
                            operation);
                    }
                    
                    // Check for common naming issues
                    if (operationName.contains("_")) {
                        addInfo(result, 
                            String.format("Operation name '%s' contains underscores - consider PascalCase", operationName), 
                            operation);
                    }
                }
            }
        });
    }
    
    private boolean isReservedWord(String name) {
        String[] reservedWords = {
            "query", "mutation", "subscription", "fragment", "on", "true", "false", "null",
            "type", "interface", "union", "enum", "input", "scalar", "directive", "schema",
            "extend", "implements", "repeatable", "deprecated", "include", "skip"
        };
        
        for (String reserved : reservedWords) {
            if (name.equalsIgnoreCase(reserved)) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public String getCategory() {
        return "STYLE";
    }
} 