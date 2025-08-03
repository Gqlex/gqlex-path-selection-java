package com.intuit.gqlex.security;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * Represents an audit log entry for GraphQL operations.
 * Contains all information needed for audit trails and compliance reporting.
 */
public class AuditLogEntry {
    
    private LocalDateTime timestamp;
    private String userId;
    private String queryString;
    private SecurityValidationResult validationResult;
    private long executionTimeMs;
    private SecurityEnums.LogType logType;
    
    // Security event fields
    private SecurityEnums.SecurityEventType eventType;
    private String description;
    private SecurityEnums.SecuritySeverity severity;
    
    // Performance event fields
    private PerformanceMetrics performanceMetrics;
    
    // Compliance event fields
    private SecurityEnums.ComplianceType complianceType;
    private boolean compliant;
    
    // Additional metadata
    private Map<String, Object> metadata = new HashMap<>();
    
    /**
     * Creates an empty audit log entry.
     */
    public AuditLogEntry() {
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters and setters
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getQueryString() {
        return queryString;
    }
    
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }
    
    public SecurityValidationResult getValidationResult() {
        return validationResult;
    }
    
    public void setValidationResult(SecurityValidationResult validationResult) {
        this.validationResult = validationResult;
    }
    
    public long getExecutionTimeMs() {
        return executionTimeMs;
    }
    
    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }
    
    public SecurityEnums.LogType getLogType() {
        return logType;
    }
    
    public void setLogType(SecurityEnums.LogType logType) {
        this.logType = logType;
    }
    
    public SecurityEnums.SecurityEventType getEventType() {
        return eventType;
    }
    
    public void setEventType(SecurityEnums.SecurityEventType eventType) {
        this.eventType = eventType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public SecurityEnums.SecuritySeverity getSeverity() {
        return severity;
    }
    
    public void setSeverity(SecurityEnums.SecuritySeverity severity) {
        this.severity = severity;
    }
    
    public PerformanceMetrics getPerformanceMetrics() {
        return performanceMetrics;
    }
    
    public void setPerformanceMetrics(PerformanceMetrics performanceMetrics) {
        this.performanceMetrics = performanceMetrics;
    }
    
    public SecurityEnums.ComplianceType getComplianceType() {
        return complianceType;
    }
    
    public void setComplianceType(SecurityEnums.ComplianceType complianceType) {
        this.complianceType = complianceType;
    }
    
    public boolean isCompliant() {
        return compliant;
    }
    
    public void setCompliant(boolean compliant) {
        this.compliant = compliant;
    }
    
    public Map<String, Object> getMetadata() {
        return new HashMap<>(metadata);
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = new HashMap<>(metadata);
    }
    
    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }
    
    public Object getMetadata(String key) {
        return metadata.get(key);
    }
    
    @Override
    public String toString() {
        return String.format("AuditLogEntry{timestamp=%s, userId='%s', logType=%s, description='%s'}",
            timestamp, userId, logType, description);
    }
} 