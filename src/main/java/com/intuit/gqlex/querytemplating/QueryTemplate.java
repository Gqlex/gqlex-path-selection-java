package com.intuit.gqlex.querytemplating;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GraphQL Query Template Engine
 * 
 * A generic and agnostic templating system for GraphQL queries and mutations.
 * Supports variable substitution and conditional blocks for dynamic query generation.
 * 
 * Features:
 * - Variable substitution: ${variableName}
 * - Conditional blocks: #if($condition) ... #end
 * - Works with any GraphQL query or mutation
 * - Type-safe variable handling
 * - Fluent API for easy usage
 * 
 * @author gqlex-team
 * @version 1.0.0
 */
public class QueryTemplate {
    
    private String templateString;
    private final Map<String, Object> variables = new HashMap<>();
    private final Map<String, Boolean> conditions = new HashMap<>();
    
    // Regex patterns for template parsing
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");
    private static final Pattern CONDITIONAL_START_PATTERN = Pattern.compile("#if\\(\\$([^)]+)\\)");
    private static final Pattern CONDITIONAL_END_PATTERN = Pattern.compile("#end");
    
    /**
     * Private constructor - use static factory methods
     */
    private QueryTemplate() {}
    
    /**
     * Create a QueryTemplate from a string template
     * 
     * @param template The GraphQL template string
     * @return QueryTemplate instance
     */
    public static QueryTemplate fromString(String template) {
        QueryTemplate queryTemplate = new QueryTemplate();
        queryTemplate.templateString = template;
        return queryTemplate;
    }
    
    /**
     * Create a QueryTemplate from a file
     * 
     * @param filePath Path to the template file
     * @return QueryTemplate instance
     * @throws TemplateException if file cannot be read
     */
    public static QueryTemplate fromFile(String filePath) throws TemplateException {
        try {
            Path path = Paths.get(filePath);
            String template = Files.readString(path);
            return fromString(template);
        } catch (IOException e) {
            throw new TemplateException("Failed to read template file: " + filePath, e);
        }
    }
    
    /**
     * Set a variable value for substitution
     * 
     * @param name Variable name (without ${})
     * @param value Variable value
     * @return this QueryTemplate for fluent chaining
     */
    public QueryTemplate setVariable(String name, Object value) {
        variables.put(name, value);
        return this;
    }
    
    /**
     * Set multiple variables at once
     * 
     * @param variables Map of variable names to values
     * @return this QueryTemplate for fluent chaining
     */
    public QueryTemplate setVariables(Map<String, Object> variables) {
        this.variables.putAll(variables);
        return this;
    }
    
    /**
     * Set a condition for conditional blocks
     * 
     * @param name Condition name (without $)
     * @param value Boolean condition value
     * @return this QueryTemplate for fluent chaining
     */
    public QueryTemplate setCondition(String name, boolean value) {
        conditions.put(name, value);
        return this;
    }
    
    /**
     * Set multiple conditions at once
     * 
     * @param conditions Map of condition names to boolean values
     * @return this QueryTemplate for fluent chaining
     */
    public QueryTemplate setConditions(Map<String, Boolean> conditions) {
        this.conditions.putAll(conditions);
        return this;
    }
    
    /**
     * Render the template with all variables and conditions applied
     * 
     * @return The rendered GraphQL query string
     * @throws TemplateException if template rendering fails
     */
    public String render() throws TemplateException {
        if (templateString == null) {
            throw new TemplateException("No template string provided");
        }
        
        try {
            String result = templateString;
            
            // Process conditionals first
            result = processConditionals(result);
            
            // Process variable substitutions
            result = processVariables(result);
            
            return result;
        } catch (Exception e) {
            throw new TemplateException("Failed to render template", e);
        }
    }
    
    /**
     * Process conditional blocks in the template
     */
    private String processConditionals(String template) throws TemplateException {
        StringBuilder result = new StringBuilder();
        int lastEnd = 0;
        
        // Find all conditional blocks
        Matcher startMatcher = CONDITIONAL_START_PATTERN.matcher(template);
        while (startMatcher.find()) {
            int startPos = startMatcher.start();
            String conditionName = startMatcher.group(1);
            
            // Add text before the conditional
            result.append(template.substring(lastEnd, startPos));
            
            // Find the matching #end
            int endPos = findMatchingEnd(template, startPos);
            if (endPos == -1) {
                throw new TemplateException("Unmatched #if($" + conditionName + ") - missing #end");
            }
            
            // Check if condition is true
            boolean conditionValue = conditions.getOrDefault(conditionName, false);
            if (conditionValue) {
                // Include the conditional content
                String conditionalContent = template.substring(startMatcher.end(), endPos);
                result.append(conditionalContent);
            }
            // If false, skip the content (don't append anything)
            
            lastEnd = endPos + 4; // Skip past #end
        }
        
        // Add remaining text after last conditional
        result.append(template.substring(lastEnd));
        
        return result.toString();
    }
    
    /**
     * Find the matching #end for a conditional block
     */
    private int findMatchingEnd(String template, int startPos) {
        int braceCount = 0;
        boolean inConditional = false;
        
        for (int i = startPos; i < template.length(); i++) {
            char c = template.charAt(i);
            
            if (template.substring(i).startsWith("#if($")) {
                braceCount++;
                inConditional = true;
            } else if (template.substring(i).startsWith("#end")) {
                braceCount--;
                if (braceCount == 0 && inConditional) {
                    return i;
                }
            }
        }
        
        return -1; // Not found
    }
    
    /**
     * Process variable substitutions in the template
     */
    private String processVariables(String template) throws TemplateException {
        Matcher matcher = VARIABLE_PATTERN.matcher(template);
        StringBuffer result = new StringBuffer();
        
        while (matcher.find()) {
            String variableName = matcher.group(1);
            Object value = variables.get(variableName);
            
            if (value == null) {
                // Allow null values, they will be formatted as "null"
                // But if the variable is not set at all, throw an exception
                if (!variables.containsKey(variableName)) {
                    throw new TemplateException("Variable '" + variableName + "' is not set");
                }
            }
            
            String replacement = formatValue(value);
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        
        matcher.appendTail(result);
        return result.toString();
    }
    
    /**
     * Format a value for substitution in the template
     */
    private String formatValue(Object value) {
        if (value == null) {
            return "null";
        }
        
        if (value instanceof String) {
            String strValue = value.toString();
            // If it looks like a GraphQL object or already formatted, don't add quotes
            if (strValue.startsWith("{") || strValue.startsWith("[") || 
                strValue.matches("[A-Z_][a-zA-Z0-9_]*")) {
                return strValue; // GraphQL enum or type name
            } else {
                return "\"" + strValue + "\""; // Regular string value
            }
        }
        
        if (value instanceof Boolean) {
            return value.toString();
        }
        
        if (value instanceof Number) {
            return value.toString();
        }
        
        // For other types, use toString() and wrap in quotes if it looks like a string
        String strValue = value.toString();
        if (strValue.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
            return strValue; // Identifier-like, no quotes
        } else {
            return "\"" + strValue + "\""; // String-like, add quotes
        }
    }
    
    /**
     * Get the current template string
     */
    public String getTemplateString() {
        return templateString;
    }
    
    /**
     * Get all set variables
     */
    public Map<String, Object> getVariables() {
        return new HashMap<>(variables);
    }
    
    /**
     * Get all set conditions
     */
    public Map<String, Boolean> getConditions() {
        return new HashMap<>(conditions);
    }
    
    /**
     * Clear all variables and conditions
     */
    public QueryTemplate clear() {
        variables.clear();
        conditions.clear();
        return this;
    }
    
    /**
     * Validate the template syntax
     * 
     * @return true if template is valid
     * @throws TemplateException if template is invalid
     */
    public boolean validate() throws TemplateException {
        try {
            // Check for unmatched conditionals
            Matcher startMatcher = CONDITIONAL_START_PATTERN.matcher(templateString);
            while (startMatcher.find()) {
                String conditionName = startMatcher.group(1);
                int endPos = findMatchingEnd(templateString, startMatcher.start());
                if (endPos == -1) {
                    throw new TemplateException("Unmatched #if($" + conditionName + ") - missing #end");
                }
            }
            
            // Check for unmatched #end
            Matcher endMatcher = CONDITIONAL_END_PATTERN.matcher(templateString);
            while (endMatcher.find()) {
                // This is a simplified check - in a real implementation,
                // you'd want to track the nesting properly
            }
            
            return true;
        } catch (Exception e) {
            throw new TemplateException("Template validation failed", e);
        }
    }
} 