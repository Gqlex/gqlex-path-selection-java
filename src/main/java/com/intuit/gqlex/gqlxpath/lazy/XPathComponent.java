package com.intuit.gqlex.gqlxpath.lazy;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single component in an xpath query
 */
public class XPathComponent {
    
    private final XPathComponentType type;
    private final String value;
    private final Map<String, String> attributes;
    private final int position;
    
    public XPathComponent(XPathComponentType type, String value, int position) {
        this.type = type;
        this.value = value;
        this.position = position;
        this.attributes = new HashMap<>();
    }
    
    public XPathComponent(XPathComponentType type, String value, int position, Map<String, String> attributes) {
        this.type = type;
        this.value = value;
        this.position = position;
        this.attributes = new HashMap<>(attributes);
    }
    
    /**
     * Get the type of this component
     */
    public XPathComponentType getType() {
        return type;
    }
    
    /**
     * Get the value of this component
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Get the position of this component in the xpath
     */
    public int getPosition() {
        return position;
    }
    
    /**
     * Get all attributes for this component
     */
    public Map<String, String> getAttributes() {
        return new HashMap<>(attributes);
    }
    
    /**
     * Get a specific attribute value
     */
    public String getAttribute(String key) {
        return attributes.get(key);
    }
    
    /**
     * Add an attribute to this component
     */
    public void addAttribute(String key, String value) {
        attributes.put(key, value);
    }
    
    /**
     * Check if this component has a specific attribute
     */
    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }
    
    @Override
    public String toString() {
        return "XPathComponent{" +
                "type=" + type +
                ", value='" + value + '\'' +
                ", position=" + position +
                ", attributes=" + attributes +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        XPathComponent that = (XPathComponent) o;
        
        if (position != that.position) return false;
        if (type != that.type) return false;
        if (!value.equals(that.value)) return false;
        return attributes.equals(that.attributes);
    }
    
    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + value.hashCode();
        result = 31 * result + position;
        result = 31 * result + attributes.hashCode();
        return result;
    }
} 