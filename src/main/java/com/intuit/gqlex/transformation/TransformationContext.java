package com.intuit.gqlex.transformation;

import com.intuit.gqlex.common.GqlNode;
import com.intuit.gqlex.common.DocumentElementType;
import graphql.language.*;

import java.util.*;

/**
 * Context object that holds state and metadata during query transformations.
 */
public class TransformationContext {
    
    private final Document originalDocument;
    private final Map<String, Object> variables;
    private final Map<String, Boolean> conditions;
    private final Map<String, FragmentDefinition> fragments;
    private final List<String> errors;
    private final Map<String, Object> metadata;
    
    public TransformationContext(Document document) {
        this.originalDocument = document;
        this.variables = new HashMap<>();
        this.conditions = new HashMap<>();
        this.fragments = new HashMap<>();
        this.errors = new ArrayList<>();
        this.metadata = new HashMap<>();
        
        // Extract fragments from the document
        extractFragments();
    }
    
    /**
     * Extracts fragment definitions from the document.
     */
    private void extractFragments() {
        if (originalDocument.getDefinitions() != null) {
            for (Definition definition : originalDocument.getDefinitions()) {
                if (definition instanceof FragmentDefinition) {
                    FragmentDefinition fragment = (FragmentDefinition) definition;
                    fragments.put(fragment.getName(), fragment);
                }
            }
        }
    }
    
    /**
     * Gets the original document.
     */
    public Document getOriginalDocument() {
        return originalDocument;
    }
    
    /**
     * Gets a variable value.
     */
    public Object getVariable(String name) {
        return variables.get(name);
    }
    
    /**
     * Sets a variable value.
     */
    public void setVariable(String name, Object value) {
        variables.put(name, value);
    }
    
    /**
     * Gets all variables.
     */
    public Map<String, Object> getVariables() {
        return Collections.unmodifiableMap(variables);
    }
    
    /**
     * Gets a condition value.
     */
    public Boolean getCondition(String name) {
        return conditions.get(name);
    }
    
    /**
     * Sets a condition value.
     */
    public void setCondition(String name, Boolean value) {
        conditions.put(name, value);
    }
    
    /**
     * Gets all conditions.
     */
    public Map<String, Boolean> getConditions() {
        return Collections.unmodifiableMap(conditions);
    }
    
    /**
     * Gets a fragment definition by name.
     */
    public FragmentDefinition getFragment(String name) {
        return fragments.get(name);
    }
    
    /**
     * Gets all fragment definitions.
     */
    public Map<String, FragmentDefinition> getFragments() {
        return Collections.unmodifiableMap(fragments);
    }
    
    /**
     * Adds an error message.
     */
    public void addError(String error) {
        errors.add(error);
    }
    
    /**
     * Gets all error messages.
     */
    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
    
    /**
     * Checks if there are any errors.
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    /**
     * Sets metadata value.
     */
    public void setMetadata(String key, Object value) {
        metadata.put(key, value);
    }
    
    /**
     * Gets metadata value.
     */
    public Object getMetadata(String key) {
        return metadata.get(key);
    }
    
    /**
     * Gets all metadata.
     */
    public Map<String, Object> getMetadata() {
        return Collections.unmodifiableMap(metadata);
    }
} 