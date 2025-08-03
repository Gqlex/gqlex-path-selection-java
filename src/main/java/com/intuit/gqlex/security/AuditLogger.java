package com.intuit.gqlex.security;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Comprehensive audit logger for GraphQL queries.
 * Provides query logging, performance monitoring, security event logging,
 * and compliance reporting capabilities.
 */
public class AuditLogger {
    
    private static final Logger logger = Logger.getLogger(AuditLogger.class.getName());
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    // Configuration
    private boolean enableQueryLogging = true;
    private boolean enablePerformanceLogging = true;
    private boolean enableSecurityLogging = true;
    private boolean enableComplianceLogging = true;
    private int maxLogEntries = 10000;
    private LogLevel logLevel = LogLevel.INFO;
    
    // In-memory storage for recent logs (in production, this would be a database)
    private final ConcurrentLinkedQueue<AuditLogEntry> recentLogs = new ConcurrentLinkedQueue<>();
    private final AtomicLong totalQueriesLogged = new AtomicLong(0);
    private final AtomicLong totalErrorsLogged = new AtomicLong(0);
    private final AtomicLong totalWarningsLogged = new AtomicLong(0);
    
    /**
     * Logs a GraphQL query with its validation result and performance metrics.
     * 
     * @param userId the user ID making the query
     * @param queryString the GraphQL query string
     * @param validationResult the security validation result
     * @param executionTimeMs the query execution time in milliseconds
     */
    public void logQuery(String userId, String queryString, SecurityValidationResult validationResult, long executionTimeMs) {
        if (!enableQueryLogging) {
            return;
        }
        
        AuditLogEntry entry = new AuditLogEntry();
        entry.setTimestamp(LocalDateTime.now());
        entry.setUserId(userId);
        entry.setQueryString(truncateQuery(queryString));
        entry.setValidationResult(validationResult);
        entry.setExecutionTimeMs(executionTimeMs);
        entry.setLogType(SecurityEnums.LogType.QUERY);
        
        addLogEntry(entry);
        
        if (logLevel.ordinal() <= LogLevel.INFO.ordinal()) {
            logger.info(String.format("Query logged - User: %s, Valid: %s, Time: %dms, Errors: %d, Warnings: %d",
                userId, validationResult.isValid(), executionTimeMs, 
                validationResult.getErrorCount(), validationResult.getWarningCount()));
        }
    }
    
    /**
     * Logs a security event.
     * 
     * @param userId the user ID involved
     * @param eventType the type of security event
     * @param description the event description
     * @param severity the event severity
     */
    public void logSecurityEvent(String userId, SecurityEnums.SecurityEventType eventType, String description, SecurityEnums.SecuritySeverity severity) {
        if (!enableSecurityLogging) {
            return;
        }
        
        AuditLogEntry entry = new AuditLogEntry();
        entry.setTimestamp(LocalDateTime.now());
        entry.setUserId(userId);
        entry.setEventType(eventType);
        entry.setDescription(description);
        entry.setSeverity(severity);
        entry.setLogType(SecurityEnums.LogType.SECURITY);
        
        addLogEntry(entry);
        
        Level javaLogLevel = severity == SecurityEnums.SecuritySeverity.HIGH ? Level.SEVERE : 
                           severity == SecurityEnums.SecuritySeverity.MEDIUM ? Level.WARNING : Level.INFO;
        
        logger.log(javaLogLevel, String.format("Security event - User: %s, Type: %s, Severity: %s, Description: %s",
            userId, eventType, severity, description));
    }
    
    /**
     * Logs a performance event.
     * 
     * @param userId the user ID involved
     * @param queryString the GraphQL query string
     * @param performanceMetrics the performance metrics
     */
    public void logPerformanceEvent(String userId, String queryString, PerformanceMetrics performanceMetrics) {
        if (!enablePerformanceLogging) {
            return;
        }
        
        AuditLogEntry entry = new AuditLogEntry();
        entry.setTimestamp(LocalDateTime.now());
        entry.setUserId(userId);
        entry.setQueryString(truncateQuery(queryString));
        entry.setPerformanceMetrics(performanceMetrics);
        entry.setLogType(SecurityEnums.LogType.PERFORMANCE);
        
        addLogEntry(entry);
        
        if (logLevel.ordinal() <= LogLevel.INFO.ordinal()) {
            logger.info(String.format("Performance event - User: %s, Depth: %d, Fields: %d, Complexity: %d, Time: %dms",
                userId, performanceMetrics.getDepth(), performanceMetrics.getFieldCount(),
                performanceMetrics.getComplexity(), performanceMetrics.getExecutionTimeMs()));
        }
    }
    
    /**
     * Logs a compliance event.
     * 
     * @param userId the user ID involved
     * @param complianceType the type of compliance event
     * @param description the compliance description
     * @param isCompliant whether the event is compliant
     */
    public void logComplianceEvent(String userId, SecurityEnums.ComplianceType complianceType, String description, boolean isCompliant) {
        if (!enableComplianceLogging) {
            return;
        }
        
        AuditLogEntry entry = new AuditLogEntry();
        entry.setTimestamp(LocalDateTime.now());
        entry.setUserId(userId);
        entry.setComplianceType(complianceType);
        entry.setDescription(description);
        entry.setCompliant(isCompliant);
        entry.setLogType(SecurityEnums.LogType.COMPLIANCE);
        
        addLogEntry(entry);
        
        Level javaLogLevel = isCompliant ? Level.INFO : Level.WARNING;
        logger.log(javaLogLevel, String.format("Compliance event - User: %s, Type: %s, Compliant: %s, Description: %s",
            userId, complianceType, isCompliant, description));
    }
    
    /**
     * Logs a rate limiting event.
     * 
     * @param userId the user ID involved
     * @param rateLimitType the type of rate limit exceeded
     * @param currentCount the current count
     * @param limit the limit that was exceeded
     */
    public void logRateLimitEvent(String userId, SecurityEnums.RateLimitType rateLimitType, int currentCount, int limit) {
        logSecurityEvent(userId, SecurityEnums.SecurityEventType.RATE_LIMIT_EXCEEDED, 
            String.format("Rate limit exceeded: %s (current: %d, limit: %d)", rateLimitType, currentCount, limit),
            SecurityEnums.SecuritySeverity.MEDIUM);
    }
    
    /**
     * Logs an access control event.
     * 
     * @param userId the user ID involved
     * @param operation the operation that was denied
     * @param reason the reason for denial
     */
    public void logAccessControlEvent(String userId, String operation, String reason) {
        logSecurityEvent(userId, SecurityEnums.SecurityEventType.ACCESS_DENIED, 
            String.format("Access denied for operation '%s': %s", operation, reason),
            SecurityEnums.SecuritySeverity.HIGH);
    }
    
    /**
     * Gets recent log entries.
     * 
     * @param limit the maximum number of entries to return
     * @return array of recent log entries
     */
    public AuditLogEntry[] getRecentLogs(int limit) {
        return recentLogs.stream()
            .limit(limit)
            .toArray(AuditLogEntry[]::new);
    }
    
    /**
     * Gets log statistics.
     * 
     * @return log statistics
     */
    public LogStatistics getLogStatistics() {
        LogStatistics stats = new LogStatistics();
        stats.setTotalQueriesLogged(totalQueriesLogged.get());
        stats.setTotalErrorsLogged(totalErrorsLogged.get());
        stats.setTotalWarningsLogged(totalWarningsLogged.get());
        stats.setRecentLogCount(recentLogs.size());
        return stats;
    }
    
    /**
     * Generates a compliance report.
     * 
     * @param startDate the start date for the report
     * @param endDate the end date for the report
     * @return compliance report
     */
    public ComplianceReport generateComplianceReport(LocalDateTime startDate, LocalDateTime endDate) {
        ComplianceReport report = new ComplianceReport();
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setGeneratedAt(LocalDateTime.now());
        
        // Count events by type
        long securityEvents = recentLogs.stream()
            .filter(entry -> entry.getLogType() == SecurityEnums.LogType.SECURITY && 
                   entry.getTimestamp().isAfter(startDate) && 
                   entry.getTimestamp().isBefore(endDate))
            .count();
        
        long complianceEvents = recentLogs.stream()
            .filter(entry -> entry.getLogType() == SecurityEnums.LogType.COMPLIANCE && 
                   entry.getTimestamp().isAfter(startDate) && 
                   entry.getTimestamp().isBefore(endDate))
            .count();
        
        long performanceEvents = recentLogs.stream()
            .filter(entry -> entry.getLogType() == SecurityEnums.LogType.PERFORMANCE && 
                   entry.getTimestamp().isAfter(startDate) && 
                   entry.getTimestamp().isBefore(endDate))
            .count();
        
        report.setSecurityEventCount(securityEvents);
        report.setComplianceEventCount(complianceEvents);
        report.setPerformanceEventCount(performanceEvents);
        
        return report;
    }
    
    /**
     * Clears recent logs.
     */
    public void clearRecentLogs() {
        recentLogs.clear();
    }
    
    /**
     * Sets the log level.
     */
    public AuditLogger setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
        return this;
    }
    
    /**
     * Enables or disables query logging.
     */
    public AuditLogger setEnableQueryLogging(boolean enableQueryLogging) {
        this.enableQueryLogging = enableQueryLogging;
        return this;
    }
    
    /**
     * Enables or disables performance logging.
     */
    public AuditLogger setEnablePerformanceLogging(boolean enablePerformanceLogging) {
        this.enablePerformanceLogging = enablePerformanceLogging;
        return this;
    }
    
    /**
     * Enables or disables security logging.
     */
    public AuditLogger setEnableSecurityLogging(boolean enableSecurityLogging) {
        this.enableSecurityLogging = enableSecurityLogging;
        return this;
    }
    
    /**
     * Enables or disables compliance logging.
     */
    public AuditLogger setEnableComplianceLogging(boolean enableComplianceLogging) {
        this.enableComplianceLogging = enableComplianceLogging;
        return this;
    }
    
    /**
     * Sets the maximum number of log entries to keep in memory.
     */
    public AuditLogger setMaxLogEntries(int maxLogEntries) {
        this.maxLogEntries = maxLogEntries;
        return this;
    }
    
    // Private helper methods
    
    private void addLogEntry(AuditLogEntry entry) {
        recentLogs.offer(entry);
        
        // Remove old entries if we exceed the limit
        while (recentLogs.size() > maxLogEntries) {
            recentLogs.poll();
        }
        
        // Update counters
        if (entry.getLogType() == SecurityEnums.LogType.QUERY) {
            totalQueriesLogged.incrementAndGet();
            
            if (entry.getValidationResult() != null) {
                totalErrorsLogged.addAndGet(entry.getValidationResult().getErrorCount());
                totalWarningsLogged.addAndGet(entry.getValidationResult().getWarningCount());
            }
        }
    }
    
    private String truncateQuery(String queryString) {
        if (queryString == null) {
            return "";
        }
        return queryString.length() > 1000 ? queryString.substring(0, 1000) + "..." : queryString;
    }
    
    // Enums and inner classes
    
    public enum LogLevel {
        DEBUG, INFO, WARNING, ERROR
    }
} 