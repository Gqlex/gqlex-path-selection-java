package com.intuit.gqlex.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * Result of a security validation operation.
 * Contains information about security issues found during validation.
 */
public class SecurityValidationResult {
    
    private final List<SecurityIssue> errors;
    private final List<SecurityIssue> warnings;
    private final List<SecurityIssue> info;
    
    /**
     * Creates an empty security validation result.
     */
    public SecurityValidationResult() {
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
        this.info = new ArrayList<>();
    }
    
    /**
     * Adds a security error.
     * 
     * @param code the error code
     * @param message the error message
     */
    public void addError(String code, String message) {
        errors.add(new SecurityIssue(code, message, SecuritySeverity.ERROR));
    }
    
    /**
     * Adds a security warning.
     * 
     * @param code the warning code
     * @param message the warning message
     */
    public void addWarning(String code, String message) {
        warnings.add(new SecurityIssue(code, message, SecuritySeverity.WARNING));
    }
    
    /**
     * Adds a security info message.
     * 
     * @param code the info code
     * @param message the info message
     */
    public void addInfo(String code, String message) {
        info.add(new SecurityIssue(code, message, SecuritySeverity.INFO));
    }
    
    /**
     * Checks if the validation result is valid (no errors).
     * 
     * @return true if valid
     */
    public boolean isValid() {
        return errors.isEmpty();
    }
    
    /**
     * Checks if the validation result has any warnings.
     * 
     * @return true if has warnings
     */
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
    
    /**
     * Checks if the validation result has any info messages.
     * 
     * @return true if has info messages
     */
    public boolean hasInfo() {
        return !info.isEmpty();
    }
    
    /**
     * Gets the number of errors.
     * 
     * @return error count
     */
    public int getErrorCount() {
        return errors.size();
    }
    
    /**
     * Gets the number of warnings.
     * 
     * @return warning count
     */
    public int getWarningCount() {
        return warnings.size();
    }
    
    /**
     * Gets the number of info messages.
     * 
     * @return info count
     */
    public int getInfoCount() {
        return info.size();
    }
    
    /**
     * Gets all errors.
     * 
     * @return list of errors
     */
    public List<SecurityIssue> getErrors() {
        return Collections.unmodifiableList(errors);
    }
    
    /**
     * Gets all warnings.
     * 
     * @return list of warnings
     */
    public List<SecurityIssue> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }
    
    /**
     * Gets all info messages.
     * 
     * @return list of info messages
     */
    public List<SecurityIssue> getInfo() {
        return Collections.unmodifiableList(info);
    }
    
    /**
     * Gets all issues (errors, warnings, and info).
     * 
     * @return list of all issues
     */
    public List<SecurityIssue> getAllIssues() {
        List<SecurityIssue> allIssues = new ArrayList<>();
        allIssues.addAll(errors);
        allIssues.addAll(warnings);
        allIssues.addAll(info);
        return Collections.unmodifiableList(allIssues);
    }
    
    /**
     * Gets a summary of the validation result.
     * 
     * @return summary string
     */
    public String getSummary() {
        return String.format("Security validation result: %d errors, %d warnings, %d info messages",
            getErrorCount(), getWarningCount(), getInfoCount());
    }
    
    /**
     * Represents a security issue found during validation.
     */
    public static class SecurityIssue {
        private final String code;
        private final String message;
        private final SecuritySeverity severity;
        
        public SecurityIssue(String code, String message, SecuritySeverity severity) {
            this.code = code;
            this.message = message;
            this.severity = severity;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getMessage() {
            return message;
        }
        
        public SecuritySeverity getSeverity() {
            return severity;
        }
        
        @Override
        public String toString() {
            return String.format("[%s] %s: %s", severity, code, message);
        }
    }
    
    /**
     * Security severity levels.
     */
    public enum SecuritySeverity {
        INFO, WARNING, ERROR, CRITICAL
    }
} 