package com.intuit.gqlex.gqlxpath.lazy;

import java.util.*;

/**
 * Analyzes xpath queries to determine what sections of a GraphQL document are required
 * for efficient lazy loading.
 */
public class XPathAnalysis {
    
    private final Set<String> requiredSections = new HashSet<>();
    private final List<XPathComponent> components = new ArrayList<>();
    private final Map<String, String> predicates = new HashMap<>();
    
    /**
     * Add a required section to the analysis
     */
    public void addRequiredSection(String section) {
        requiredSections.add(section);
    }
    
    /**
     * Get all required sections
     */
    public Set<String> getRequiredSections() {
        return Collections.unmodifiableSet(requiredSections);
    }
    
    /**
     * Add a component to the analysis
     */
    public void addComponent(XPathComponent component) {
        components.add(component);
    }
    
    /**
     * Get all components
     */
    public List<XPathComponent> getComponents() {
        return Collections.unmodifiableList(components);
    }
    
    /**
     * Add a predicate to the analysis
     */
    public void addPredicate(String key, String value) {
        predicates.put(key, value);
    }
    
    /**
     * Get all predicates
     */
    public Map<String, String> getPredicates() {
        return Collections.unmodifiableMap(predicates);
    }
    
    /**
     * Check if a specific section is required
     */
    public boolean isSectionRequired(String section) {
        return requiredSections.contains(section);
    }
    
    /**
     * Get the depth of the xpath query
     */
    public int getDepth() {
        return components.size();
    }
    
    /**
     * Check if the xpath requires fragment resolution
     */
    public boolean requiresFragmentResolution() {
        return components.stream()
            .anyMatch(comp -> comp.getType() == XPathComponentType.FRAGMENT);
    }
    
    /**
     * Check if the xpath requires field resolution
     */
    public boolean requiresFieldResolution() {
        return components.stream()
            .anyMatch(comp -> comp.getType() == XPathComponentType.FIELD);
    }
    
    /**
     * Check if the xpath requires argument resolution
     */
    public boolean requiresArgumentResolution() {
        return components.stream()
            .anyMatch(comp -> comp.getType() == XPathComponentType.ARGUMENT);
    }
    
    /**
     * Check if the xpath requires directive resolution
     */
    public boolean requiresDirectiveResolution() {
        return components.stream()
            .anyMatch(comp -> comp.getType() == XPathComponentType.DIRECTIVE);
    }
    
    @Override
    public String toString() {
        return "XPathAnalysis{" +
                "requiredSections=" + requiredSections +
                ", components=" + components +
                ", predicates=" + predicates +
                '}';
    }
} 