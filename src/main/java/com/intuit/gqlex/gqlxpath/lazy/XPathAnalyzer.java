package com.intuit.gqlex.gqlxpath.lazy;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Analyzes xpath queries to determine what sections of a GraphQL document are required
 */
public class XPathAnalyzer {
    
    /**
     * Analyze an xpath query to determine required sections
     */
    public XPathAnalysis analyzeXPath(String xpath) {
        XPathAnalysis analysis = new XPathAnalysis();
        
        // Parse xpath components
        List<XPathComponent> components = parseXPathComponents(xpath);
        
        for (XPathComponent component : components) {
            analysis.addComponent(component);
            
            // Add required sections based on component type
            switch (component.getType()) {
                case FIELD:
                    analysis.addRequiredSection("field:" + component.getValue());
                    break;
                case FRAGMENT:
                    analysis.addRequiredSection("fragment:" + component.getValue());
                    break;
                case ARGUMENT:
                    analysis.addRequiredSection("argument:" + component.getValue());
                    break;
                case DIRECTIVE:
                    analysis.addRequiredSection("directive:" + component.getValue());
                    break;
                case OPERATION:
                    analysis.addRequiredSection("operation:" + component.getValue());
                    break;
                case ALIAS:
                    analysis.addRequiredSection("alias:" + component.getValue());
                    break;
                case VARIABLE:
                    analysis.addRequiredSection("variable:" + component.getValue());
                    break;
            }
            
            // Add predicates
            for (Map.Entry<String, String> entry : component.getAttributes().entrySet()) {
                analysis.addPredicate(entry.getKey(), entry.getValue());
            }
        }
        
        return analysis;
    }
    
    /**
     * Parse xpath into components
     */
    private List<XPathComponent> parseXPathComponents(String xpath) {
        List<XPathComponent> components = new ArrayList<>();
        
        // Split xpath by slashes
        String[] parts = xpath.split("/+");
        int position = 0;
        
        for (String part : parts) {
            if (part.trim().isEmpty()) {
                continue;
            }
            
            // Parse component
            XPathComponent component = parseComponent(part, position);
            if (component != null) {
                components.add(component);
            }
            
            position++;
        }
        
        return components;
    }
    
    /**
     * Parse a single component
     */
    private XPathComponent parseComponent(String part, int position) {
        if (part.trim().isEmpty()) {
            return null;
        }
        
        // Simple parsing - extract name and predicates
        String componentName = part;
        Map<String, String> attributes = new HashMap<>();
        
        // Check for predicates [key=value]
        if (part.contains("[") && part.contains("]")) {
            int startBracket = part.indexOf("[");
            int endBracket = part.indexOf("]");
            
            componentName = part.substring(0, startBracket);
            String predicate = part.substring(startBracket + 1, endBracket);
            
            // Parse predicate
            String[] pairs = predicate.split("\\s+");
            for (String pair : pairs) {
                if (pair.contains("=")) {
                    String[] keyValue = pair.split("=", 2);
                    if (keyValue.length == 2) {
                        String value = keyValue[1];
                        // Remove quotes if present
                        if ((value.startsWith("\"") && value.endsWith("\"")) ||
                            (value.startsWith("'") && value.endsWith("'"))) {
                            value = value.substring(1, value.length() - 1);
                        }
                        attributes.put(keyValue[0], value);
                    }
                }
            }
        }
        
        // Determine component type
        XPathComponentType type = determineComponentType(componentName);
        
        return new XPathComponent(type, componentName, position, attributes);
    }
    
    /**
     * Determine the type of a component
     */
    private XPathComponentType determineComponentType(String componentName) {
        // Check for operation types
        if (componentName.equalsIgnoreCase("query") || 
            componentName.equalsIgnoreCase("mutation") || 
            componentName.equalsIgnoreCase("subscription")) {
            return XPathComponentType.OPERATION;
        }
        
        // Check for fragment
        if (componentName.startsWith("fragment") || componentName.contains("fragment")) {
            return XPathComponentType.FRAGMENT;
        }
        
        // Check for directive
        if (componentName.startsWith("@") || componentName.contains("directive")) {
            return XPathComponentType.DIRECTIVE;
        }
        
        // Check for argument
        if (componentName.contains("argument") || componentName.contains("arg")) {
            return XPathComponentType.ARGUMENT;
        }
        
        // Check for alias
        if (componentName.contains("alias") || componentName.contains(":")) {
            return XPathComponentType.ALIAS;
        }
        
        // Check for variable
        if (componentName.startsWith("$") || componentName.contains("variable")) {
            return XPathComponentType.VARIABLE;
        }
        
        // Default to field
        return XPathComponentType.FIELD;
    }
} 