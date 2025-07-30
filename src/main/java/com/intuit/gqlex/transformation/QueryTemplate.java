package com.intuit.gqlex.transformation;

import graphql.language.Document;
import graphql.parser.Parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Template system for GraphQL queries with variable substitution and conditional logic.
 */
public class QueryTemplate {
    
    private final String templateString;
    private final Map<String, Object> variables;
    private final Map<String, Boolean> conditions;
    
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");
    private static final Pattern CONDITIONAL_PATTERN = Pattern.compile("#if\\(\\$([^)]+)\\)(.*?)#end", Pattern.DOTALL);
    
    /**
     * Creates a template from a string.
     */
    public QueryTemplate(String templateString) {
        this.templateString = templateString;
        this.variables = new HashMap<>();
        this.conditions = new HashMap<>();
    }
    
    /**
     * Creates a template from a file.
     */
    public static QueryTemplate fromFile(String filePath) throws IOException {
        String content = Files.readString(new File(filePath).toPath());
        return new QueryTemplate(content);
    }
    
    /**
     * Creates a template from a file.
     */
    public static QueryTemplate fromFile(File file) throws IOException {
        String content = Files.readString(file.toPath());
        return new QueryTemplate(content);
    }
    
    /**
     * Sets a variable for substitution.
     */
    public QueryTemplate setVariable(String name, Object value) {
        variables.put(name, value);
        return this;
    }
    
    /**
     * Sets multiple variables for substitution.
     */
    public QueryTemplate setVariables(Map<String, Object> variables) {
        this.variables.putAll(variables);
        return this;
    }
    
    /**
     * Sets a condition for conditional field inclusion.
     */
    public QueryTemplate setCondition(String name, boolean value) {
        conditions.put(name, value);
        return this;
    }
    
    /**
     * Sets multiple conditions for conditional field inclusion.
     */
    public QueryTemplate setConditions(Map<String, Boolean> conditions) {
        this.conditions.putAll(conditions);
        return this;
    }
    
    /**
     * Renders the template with the current variables and conditions.
     */
    public String render() {
        String result = templateString;
        
        // Apply variable substitution first
        result = applyVariableSubstitution(result);
        
        // Apply conditional logic
        result = applyConditionalLogic(result);
        
        return result;
    }
    
    /**
     * Renders the template and returns a GraphQL Document.
     */
    public Document renderAsDocument() {
        String renderedQuery = render();
        Parser parser = new Parser();
        return parser.parseDocument(renderedQuery);
    }
    
    /**
     * Applies conditional logic to the template.
     */
    private String applyConditionalLogic(String template) {
        Matcher matcher = CONDITIONAL_PATTERN.matcher(template);
        StringBuffer result = new StringBuffer();
        
        while (matcher.find()) {
            String conditionName = matcher.group(1).trim();
            String conditionalContent = matcher.group(2);
            
            Boolean conditionValue = conditions.get(conditionName);
            if (conditionValue != null && conditionValue) {
                // Condition is true, include the content
                matcher.appendReplacement(result, conditionalContent);
            } else {
                // Condition is false, remove the content
                matcher.appendReplacement(result, "");
            }
        }
        matcher.appendTail(result);
        
        return result.toString();
    }
    
    /**
     * Applies variable substitution to the template.
     */
    private String applyVariableSubstitution(String template) {
        Matcher matcher = VARIABLE_PATTERN.matcher(template);
        StringBuffer result = new StringBuffer();
        
        while (matcher.find()) {
            String variableName = matcher.group(1).trim();
            Object value = variables.get(variableName);
            
            String replacement = value != null ? value.toString() : "";
            // Escape any special regex characters in the replacement
            replacement = replacement.replace("\\", "\\\\").replace("$", "\\$");
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);
        
        return result.toString();
    }
    
    /**
     * Gets all variables.
     */
    public Map<String, Object> getVariables() {
        return new HashMap<>(variables);
    }
    
    /**
     * Gets all conditions.
     */
    public Map<String, Boolean> getConditions() {
        return new HashMap<>(conditions);
    }
    
    /**
     * Gets the original template string.
     */
    public String getTemplateString() {
        return templateString;
    }
    
    /**
     * Validates that all variables are set.
     */
    public boolean validate() {
        Matcher matcher = VARIABLE_PATTERN.matcher(templateString);
        while (matcher.find()) {
            String variableName = matcher.group(1).trim();
            if (!variables.containsKey(variableName)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Gets missing variables.
     */
    public java.util.Set<String> getMissingVariables() {
        java.util.Set<String> missing = new java.util.HashSet<>();
        Matcher matcher = VARIABLE_PATTERN.matcher(templateString);
        while (matcher.find()) {
            String variableName = matcher.group(1).trim();
            if (!variables.containsKey(variableName)) {
                missing.add(variableName);
            }
        }
        return missing;
    }
} 