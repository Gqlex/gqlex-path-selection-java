package com.intuit.gqlex.security;

import java.time.LocalDateTime;

/**
 * Compliance report for GraphQL operations.
 * Contains information about compliance events and their status.
 */
public class ComplianceReport {
    
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime generatedAt;
    private long securityEventCount;
    private long complianceEventCount;
    private long performanceEventCount;
    private long totalEvents;
    
    /**
     * Creates an empty compliance report.
     */
    public ComplianceReport() {
        this.generatedAt = LocalDateTime.now();
    }
    
    /**
     * Creates a compliance report with all information.
     */
    public ComplianceReport(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime generatedAt,
                           long securityEventCount, long complianceEventCount, long performanceEventCount) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.generatedAt = generatedAt;
        this.securityEventCount = securityEventCount;
        this.complianceEventCount = complianceEventCount;
        this.performanceEventCount = performanceEventCount;
        this.totalEvents = securityEventCount + complianceEventCount + performanceEventCount;
    }
    
    // Getters and setters
    
    public LocalDateTime getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    
    public LocalDateTime getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
    
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
    
    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
    
    public long getSecurityEventCount() {
        return securityEventCount;
    }
    
    public void setSecurityEventCount(long securityEventCount) {
        this.securityEventCount = securityEventCount;
        updateTotalEvents();
    }
    
    public long getComplianceEventCount() {
        return complianceEventCount;
    }
    
    public void setComplianceEventCount(long complianceEventCount) {
        this.complianceEventCount = complianceEventCount;
        updateTotalEvents();
    }
    
    public long getPerformanceEventCount() {
        return performanceEventCount;
    }
    
    public void setPerformanceEventCount(long performanceEventCount) {
        this.performanceEventCount = performanceEventCount;
        updateTotalEvents();
    }
    
    public long getTotalEvents() {
        return totalEvents;
    }
    
    /**
     * Gets the security event percentage.
     * 
     * @return security event percentage
     */
    public double getSecurityEventPercentage() {
        return totalEvents > 0 ? (double) securityEventCount / totalEvents * 100 : 0.0;
    }
    
    /**
     * Gets the compliance event percentage.
     * 
     * @return compliance event percentage
     */
    public double getComplianceEventPercentage() {
        return totalEvents > 0 ? (double) complianceEventCount / totalEvents * 100 : 0.0;
    }
    
    /**
     * Gets the performance event percentage.
     * 
     * @return performance event percentage
     */
    public double getPerformanceEventPercentage() {
        return totalEvents > 0 ? (double) performanceEventCount / totalEvents * 100 : 0.0;
    }
    
    /**
     * Updates the total events count.
     */
    private void updateTotalEvents() {
        this.totalEvents = securityEventCount + complianceEventCount + performanceEventCount;
    }
    
    @Override
    public String toString() {
        return String.format("ComplianceReport{startDate=%s, endDate=%s, totalEvents=%d, securityEvents=%d, complianceEvents=%d, performanceEvents=%d}",
            startDate, endDate, totalEvents, securityEventCount, complianceEventCount, performanceEventCount);
    }
} 