package com.intuit.gqlex.security;

import graphql.language.Document;
import graphql.language.Field;
import graphql.language.OperationDefinition;
import graphql.language.SelectionSet;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Access control manager for GraphQL queries.
 * Provides field-level and operation-level access control based on user roles and permissions.
 */
public class AccessControlManager {
    
    // Access control configuration
    private final Map<String, Set<String>> fieldPermissions = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> operationPermissions = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> userRoles = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> rolePermissions = new ConcurrentHashMap<>();
    
    // Default permissions
    private Set<String> defaultAllowedFields = ConcurrentHashMap.newKeySet();
    private Set<String> defaultAllowedOperations = ConcurrentHashMap.newKeySet();
    private Set<String> defaultDeniedFields = ConcurrentHashMap.newKeySet();
    private Set<String> defaultDeniedOperations = ConcurrentHashMap.newKeySet();
    
    /**
     * Creates an access control manager with default settings.
     */
    public AccessControlManager() {
        initializeDefaultPermissions();
    }
    
    /**
     * Checks if a user has access to a GraphQL document.
     * 
     * @param document the GraphQL document to check
     * @param userContext the user context containing user information
     * @return access control result
     */
    public AccessControlResult checkAccess(Document document, UserContext userContext) {
        if (document == null) {
            return AccessControlResult.allowed("No document to check");
        }
        
        if (userContext == null) {
            return AccessControlResult.denied("No user context provided");
        }
        
        String userId = userContext.getUserId();
        Set<String> userRoles = getUserRoles(userId);
        
        // Check operation-level permissions
        for (OperationDefinition operation : document.getDefinitionsOfType(OperationDefinition.class)) {
            String operationType = operation.getOperation().name();
            
            if (!hasOperationPermission(userRoles, operationType)) {
                return AccessControlResult.denied(
                    String.format("User does not have permission for operation type: %s", operationType));
            }
        }
        
        // Check field-level permissions
        for (OperationDefinition operation : document.getDefinitionsOfType(OperationDefinition.class)) {
            if (operation.getSelectionSet() != null) {
                AccessControlResult fieldResult = checkFieldAccess(operation.getSelectionSet(), userRoles);
                if (!fieldResult.isAllowed()) {
                    return fieldResult;
                }
            }
        }
        
        return AccessControlResult.allowed("Access granted");
    }
    
    /**
     * Checks field-level access permissions.
     * 
     * @param selectionSet the selection set to check
     * @param userRoles the user's roles
     * @return access control result
     */
    private AccessControlResult checkFieldAccess(SelectionSet selectionSet, Set<String> userRoles) {
        if (selectionSet == null) {
            return AccessControlResult.allowed("No selection set to check");
        }
        
        for (graphql.language.Selection selection : selectionSet.getSelections()) {
            if (selection instanceof Field) {
                Field field = (Field) selection;
                String fieldPath = buildFieldPath(field);
                
                // Check if field is explicitly denied
                if (isFieldDenied(fieldPath, userRoles)) {
                    return AccessControlResult.denied(
                        String.format("Access denied to field: %s", fieldPath));
                }
                
                // Check if field is explicitly allowed
                if (!isFieldAllowed(fieldPath, userRoles)) {
                    return AccessControlResult.denied(
                        String.format("No permission for field: %s", fieldPath));
                }
                
                // Recursively check nested fields
                if (field.getSelectionSet() != null) {
                    AccessControlResult nestedResult = checkFieldAccess(field.getSelectionSet(), userRoles);
                    if (!nestedResult.isAllowed()) {
                        return nestedResult;
                    }
                }
            }
        }
        
        return AccessControlResult.allowed("Field access granted");
    }
    
    /**
     * Builds a field path for permission checking.
     * 
     * @param field the field to build path for
     * @return field path string
     */
    private String buildFieldPath(Field field) {
        StringBuilder path = new StringBuilder();
        Field current = field;
        
        while (current != null) {
            if (path.length() > 0) {
                path.insert(0, ".");
            }
            path.insert(0, current.getName());
            current = getParentField(current);
        }
        
        return path.toString();
    }
    
    /**
     * Gets the parent field (simplified implementation).
     * 
     * @param field the field to get parent for
     * @return parent field or null
     */
    private Field getParentField(Field field) {
        // This is a simplified implementation
        // In a real implementation, you would traverse the AST to find the parent
        return null;
    }
    
    /**
     * Checks if a user has permission for an operation type.
     * 
     * @param userRoles the user's roles
     * @param operationType the operation type to check
     * @return true if user has permission
     */
    private boolean hasOperationPermission(Set<String> userRoles, String operationType) {
        // Check default denied operations
        if (defaultDeniedOperations.contains(operationType)) {
            return false;
        }
        
        // Check default allowed operations
        if (defaultAllowedOperations.contains(operationType)) {
            return true;
        }
        
        // Check role-based permissions
        for (String role : userRoles) {
            Set<String> rolePerms = rolePermissions.get(role);
            if (rolePerms != null && rolePerms.contains("operation:" + operationType)) {
                return true;
            }
        }
        
        // Check explicit operation permissions
        Set<String> allowedRoles = operationPermissions.get(operationType);
        if (allowedRoles != null) {
            return !Collections.disjoint(userRoles, allowedRoles);
        }
        
        return false;
    }
    
    /**
     * Checks if a field is allowed for the user's roles.
     * 
     * @param fieldPath the field path to check
     * @param userRoles the user's roles
     * @return true if field is allowed
     */
    private boolean isFieldAllowed(String fieldPath, Set<String> userRoles) {
        // Check default denied fields
        if (defaultDeniedFields.contains(fieldPath)) {
            return false;
        }
        
        // Check default allowed fields
        if (defaultAllowedFields.contains(fieldPath)) {
            return true;
        }
        
        // Check role-based permissions
        for (String role : userRoles) {
            Set<String> rolePerms = rolePermissions.get(role);
            if (rolePerms != null && rolePerms.contains("field:" + fieldPath)) {
                return true;
            }
        }
        
        // Check explicit field permissions
        Set<String> allowedRoles = fieldPermissions.get(fieldPath);
        if (allowedRoles != null) {
            return !Collections.disjoint(userRoles, allowedRoles);
        }
        
        return false;
    }
    
    /**
     * Checks if a field is denied for the user's roles.
     * 
     * @param fieldPath the field path to check
     * @param userRoles the user's roles
     * @return true if field is denied
     */
    private boolean isFieldDenied(String fieldPath, Set<String> userRoles) {
        // Check for explicit deny permissions
        for (String role : userRoles) {
            Set<String> rolePerms = rolePermissions.get(role);
            if (rolePerms != null && rolePerms.contains("deny:field:" + fieldPath)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Gets the roles for a user.
     * 
     * @param userId the user ID
     * @return set of user roles
     */
    private Set<String> getUserRoles(String userId) {
        Set<String> roles = userRoles.get(userId);
        return roles != null ? roles : Collections.emptySet();
    }
    
    /**
     * Adds a field permission for a specific role.
     * 
     * @param fieldPath the field path
     * @param role the role to grant permission to
     */
    public AccessControlManager addFieldPermission(String fieldPath, String role) {
        fieldPermissions.computeIfAbsent(fieldPath, k -> ConcurrentHashMap.newKeySet()).add(role);
        return this;
    }
    
    /**
     * Adds an operation permission for a specific role.
     * 
     * @param operationType the operation type
     * @param role the role to grant permission to
     */
    public AccessControlManager addOperationPermission(String operationType, String role) {
        operationPermissions.computeIfAbsent(operationType, k -> ConcurrentHashMap.newKeySet()).add(role);
        return this;
    }
    
    /**
     * Assigns roles to a user.
     * 
     * @param userId the user ID
     * @param roles the roles to assign
     */
    public AccessControlManager assignUserRoles(String userId, Set<String> roles) {
        userRoles.put(userId, new HashSet<>(roles));
        return this;
    }
    
    /**
     * Assigns permissions to a role.
     * 
     * @param role the role
     * @param permissions the permissions to assign
     */
    public AccessControlManager assignRolePermissions(String role, Set<String> permissions) {
        rolePermissions.put(role, new HashSet<>(permissions));
        return this;
    }
    
    /**
     * Adds a default allowed field.
     * 
     * @param fieldPath the field path
     */
    public AccessControlManager addDefaultAllowedField(String fieldPath) {
        defaultAllowedFields.add(fieldPath);
        return this;
    }
    
    /**
     * Adds a default allowed operation.
     * 
     * @param operationType the operation type
     */
    public AccessControlManager addDefaultAllowedOperation(String operationType) {
        defaultAllowedOperations.add(operationType);
        return this;
    }
    
    /**
     * Adds a default denied field.
     * 
     * @param fieldPath the field path
     */
    public AccessControlManager addDefaultDeniedField(String fieldPath) {
        defaultDeniedFields.add(fieldPath);
        return this;
    }
    
    /**
     * Adds a default denied operation.
     * 
     * @param operationType the operation type
     */
    public AccessControlManager addDefaultDeniedOperation(String operationType) {
        defaultDeniedOperations.add(operationType);
        return this;
    }
    
    /**
     * Gets all field permissions.
     * 
     * @return map of field permissions
     */
    public Map<String, Set<String>> getFieldPermissions() {
        return new HashMap<>(fieldPermissions);
    }
    
    /**
     * Gets all operation permissions.
     * 
     * @return map of operation permissions
     */
    public Map<String, Set<String>> getOperationPermissions() {
        return new HashMap<>(operationPermissions);
    }
    
    /**
     * Gets all user roles.
     * 
     * @return map of user roles
     */
    public Map<String, Set<String>> getUserRoles() {
        return new HashMap<>(userRoles);
    }
    
    /**
     * Gets all role permissions.
     * 
     * @return map of role permissions
     */
    public Map<String, Set<String>> getRolePermissions() {
        return new HashMap<>(rolePermissions);
    }
    
    /**
     * Initializes default permissions.
     */
    private void initializeDefaultPermissions() {
        // Default allowed operations
        defaultAllowedOperations.add("query");
        
        // Default denied operations (none by default)
        
        // Default allowed fields (none by default - must be explicitly granted)
        
        // Default denied fields (none by default)
    }
    
    /**
     * Result of an access control check.
     */
    public static class AccessControlResult {
        private final boolean allowed;
        private final String message;
        private final String deniedField;
        private final String deniedOperation;
        
        private AccessControlResult(boolean allowed, String message, String deniedField, String deniedOperation) {
            this.allowed = allowed;
            this.message = message;
            this.deniedField = deniedField;
            this.deniedOperation = deniedOperation;
        }
        
        public static AccessControlResult allowed(String message) {
            return new AccessControlResult(true, message, null, null);
        }
        
        public static AccessControlResult denied(String message) {
            return new AccessControlResult(false, message, null, null);
        }
        
        public static AccessControlResult deniedField(String fieldPath, String message) {
            return new AccessControlResult(false, message, fieldPath, null);
        }
        
        public static AccessControlResult deniedOperation(String operationType, String message) {
            return new AccessControlResult(false, message, null, operationType);
        }
        
        public boolean isAllowed() {
            return allowed;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getDeniedField() {
            return deniedField;
        }
        
        public String getDeniedOperation() {
            return deniedOperation;
        }
    }
} 