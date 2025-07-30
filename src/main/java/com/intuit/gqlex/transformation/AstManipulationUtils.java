package com.intuit.gqlex.transformation;

import com.intuit.gqlex.gqlxpath.selector.SelectorFacade;
import com.intuit.gqlex.common.GqlNodeContext;
import graphql.language.*;
import graphql.parser.Parser;

import java.util.*;

/**
 * Utility class for AST manipulation operations used by transformation operations.
 */
public class AstManipulationUtils {
    
    private static final SelectorFacade selectorFacade = new SelectorFacade();
    
    /**
     * Finds a node at the specified path using gqlXPath.
     */
    public static Node findNodeAtPath(Document document, String path) {
        try {
            GqlNodeContext context = selectorFacade.selectSingle(document, path);
            return context != null ? context.getNode() : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Finds all nodes at the specified path using gqlXPath.
     */
    public static List<Node> findNodesAtPath(Document document, String path) {
        try {
            List<GqlNodeContext> contexts = selectorFacade.selectMany(document, path);
            List<Node> nodes = new ArrayList<>();
            for (GqlNodeContext context : contexts) {
                nodes.add(context.getNode());
            }
            return nodes;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
    
    /**
     * Renames a field in the document by creating a new field with the new name
     * and replacing it in the AST structure.
     */
    public static Document renameField(Document document, String path, String newName) {
        try {
            // Extract target field name from path for direct string manipulation
            String[] pathParts = path.split("/");
            if (pathParts.length < 3) {
                return document;
            }
            
            String oldName = pathParts[pathParts.length - 1];
            
            // Convert document to string for manipulation
            String queryString = AstPrinter.printAst(document);
            
            // Use regex to replace the field name while preserving structure
            String modifiedQuery = renameFieldInStringDirect(queryString, oldName, newName);
            
            // Parse the modified string back to a Document
            try {
                Parser parser = new Parser();
                Document newDocument = parser.parseDocument(modifiedQuery);
                return newDocument;
            } catch (Exception parseError) {
                // If parsing fails, return the original document
                return document;
            }
            
        } catch (Exception e) {
            return document; // Return unchanged on error
        }
    }
    
    /**
     * Renames a field in a GraphQL query string using direct string manipulation.
     * This is a generic and agnostic approach that preserves the GraphQL structure.
     */
    private static String renameFieldInStringDirect(String queryString, String oldName, String newName) {
        // Use word boundaries to ensure we only replace the exact field name
        // This prevents replacing "email" when it's part of "emailAddress"
        String pattern = "\\b" + oldName + "\\b";
        String result = queryString.replaceAll(pattern, newName);
        
        return result;
    }
    
    /**
     * Renames a field in a GraphQL query string using regex patterns.
     * This is a generic and agnostic approach that preserves the GraphQL structure.
     */
    private static String renameFieldInString(String queryString, String oldName, String newName) {
        // Use word boundaries to ensure we only replace the exact field name
        // This prevents replacing "email" when it's part of "emailAddress"
        String pattern = "\\b" + oldName + "\\b";
        String result = queryString.replaceAll(pattern, newName);
        
        return result;
    }
    
    /**
     * Adds a field to the document at the specified path.
     * This is a generic and agnostic approach that preserves the GraphQL structure.
     */
    public static Document addField(Document document, String path, String fieldName) {
        try {
            // Convert document to string for manipulation
            String queryString = AstPrinter.printAst(document);
            
            // Extract target field name from path for direct string manipulation
            String[] pathParts = path.split("/");
            if (pathParts.length < 3) {
                return document;
            }
            
            String targetFieldName = pathParts[pathParts.length - 1];
            
            // Direct string manipulation approach - more robust and generic
            String modifiedQuery = addFieldToQueryStringDirect(queryString, targetFieldName, fieldName);
            
            // Parse the modified string back to a Document
            try {
                Parser parser = new Parser();
                Document newDocument = parser.parseDocument(modifiedQuery);
                return newDocument;
            } catch (Exception parseError) {
                // If parsing fails, return the original document
                return document;
            }
            
        } catch (Exception e) {
            return document; // Return unchanged on error
        }
    }
    
    /**
     * Adds a field to a GraphQL query string using direct string manipulation.
     * This is a generic and agnostic approach that works with any GraphQL schema.
     */
    private static String addFieldToQueryStringDirect(String queryString, String targetFieldName, String fieldName) {
        try {
            // Use regex to find the target field with its selection set
            // Look for patterns like: targetFieldName { ... } or targetFieldName(...) { ... }
            // Use a more robust regex that handles nested braces correctly
            String regex = "\\b" + targetFieldName + "\\s*\\{";
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.DOTALL);
            java.util.regex.Matcher matcher = pattern.matcher(queryString);
            
            if (matcher.find()) {
                String matchedField = matcher.group();
                int fieldStart = matcher.start();
                int fieldEnd = matcher.end();
                
                // Find the closing brace of this field's selection set
                int braceCount = 0;
                int closingBracePos = -1;
                boolean inSelectionSet = false;
                
                for (int i = fieldStart; i < queryString.length(); i++) {
                    char c = queryString.charAt(i);
                    if (c == '{') {
                        braceCount++;
                        inSelectionSet = true;
                    } else if (c == '}') {
                        braceCount--;
                        if (braceCount == 0 && inSelectionSet) {
                            closingBracePos = i;
                            break;
                        }
                    }
                }
                
                if (closingBracePos != -1) {
                    // Add the new field before the closing brace
                    String beforeBrace = queryString.substring(0, closingBracePos);
                    String afterBrace = queryString.substring(closingBracePos);
                    
                    // Determine proper indentation by looking at the existing fields in the selection set
                    String indent = "    ";
                    // Look for the indentation pattern in the selection set
                    String selectionSet = queryString.substring(fieldStart, closingBracePos);
                    java.util.regex.Pattern indentPattern = java.util.regex.Pattern.compile("\\n(\\s+)\\w");
                    java.util.regex.Matcher indentMatcher = indentPattern.matcher(selectionSet);
                    if (indentMatcher.find()) {
                        indent = indentMatcher.group(1);
                    }
                    
                    String result = beforeBrace + "\n" + indent + fieldName + afterBrace;
                    return result;
                }
            }
            
            // If not found with selection set, try to find the field without selection set
            // This handles fields with arguments and directives
            String simpleRegex = "\\b" + targetFieldName + "\\b";
            java.util.regex.Pattern simplePattern = java.util.regex.Pattern.compile(simpleRegex);
            java.util.regex.Matcher simpleMatcher = simplePattern.matcher(queryString);
            
            if (simpleMatcher.find()) {
                int fieldStart = simpleMatcher.start();
                int fieldNameEnd = fieldStart + targetFieldName.length();
                
                // Look for the opening brace after the field (handles arguments and directives)
                int braceStart = queryString.indexOf("{", fieldNameEnd);
                if (braceStart != -1) {
                    // Find the closing brace
                    int braceCount = 0;
                    int closingBracePos = -1;
                    for (int i = braceStart; i < queryString.length(); i++) {
                        char c = queryString.charAt(i);
                        if (c == '{') braceCount++;
                        else if (c == '}') braceCount--;
                        if (braceCount == 0) {
                            closingBracePos = i;
                            break;
                        }
                    }
                    
                    if (closingBracePos != -1) {
                        // Determine proper indentation
                        String indent = "    ";
                        String selectionSet = queryString.substring(braceStart, closingBracePos);
                        java.util.regex.Pattern indentPattern = java.util.regex.Pattern.compile("\\n(\\s+)\\w");
                        java.util.regex.Matcher indentMatcher = indentPattern.matcher(selectionSet);
                        if (indentMatcher.find()) {
                            indent = indentMatcher.group(1);
                        }
                        
                        // Insert the new field before the closing brace
                        String beforeClosingBrace = queryString.substring(0, closingBracePos);
                        String afterClosingBrace = queryString.substring(closingBracePos);
                        
                        return beforeClosingBrace + "\n" + indent + fieldName + afterClosingBrace;
                    }
                }
            }
            
            return queryString;
        } catch (Exception e) {
            return queryString; // Return unchanged on error
        }
    }
    
    /**
     * Adds a field to a GraphQL query string.
     * This is a generic and agnostic approach that works with any GraphQL schema.
     */
    private static String addFieldToQueryString(String queryString, String path, String fieldName) {
        // Generic approach: find the target field and add the new field to its selection set
        try {
            // Parse the path to find the target field
            String[] pathParts = path.split("/");
            if (pathParts.length < 3) {
                System.out.println("DEBUG: Invalid path length: " + pathParts.length);
                return queryString; // Invalid path
            }
            
            String targetFieldName = pathParts[pathParts.length - 1];
            System.out.println("DEBUG: Target field name: " + targetFieldName);
            System.out.println("DEBUG: Query string: " + queryString);
            
            // More robust approach: use regex to find the target field with its selection set
            // Look for patterns like: targetFieldName { ... } or targetFieldName(...) { ... }
            String regex = "\\b" + targetFieldName + "\\s*\\{[^}]*\\}";
            System.out.println("DEBUG: Regex pattern: " + regex);
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.DOTALL);
            java.util.regex.Matcher matcher = pattern.matcher(queryString);
            
            if (matcher.find()) {
                String matchedField = matcher.group();
                System.out.println("DEBUG: Found match: " + matchedField);
                int fieldStart = matcher.start();
                int fieldEnd = matcher.end();
                
                // Find the closing brace of this field's selection set
                int braceCount = 0;
                int closingBracePos = -1;
                boolean inSelectionSet = false;
                
                for (int i = fieldStart; i < queryString.length(); i++) {
                    char c = queryString.charAt(i);
                    if (c == '{') {
                        braceCount++;
                        inSelectionSet = true;
                    } else if (c == '}') {
                        braceCount--;
                        if (braceCount == 0 && inSelectionSet) {
                            closingBracePos = i;
                            break;
                        }
                    }
                }
                
                if (closingBracePos != -1) {
                    System.out.println("DEBUG: Found closing brace at position: " + closingBracePos);
                    // Add the new field before the closing brace
                    String beforeBrace = queryString.substring(0, closingBracePos);
                    String afterBrace = queryString.substring(closingBracePos);
                    
                    // Determine proper indentation by looking at the last line before the closing brace
                    String indent = "    ";
                    int lastNewline = beforeBrace.lastIndexOf('\n');
                    if (lastNewline != -1) {
                        String lastLine = beforeBrace.substring(lastNewline + 1);
                        if (lastLine.trim().isEmpty()) {
                            // Use the same indentation as the existing fields
                            indent = lastLine;
                        }
                    }
                    
                    String result = beforeBrace + "\n" + indent + fieldName + afterBrace;
                    System.out.println("DEBUG: Result: " + result);
                    return result;
                } else {
                    System.out.println("DEBUG: Could not find closing brace");
                }
            } else {
                System.out.println("DEBUG: No match found for regex");
            }
            
            return queryString;
        } catch (Exception e) {
            System.out.println("DEBUG: Exception in addFieldToQueryString: " + e.getMessage());
            return queryString; // Return unchanged on error
        }
    }
    
    /**
     * Removes a field from the document at the specified path.
     * This is a generic and agnostic approach that preserves the GraphQL structure.
     */
    public static Document removeField(Document document, String path) {
        try {
            // Convert document to string for manipulation
            String queryString = AstPrinter.printAst(document);
            
            // Extract field name from path (last part after the last slash)
            String[] pathParts = path.split("/");
            String fieldName = pathParts[pathParts.length - 1];
            
            // Remove the field from the query string
            String modifiedQuery = removeFieldFromQueryString(queryString, fieldName);
            
            // If the modified query is empty or only contains the operation name and empty braces, 
            // create a minimal valid Document that will print as expected
            String trimmed = modifiedQuery.replaceAll("[\\s\\{\\}]", "");
            if (trimmed.isEmpty()) {
                // Extract operation name and type from original query
                String operationName = extractOperationName(queryString);
                String operationType = extractOperationType(queryString);
                // Create a minimal query with a dummy field to prevent AST reversion
                String minimalQuery = operationType + " " + operationName + " { _empty }";
                Parser parser = new Parser();
                return parser.parseDocument(minimalQuery);
            }
            
            // Parse the modified string back to a Document
            try {
                Parser parser = new Parser();
                Document newDocument = parser.parseDocument(modifiedQuery);
                return newDocument;
            } catch (Exception parseError) {
                // If parsing fails, return the original document
                return document;
            }
            
        } catch (Exception e) {
            return document; // Return unchanged on error
        }
    }
    
    private static String extractOperationName(String queryString) {
        // Extract operation name from query string
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(query|mutation|subscription)\\s+([a-zA-Z0-9_]+)");
        java.util.regex.Matcher matcher = pattern.matcher(queryString);
        if (matcher.find()) {
            return matcher.group(2);
        }
        return "Query"; // Default fallback
    }
    
    private static String extractOperationType(String queryString) {
        // Extract operation type from query string
        if (queryString.contains("mutation")) return "mutation";
        if (queryString.contains("subscription")) return "subscription";
        return "query"; // Default
    }
    
    /**
     * Removes a field from a GraphQL query string.
     * This is a generic and agnostic approach that works with any GraphQL schema.
     */
    private static String removeFieldFromQueryString(String queryString, String fieldName) {
        // Generic approach: remove the field and its entire selection set
        try {
            String prev;
            do {
                prev = queryString;
                // Use regex to match the field name followed by optional whitespace and a brace
                String regex = "(^|\n)[ \t]*" + fieldName + "[ \t\r\n]*\\{";
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
                java.util.regex.Matcher matcher = pattern.matcher(queryString);
                if (matcher.find()) {
                    int fieldStart = matcher.start();
                    int braceStart = queryString.indexOf('{', matcher.end() - 1);
                    if (braceStart == -1) break;
                    // Find the matching closing brace
                    int braceCount = 0;
                    int closingBracePos = -1;
                    for (int i = braceStart; i < queryString.length(); i++) {
                        char c = queryString.charAt(i);
                        if (c == '{') braceCount++;
                        else if (c == '}') braceCount--;
                        if (braceCount == 0) {
                            closingBracePos = i;
                            break;
                        }
                    }
                    if (closingBracePos != -1) {
                        String beforeField = queryString.substring(0, fieldStart);
                        String afterField = queryString.substring(closingBracePos + 1);
                        queryString = beforeField + afterField;
                        continue;
                    }
                } else {
                    // Field without selection set, try simple pattern matching
                    String regexSimple = "(^|\n)[ \t]*" + fieldName + "[ \t\r\n]*(?=\n|$)";
                    queryString = queryString.replaceAll(regexSimple, "");
                }
            } while (!queryString.equals(prev));
            
            // Also try to remove fields that might be on the same line
            String sameLineRegex = "\\s*" + fieldName + "\\s*";
            queryString = queryString.replaceAll(sameLineRegex, "");
            
            // Recursively remove empty blocks (e.g., 'inventory { }' or 'inventory{}')
            String emptyBlockRegex = "(^|\n)[ \t]*[a-zA-Z0-9_]+[ \t\r\n]*\\{[ \t\r\n]*\\}";
            prev = null;
            while (!queryString.equals(prev)) {
                prev = queryString;
                queryString = queryString.replaceAll(emptyBlockRegex, "");
            }
            return queryString;
        } catch (Exception e) {
            return queryString; // Return unchanged on error
        }
    }
    
    /**
     * Converts a Java object to a GraphQL Value.
     * Simplified implementation for now.
     */
    public static Value convertToGraphQLValue(Object value) {
        // Simplified implementation - in a full version, you would handle all value types properly
        if (value == null) {
            return null; // Handle null appropriately
        } else if (value instanceof String) {
            return new StringValue((String) value);
        } else {
            // For other types, convert to string for now
            return new StringValue(value.toString());
        }
    }
    
    /**
     * Adds a field to a selection set container.
     * Simplified implementation for now.
     */
    public static void addFieldToSelectionSet(SelectionSetContainer container, Field newField) {
        // Simplified implementation - in a full version, you would properly update the AST
        // For now, this is a placeholder that demonstrates the concept
        SelectionSet selectionSet = container.getSelectionSet();
        if (selectionSet == null) {
            // Create a new selection set if none exists
            // Note: In a real implementation, you'd need to properly update the container
        } else {
            // Add the field to the existing selection set
            // Note: In a real implementation, you'd need to create a new SelectionSet and update the container
        }
    }
    
    /**
     * Removes a field from its parent selection set.
     * Simplified implementation for now.
     */
    public static void removeFieldFromParent(Field fieldToRemove, Node parentNode) {
        // Simplified implementation - in a full version, you would properly update the AST
        // For now, this is a placeholder that demonstrates the concept
        if (parentNode instanceof SelectionSetContainer) {
            SelectionSetContainer container = (SelectionSetContainer) parentNode;
            SelectionSet selectionSet = container.getSelectionSet();
            
            if (selectionSet != null) {
                // Remove the field from the selection set
                // Note: In a real implementation, you'd need to create a new SelectionSet and update the container
            }
        }
    }
    
    /**
     * Adds an argument to a field.
     * Simplified implementation for now.
     */
    public static void addArgumentToField(Field field, String argumentName, Object argumentValue) {
        // Simplified implementation - in a full version, you would properly update the AST
        // For now, this is a placeholder that demonstrates the concept
        // Note: In a real implementation, you'd need to create a new Field with the added argument
    }
    
    /**
     * Updates an argument value in a field in the document.
     * This is a generic and agnostic approach that preserves the GraphQL structure.
     */
    public static Document updateArgument(Document document, String path, String argumentName, Object newValue) {
        try {
            // Convert document to string for manipulation
            String queryString = AstPrinter.printAst(document);
            
            // Find the target field to update argument in
            Node targetNode = findNodeAtPath(document, path);
            if (!(targetNode instanceof Field)) {
                return document; // Not a field, return unchanged
            }
            
            // Update the argument in the query string
            String modifiedQuery = updateArgumentInQueryString(queryString, path, argumentName, newValue);
            
            // Parse the modified string back to a Document
            try {
                Parser parser = new Parser();
                Document newDocument = parser.parseDocument(modifiedQuery);
                return newDocument;
            } catch (Exception parseError) {
                // If parsing fails, return the original document
                return document;
            }
            
        } catch (Exception e) {
            return document; // Return unchanged on error
        }
    }
    
    /**
     * Updates an argument in a GraphQL query string.
     * This is a generic and agnostic approach that works with any GraphQL schema.
     */
    private static String updateArgumentInQueryString(String queryString, String path, String argumentName, Object newValue) {
        // Generic approach: find and update the argument value
        try {
            String newValueStr = newValue instanceof String ? "\"" + newValue + "\"" : newValue.toString();
            
            // Parse the path to find the target field
            String[] pathParts = path.split("/");
            if (pathParts.length < 3) {
                return queryString; // Invalid path
            }
            
            String targetFieldName = pathParts[pathParts.length - 1];
            
            // Find the target field in the query string
            int fieldStart = queryString.indexOf(targetFieldName + "(");
            if (fieldStart != -1) {
                // Field has arguments, find and update the specific argument
                int openParen = queryString.indexOf("(", fieldStart);
                int closeParen = queryString.indexOf(")", openParen);
                if (closeParen != -1) {
                    String argsSection = queryString.substring(openParen + 1, closeParen);
                    String[] args = argsSection.split(",");
                    
                    for (int i = 0; i < args.length; i++) {
                        String arg = args[i].trim();
                        if (arg.startsWith(argumentName + ":")) {
                            // Found the argument, replace it
                            args[i] = argumentName + ": " + newValueStr;
                            String newArgsSection = String.join(", ", args);
                            return queryString.substring(0, openParen + 1) + newArgsSection + queryString.substring(closeParen);
                        }
                    }
                }
            }
            
            return queryString;
        } catch (Exception e) {
            return queryString; // Return unchanged on error
        }
    }
    
    /**
     * Removes an argument from a field.
     * Simplified implementation for now.
     */
    public static void removeArgumentFromField(Field field, String argumentName) {
        // Simplified implementation - in a full version, you would properly update the AST
        // For now, this is a placeholder that demonstrates the concept
        // Note: In a real implementation, you'd need to create a new Field without the argument
    }
    
    /**
     * Sets an alias for a field in the document.
     * This is a generic and agnostic approach that preserves the GraphQL structure.
     */
    public static Document setAlias(Document document, String path, String alias) {
        try {
            // Convert document to string for manipulation
            String queryString = AstPrinter.printAst(document);
            
            // Extract target field name from path for direct string manipulation
            String[] pathParts = path.split("/");
            if (pathParts.length < 3) {
                return document;
            }
            
            String targetFieldName = pathParts[pathParts.length - 1];
            
            // Set the alias in the query string
            String modifiedQuery = setAliasInQueryStringDirect(queryString, targetFieldName, alias);
            
            // Parse the modified string back to a Document
            try {
                Parser parser = new Parser();
                Document newDocument = parser.parseDocument(modifiedQuery);
                return newDocument;
            } catch (Exception parseError) {
                // If parsing fails, return the original document
                return document;
            }
            
        } catch (Exception e) {
            return document; // Return unchanged on error
        }
    }
    
    /**
     * Sets an alias in a GraphQL query string using direct string manipulation.
     * This is a generic and agnostic approach that works with any GraphQL schema.
     */
    private static String setAliasInQueryStringDirect(String queryString, String targetFieldName, String alias) {
        try {
            // Use regex to find the target field with its selection set
            String regex = "\\b" + targetFieldName + "\\s*\\{";
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.DOTALL);
            java.util.regex.Matcher matcher = pattern.matcher(queryString);
            
            if (matcher.find()) {
                int fieldStart = matcher.start();
                // Replace the field name with alias: fieldName
                String beforeField = queryString.substring(0, fieldStart);
                String afterField = queryString.substring(fieldStart + targetFieldName.length());
                return beforeField + alias + ": " + targetFieldName + afterField;
            }
            
            // If not found with selection set, try to find the field without selection set
            String simpleRegex = "\\b" + targetFieldName + "\\b";
            java.util.regex.Pattern simplePattern = java.util.regex.Pattern.compile(simpleRegex);
            java.util.regex.Matcher simpleMatcher = simplePattern.matcher(queryString);
            
            if (simpleMatcher.find()) {
                int fieldStart = simpleMatcher.start();
                String beforeField = queryString.substring(0, fieldStart);
                String afterField = queryString.substring(fieldStart + targetFieldName.length());
                return beforeField + alias + ": " + targetFieldName + afterField;
            }
            
            return queryString;
        } catch (Exception e) {
            return queryString; // Return unchanged on error
        }
    }
    
    /**
     * Sets an alias in a GraphQL query string.
     * This is a generic and agnostic approach that works with any GraphQL schema.
     */
    private static String setAliasInQueryString(String queryString, String fieldName, String alias) {
        // Generic approach: add alias to the field using word boundaries
        try {
            // Use word boundaries to ensure we only match the exact field name
            // This prevents matching "hero" when it's part of "superhero"
            String pattern = "\\b" + fieldName + "\\b";
            String replacement = alias + ": " + fieldName;
            
            return queryString.replaceAll(pattern, replacement);
        } catch (Exception e) {
            return queryString; // Return unchanged on error
        }
    }
    
    /**
     * Renames a field.
     * Simplified implementation for now.
     */
    public static void renameField(Field field, String newName) {
        // Simplified implementation - in a full version, you would properly update the AST
        // For now, this is a placeholder that demonstrates the concept
        // Note: In a real implementation, you'd need to create a new Field with the new name
    }
    
    /**
     * Renames an argument.
     * Simplified implementation for now.
     */
    public static void renameArgument(Field field, String oldName, String newName) {
        // Simplified implementation - in a full version, you would properly update the AST
        // For now, this is a placeholder that demonstrates the concept
        // Note: In a real implementation, you'd need to create a new Field with the renamed argument
    }
    
    /**
     * Inlines fragments in a document.
     * Simplified implementation for now.
     */
    public static void inlineFragments(Document document) {
        // Simplified implementation - in a full version, you would properly update the AST
        // For now, this is a placeholder that demonstrates the concept
        // Note: In a real implementation, you'd need to replace fragment spreads with their definitions
    }
    
    /**
     * Sorts fields in a field.
     * Simplified implementation for now.
     */
    public static void sortFields(Field field) {
        // Simplified implementation - in a full version, you would properly update the AST
        // For now, this is a placeholder that demonstrates the concept
        // Note: In a real implementation, you'd need to sort the fields in the selection set
    }
    
    /**
     * Normalizes arguments in a field.
     * Simplified implementation for now.
     */
    public static void normalizeArguments(Field field) {
        // Simplified implementation - in a full version, you would properly update the AST
        // For now, this is a placeholder that demonstrates the concept
        // Note: In a real implementation, you'd need to normalize argument values for consistent caching
    }
    
    /**
     * Extracts a fragment from a field.
     * Simplified implementation for now.
     */
    public static void extractFragment(Field field, String fragmentName) {
        // Simplified implementation - in a full version, you would properly update the AST
        // For now, this is a placeholder that demonstrates the concept
        // Note: In a real implementation, you'd need to create a fragment definition and replace the field with a fragment spread
    }
    
    /**
     * Adds an argument to a field in the document.
     * This is a generic and agnostic approach that preserves the GraphQL structure.
     */
    public static Document addArgument(Document document, String path, String argumentName, Object value) {
        try {
            // Convert document to string for manipulation
            String queryString = AstPrinter.printAst(document);
            
            // Extract target field name from path for direct string manipulation
            String[] pathParts = path.split("/");
            if (pathParts.length < 3) {
                return document;
            }
            
            String targetFieldName = pathParts[pathParts.length - 1];
            
            // Add the argument to the query string
            String modifiedQuery = addArgumentToQueryStringDirect(queryString, targetFieldName, argumentName, value);
            
            // Parse the modified string back to a Document
            try {
                Parser parser = new Parser();
                Document newDocument = parser.parseDocument(modifiedQuery);
                return newDocument;
            } catch (Exception parseError) {
                // If parsing fails, return the original document
                return document;
            }
            
        } catch (Exception e) {
            return document; // Return unchanged on error
        }
    }
    
    /**
     * Adds an argument to a GraphQL query string using direct string manipulation.
     * This is a generic and agnostic approach that works with any GraphQL schema.
     */
    private static String addArgumentToQueryStringDirect(String queryString, String targetFieldName, String argumentName, Object value) {
        try {
            String valueStr = value instanceof String ? "\"" + value + "\"" : value.toString();
            
            // Use regex to find the target field with its selection set
            String regex = "\\b" + targetFieldName + "\\s*\\{";
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.DOTALL);
            java.util.regex.Matcher matcher = pattern.matcher(queryString);
            
            if (matcher.find()) {
                int fieldStart = matcher.start();
                int fieldNameEnd = fieldStart + targetFieldName.length();
                
                // Check if field already has arguments
                String afterFieldName = queryString.substring(fieldNameEnd);
                if (afterFieldName.trim().startsWith("(")) {
                    // Field has arguments, add to existing list
                    int openParen = queryString.indexOf("(", fieldNameEnd);
                    int closeParen = queryString.indexOf(")", openParen);
                    if (closeParen != -1) {
                        String beforeArgs = queryString.substring(0, closeParen);
                        String afterArgs = queryString.substring(closeParen);
                        return beforeArgs + ", " + argumentName + ": " + valueStr + afterArgs;
                    }
                } else {
                    // Field has no arguments, add new argument list
                    String beforeField = queryString.substring(0, fieldNameEnd);
                    String afterField = queryString.substring(fieldNameEnd);
                    return beforeField + "(" + argumentName + ": " + valueStr + ")" + afterField;
                }
            }
            
            // If not found with selection set, try to find the field without selection set
            String simpleRegex = "\\b" + targetFieldName + "\\b";
            java.util.regex.Pattern simplePattern = java.util.regex.Pattern.compile(simpleRegex);
            java.util.regex.Matcher simpleMatcher = simplePattern.matcher(queryString);
            
            if (simpleMatcher.find()) {
                int fieldStart = simpleMatcher.start();
                int fieldNameEnd = fieldStart + targetFieldName.length();
                
                // Check if field already has arguments
                String afterFieldName = queryString.substring(fieldNameEnd);
                if (afterFieldName.trim().startsWith("(")) {
                    // Field has arguments, add to existing list
                    int openParen = queryString.indexOf("(", fieldNameEnd);
                    int closeParen = queryString.indexOf(")", openParen);
                    if (closeParen != -1) {
                        String beforeArgs = queryString.substring(0, closeParen);
                        String afterArgs = queryString.substring(closeParen);
                        return beforeArgs + ", " + argumentName + ": " + valueStr + afterArgs;
                    }
                } else {
                    // Field has no arguments, add new argument list
                    String beforeField = queryString.substring(0, fieldNameEnd);
                    String afterField = queryString.substring(fieldNameEnd);
                    return beforeField + "(" + argumentName + ": " + valueStr + ")" + afterField;
                }
            }
            
            return queryString;
        } catch (Exception e) {
            return queryString; // Return unchanged on error
        }
    }
    
    /**
     * Adds an argument to a GraphQL query string.
     * This is a generic and agnostic approach that works with any GraphQL schema.
     */
    private static String addArgumentToQueryString(String queryString, String path, String argumentName, Object value) {
        // Generic approach: find the target field and add the argument to it
        try {
            // Parse the path to find the target field
            String[] pathParts = path.split("/");
            if (pathParts.length < 3) {
                return queryString; // Invalid path
            }
            
            String targetFieldName = pathParts[pathParts.length - 1];
            String valueStr = value instanceof String ? "\"" + value + "\"" : value.toString();
            
            // Find the target field in the query string
            int fieldStart = queryString.indexOf(targetFieldName + "(");
            
            if (fieldStart != -1) {
                // Field has arguments, add to existing list
                int openParen = queryString.indexOf("(", fieldStart);
                int closeParen = queryString.indexOf(")", openParen);
                if (closeParen != -1) {
                    String beforeArgs = queryString.substring(0, closeParen);
                    String afterArgs = queryString.substring(closeParen);
                    return beforeArgs + ", " + argumentName + ": " + valueStr + afterArgs;
                }
            } else {
                // Field has no arguments, add new argument list
                fieldStart = queryString.indexOf(targetFieldName + " {");
                if (fieldStart != -1) {
                    String beforeField = queryString.substring(0, fieldStart + targetFieldName.length());
                    String afterField = queryString.substring(fieldStart + targetFieldName.length());
                    return beforeField + "(" + argumentName + ": " + valueStr + ")" + afterField;
                }
            }
            
            return queryString;
        } catch (Exception e) {
            return queryString; // Return unchanged on error
        }
    }
} 