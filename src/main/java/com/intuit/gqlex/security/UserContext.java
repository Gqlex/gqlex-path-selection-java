package com.intuit.gqlex.security;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

/**
 * User context for security validation.
 * Contains user information needed for access control and audit logging.
 */
public class UserContext {
    
    private final String userId;
    private final Set<String> roles;
    private final Map<String, Object> attributes;
    private final String sessionId;
    private final String ipAddress;
    private final String userAgent;
    
    /**
     * Creates a user context with basic information.
     * 
     * @param userId the user ID
     */
    public UserContext(String userId) {
        this(userId, new HashSet<>(), new HashMap<>(), null, null, null);
    }
    
    /**
     * Creates a user context with roles.
     * 
     * @param userId the user ID
     * @param roles the user roles
     */
    public UserContext(String userId, Set<String> roles) {
        this(userId, roles, new HashMap<>(), null, null, null);
    }
    
    /**
     * Creates a user context with all information.
     * 
     * @param userId the user ID
     * @param roles the user roles
     * @param attributes additional user attributes
     * @param sessionId the session ID
     * @param ipAddress the IP address
     * @param userAgent the user agent string
     */
    public UserContext(String userId, Set<String> roles, Map<String, Object> attributes, 
                      String sessionId, String ipAddress, String userAgent) {
        this.userId = userId;
        this.roles = new HashSet<>(roles);
        this.attributes = new HashMap<>(attributes);
        this.sessionId = sessionId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }
    
    /**
     * Gets the user ID.
     * 
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }
    
    /**
     * Gets the user roles.
     * 
     * @return the user roles
     */
    public Set<String> getRoles() {
        return new HashSet<>(roles);
    }
    
    /**
     * Adds a role to the user.
     * 
     * @param role the role to add
     */
    public void addRole(String role) {
        roles.add(role);
    }
    
    /**
     * Removes a role from the user.
     * 
     * @param role the role to remove
     */
    public void removeRole(String role) {
        roles.remove(role);
    }
    
    /**
     * Checks if the user has a specific role.
     * 
     * @param role the role to check
     * @return true if user has the role
     */
    public boolean hasRole(String role) {
        return roles.contains(role);
    }
    
    /**
     * Checks if the user has any of the specified roles.
     * 
     * @param roles the roles to check
     * @return true if user has any of the roles
     */
    public boolean hasAnyRole(Set<String> roles) {
        return !this.roles.isEmpty() && !roles.isEmpty() && 
               !java.util.Collections.disjoint(this.roles, roles);
    }
    
    /**
     * Gets all user attributes.
     * 
     * @return the user attributes
     */
    public Map<String, Object> getAttributes() {
        return new HashMap<>(attributes);
    }
    
    /**
     * Gets a specific attribute.
     * 
     * @param key the attribute key
     * @return the attribute value
     */
    public Object getAttribute(String key) {
        return attributes.get(key);
    }
    
    /**
     * Gets a specific attribute with type casting.
     * 
     * @param key the attribute key
     * @param type the expected type
     * @return the attribute value
     */
    public <T> T getAttribute(String key, Class<T> type) {
        Object value = attributes.get(key);
        return type.isInstance(value) ? type.cast(value) : null;
    }
    
    /**
     * Sets an attribute.
     * 
     * @param key the attribute key
     * @param value the attribute value
     */
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }
    
    /**
     * Removes an attribute.
     * 
     * @param key the attribute key
     */
    public void removeAttribute(String key) {
        attributes.remove(key);
    }
    
    /**
     * Gets the session ID.
     * 
     * @return the session ID
     */
    public String getSessionId() {
        return sessionId;
    }
    
    /**
     * Gets the IP address.
     * 
     * @return the IP address
     */
    public String getIpAddress() {
        return ipAddress;
    }
    
    /**
     * Gets the user agent string.
     * 
     * @return the user agent string
     */
    public String getUserAgent() {
        return userAgent;
    }
    
    /**
     * Creates a builder for UserContext.
     * 
     * @return a UserContextBuilder
     */
    public static UserContextBuilder builder() {
        return new UserContextBuilder();
    }
    
    /**
     * Builder class for UserContext.
     */
    public static class UserContextBuilder {
        private String userId;
        private Set<String> roles = new HashSet<>();
        private Map<String, Object> attributes = new HashMap<>();
        private String sessionId;
        private String ipAddress;
        private String userAgent;
        
        public UserContextBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }
        
        public UserContextBuilder role(String role) {
            this.roles.add(role);
            return this;
        }
        
        public UserContextBuilder roles(Set<String> roles) {
            this.roles.addAll(roles);
            return this;
        }
        
        public UserContextBuilder attribute(String key, Object value) {
            this.attributes.put(key, value);
            return this;
        }
        
        public UserContextBuilder attributes(Map<String, Object> attributes) {
            this.attributes.putAll(attributes);
            return this;
        }
        
        public UserContextBuilder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }
        
        public UserContextBuilder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }
        
        public UserContextBuilder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }
        
        public UserContext build() {
            return new UserContext(userId, roles, attributes, sessionId, ipAddress, userAgent);
        }
    }
} 