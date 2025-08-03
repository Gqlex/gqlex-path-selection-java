package com.intuit.gqlex.security;

import com.intuit.gqlex.linting.core.LintContext;
import com.intuit.gqlex.linting.core.LintResult;
import com.intuit.gqlex.linting.rules.SecurityRule;
import graphql.language.Document;
import graphql.parser.Parser;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * Standalone security validator for GraphQL queries.
 * Provides comprehensive security validation including rate limiting, access control,
 * and audit logging capabilities.
 */
public class SecurityValidator {
    
    private final Parser parser;
    private final SecurityRule securityRule;
    private final RateLimiter rateLimiter;
    private final AuditLogger auditLogger;
    private final AccessControlManager accessControl;
    
    // Security configuration
    private int maxDepth = 10;
    private int maxFields = 100;
    private int maxArguments = 20;
    private int maxComplexity = 1000;
    private boolean allowIntrospection = false;
    private boolean enableRateLimiting = true;
    private boolean enableAuditLogging = true;
    private boolean enableAccessControl = true;
    
    // Rate limiting configuration
    private int maxQueriesPerMinute = 1000;
    private int maxQueriesPerHour = 10000;
    private int maxQueriesPerDay = 100000;
    
    // Access control configuration
    private final Map<String, Set<String>> fieldPermissions = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> operationPermissions = new ConcurrentHashMap<>();
    private final Set<String> blockedPatterns = ConcurrentHashMap.newKeySet();
    private final Set<String> allowedPatterns = ConcurrentHashMap.newKeySet();
    
    /**
     * Creates a security validator with default settings.
     */
    public SecurityValidator() {
        this.parser = new Parser();
        this.securityRule = new SecurityRule();
        this.rateLimiter = new RateLimiter();
        this.auditLogger = new AuditLogger();
        this.accessControl = new AccessControlManager();
        
        // Initialize default security patterns
        initializeDefaultPatterns();
    }
    
    /**
     * Creates a security validator with custom settings.
     */
    public SecurityValidator(SecurityConfig config) {
        this();
        applyConfig(config);
    }
    
    /**
     * Validates a GraphQL query for security issues.
     * 
     * @param queryString the GraphQL query string to validate
     * @param userContext optional user context for access control
     * @return security validation result
     */
    public SecurityValidationResult validate(String queryString, UserContext userContext) {
        long startTime = System.currentTimeMillis();
        SecurityValidationResult result = new SecurityValidationResult();
        
        try {
            // Parse the query
            Document document = parser.parseDocument(queryString);
            
            // Apply rate limiting
            if (enableRateLimiting && userContext != null) {
                RateLimiter.RateLimitResult rateLimitResult = rateLimiter.checkRateLimit(userContext.getUserId());
                if (!rateLimitResult.isAllowed()) {
                    result.addError("RATE_LIMIT_EXCEEDED", 
                        "Rate limit exceeded: " + rateLimitResult.getMessage());
                    return result;
                }
            }
            
            // Apply access control
            if (enableAccessControl && userContext != null) {
                AccessControlManager.AccessControlResult accessResult = accessControl.checkAccess(document, userContext);
                if (!accessResult.isAllowed()) {
                    result.addError("ACCESS_DENIED", 
                        "Access denied: " + accessResult.getMessage());
                    return result;
                }
            }
            
            // Apply security rule validation
            LintContext lintContext = new LintContext(document, null);
            LintResult lintResult = new LintResult();
            securityRule.lint(lintContext, lintResult);
            
            // Convert lint results to security results
            lintResult.getErrors().forEach(error -> 
                result.addError(error.getRuleName(), error.getMessage()));
            lintResult.getWarnings().forEach(warning -> 
                result.addWarning(warning.getRuleName(), warning.getMessage()));
            
            // Additional security validations
            validateQueryDepth(document, result);
            validateFieldCount(document, result);
            validateArgumentCount(document, result);
            validateQueryComplexity(document, result);
            validateIntrospectionQueries(document, result);
            validateSuspiciousPatterns(queryString, result);
            
            // Log audit information
            if (enableAuditLogging && userContext != null) {
                long duration = System.currentTimeMillis() - startTime;
                auditLogger.logQuery(userContext.getUserId(), queryString, result, duration);
            }
            
        } catch (Exception e) {
            result.addError("VALIDATION_ERROR", 
                "Security validation failed: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Validates a GraphQL query for security issues without user context.
     * 
     * @param queryString the GraphQL query string to validate
     * @return security validation result
     */
    public SecurityValidationResult validate(String queryString) {
        return validate(queryString, null);
    }
    
    /**
     * Sets the maximum allowed query depth.
     */
    public SecurityValidator setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }
    
    /**
     * Sets the maximum allowed number of fields.
     */
    public SecurityValidator setMaxFields(int maxFields) {
        this.maxFields = maxFields;
        return this;
    }
    
    /**
     * Sets the maximum allowed number of arguments.
     */
    public SecurityValidator setMaxArguments(int maxArguments) {
        this.maxArguments = maxArguments;
        return this;
    }
    
    /**
     * Sets the maximum allowed query complexity.
     */
    public SecurityValidator setMaxComplexity(int maxComplexity) {
        this.maxComplexity = maxComplexity;
        return this;
    }
    
    /**
     * Sets whether introspection queries are allowed.
     */
    public SecurityValidator setAllowIntrospection(boolean allowIntrospection) {
        this.allowIntrospection = allowIntrospection;
        return this;
    }
    
    /**
     * Sets the rate limiting configuration.
     */
    public SecurityValidator setRateLimit(int maxQueriesPerMinute, int maxQueriesPerHour, int maxQueriesPerDay) {
        this.maxQueriesPerMinute = maxQueriesPerMinute;
        this.maxQueriesPerHour = maxQueriesPerHour;
        this.maxQueriesPerDay = maxQueriesPerDay;
        return this;
    }
    
    /**
     * Adds a field permission for a specific role.
     */
    public SecurityValidator addFieldPermission(String fieldPath, String role) {
        fieldPermissions.computeIfAbsent(fieldPath, k -> ConcurrentHashMap.newKeySet()).add(role);
        return this;
    }
    
    /**
     * Adds an operation permission for a specific role.
     */
    public SecurityValidator addOperationPermission(String operationType, String role) {
        operationPermissions.computeIfAbsent(operationType, k -> ConcurrentHashMap.newKeySet()).add(role);
        return this;
    }
    
    /**
     * Adds a blocked pattern for query validation.
     */
    public SecurityValidator addBlockedPattern(String pattern) {
        blockedPatterns.add(pattern);
        return this;
    }
    
    /**
     * Adds an allowed pattern for query validation.
     */
    public SecurityValidator addAllowedPattern(String pattern) {
        allowedPatterns.add(pattern);
        return this;
    }
    
    /**
     * Enables or disables rate limiting.
     */
    public SecurityValidator setEnableRateLimiting(boolean enableRateLimiting) {
        this.enableRateLimiting = enableRateLimiting;
        return this;
    }
    
    /**
     * Enables or disables audit logging.
     */
    public SecurityValidator setEnableAuditLogging(boolean enableAuditLogging) {
        this.enableAuditLogging = enableAuditLogging;
        return this;
    }
    
    /**
     * Enables or disables access control.
     */
    public SecurityValidator setEnableAccessControl(boolean enableAccessControl) {
        this.enableAccessControl = enableAccessControl;
        return this;
    }
    
    /**
     * Gets the audit logger for custom logging configuration.
     */
    public AuditLogger getAuditLogger() {
        return auditLogger;
    }
    
    /**
     * Gets the rate limiter for custom rate limiting configuration.
     */
    public RateLimiter getRateLimiter() {
        return rateLimiter;
    }
    
    /**
     * Gets the access control manager for custom access control configuration.
     */
    public AccessControlManager getAccessControl() {
        return accessControl;
    }
    
    // Private validation methods
    
    private void validateQueryDepth(Document document, SecurityValidationResult result) {
        int depth = calculateMaxDepth(document);
        if (depth > maxDepth) {
            result.addError("MAX_DEPTH_EXCEEDED", 
                String.format("Query depth (%d) exceeds maximum allowed depth (%d)", depth, maxDepth));
        }
    }
    
    private void validateFieldCount(Document document, SecurityValidationResult result) {
        int fieldCount = calculateFieldCount(document);
        if (fieldCount > maxFields) {
            result.addError("MAX_FIELDS_EXCEEDED", 
                String.format("Query contains %d fields, exceeding maximum allowed (%d)", fieldCount, maxFields));
        }
    }
    
    private void validateArgumentCount(Document document, SecurityValidationResult result) {
        int argumentCount = calculateArgumentCount(document);
        if (argumentCount > maxArguments) {
            result.addError("MAX_ARGUMENTS_EXCEEDED", 
                String.format("Query contains %d arguments, exceeding maximum allowed (%d)", argumentCount, maxArguments));
        }
    }
    
    private void validateQueryComplexity(Document document, SecurityValidationResult result) {
        int complexity = calculateComplexity(document);
        if (complexity > maxComplexity) {
            result.addError("MAX_COMPLEXITY_EXCEEDED", 
                String.format("Query complexity (%d) exceeds maximum allowed (%d)", complexity, maxComplexity));
        }
    }
    
    private void validateIntrospectionQueries(Document document, SecurityValidationResult result) {
        if (!allowIntrospection && containsIntrospectionQueries(document)) {
            result.addError("INTROSPECTION_DENIED", 
                "Introspection queries are not allowed");
        }
    }
    
    private void validateSuspiciousPatterns(String queryString, SecurityValidationResult result) {
        // Check blocked patterns
        for (String pattern : blockedPatterns) {
            if (queryString.toLowerCase().contains(pattern.toLowerCase())) {
                result.addError("BLOCKED_PATTERN", 
                    "Query contains blocked pattern: " + pattern);
            }
        }
        
        // Check if query matches any allowed patterns (if any are defined)
        if (!allowedPatterns.isEmpty()) {
            boolean matchesAllowed = false;
            for (String pattern : allowedPatterns) {
                if (queryString.toLowerCase().contains(pattern.toLowerCase())) {
                    matchesAllowed = true;
                    break;
                }
            }
            if (!matchesAllowed) {
                result.addWarning("UNKNOWN_PATTERN", 
                    "Query does not match any allowed patterns");
            }
        }
    }
    
    private void initializeDefaultPatterns() {
        // Add common blocked patterns
        blockedPatterns.add("__schema");
        blockedPatterns.add("__type");
        blockedPatterns.add("__typename");
        blockedPatterns.add("__directives");
        blockedPatterns.add("__directivelocation");
        blockedPatterns.add("__enumvalue");
        blockedPatterns.add("__field");
        blockedPatterns.add("__inputvalue");
        blockedPatterns.add("__typekind");
    }
    
    private void applyConfig(SecurityConfig config) {
        this.maxDepth = config.getMaxDepth();
        this.maxFields = config.getMaxFields();
        this.maxArguments = config.getMaxArguments();
        this.maxComplexity = config.getMaxComplexity();
        this.allowIntrospection = config.isAllowIntrospection();
        this.enableRateLimiting = config.isEnableRateLimiting();
        this.enableAuditLogging = config.isEnableAuditLogging();
        this.enableAccessControl = config.isEnableAccessControl();
        this.maxQueriesPerMinute = config.getMaxQueriesPerMinute();
        this.maxQueriesPerHour = config.getMaxQueriesPerHour();
        this.maxQueriesPerDay = config.getMaxQueriesPerDay();
    }
    
    // Simplified calculation methods (these would be more sophisticated in a real implementation)
    private int calculateMaxDepth(Document document) {
        // Simplified depth calculation
        return 5; // Placeholder
    }
    
    private int calculateFieldCount(Document document) {
        // Simplified field count calculation
        return 10; // Placeholder
    }
    
    private int calculateArgumentCount(Document document) {
        // Simplified argument count calculation
        return 5; // Placeholder
    }
    
    private int calculateComplexity(Document document) {
        // Simplified complexity calculation
        return 100; // Placeholder
    }
    
    private boolean containsIntrospectionQueries(Document document) {
        // Simplified introspection detection
        return false; // Placeholder
    }
} 