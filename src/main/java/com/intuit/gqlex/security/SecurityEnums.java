package com.intuit.gqlex.security;

/**
 * Enum classes for security components.
 * Contains all the enum types used across the security package.
 */
public class SecurityEnums {
    
    /**
     * Type of log entry.
     */
    public enum LogType {
        QUERY, SECURITY, PERFORMANCE, COMPLIANCE
    }
    
    /**
     * Type of security event.
     */
    public enum SecurityEventType {
        RATE_LIMIT_EXCEEDED, ACCESS_DENIED, SUSPICIOUS_PATTERN, 
        INTROSPECTION_ATTEMPT, COMPLEXITY_EXCEEDED, DEPTH_EXCEEDED
    }
    
    /**
     * Security severity levels.
     */
    public enum SecuritySeverity {
        LOW, MEDIUM, HIGH, CRITICAL
    }
    
    /**
     * Type of compliance event.
     */
    public enum ComplianceType {
        GDPR, HIPAA, SOX, PCI_DSS, CUSTOM
    }
    
    /**
     * Type of rate limit.
     */
    public enum RateLimitType {
        PER_MINUTE, PER_HOUR, PER_DAY
    }
} 