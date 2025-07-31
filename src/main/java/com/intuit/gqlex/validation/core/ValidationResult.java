package com.intuit.gqlex.validation.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the result of a GraphQL validation operation.
 * This class is completely generic and works with any GraphQL validation scenario.
 */
public class ValidationResult {
    private final List<ValidationError> errors = new ArrayList<>();
    private final List<ValidationError> warnings = new ArrayList<>();
    private final List<ValidationError> info = new ArrayList<>();

    /**
     * Creates an empty validation result.
     */
    public ValidationResult() {
        // Empty constructor for creating new validation results
    }

    /**
     * Adds a validation error to the result.
     * 
     * @param ruleName the name of the validation rule
     * @param message the error message (generic, no hardcoded field names)
     * @param node the GraphQL node where the error occurred
     */
    public void addError(String ruleName, String message, graphql.language.Node node) {
        errors.add(new ValidationError(ruleName, message, ValidationLevel.ERROR, node));
    }

    /**
     * Adds a validation error to the result.
     * 
     * @param ruleName the name of the validation rule
     * @param message the error message
     */
    public void addError(String ruleName, String message) {
        errors.add(new ValidationError(ruleName, message, ValidationLevel.ERROR));
    }

    /**
     * Adds a validation warning to the result.
     * 
     * @param ruleName the name of the validation rule
     * @param message the warning message
     * @param node the GraphQL node where the warning occurred
     */
    public void addWarning(String ruleName, String message, graphql.language.Node node) {
        warnings.add(new ValidationError(ruleName, message, ValidationLevel.WARNING, node));
    }

    /**
     * Adds a validation warning to the result.
     * 
     * @param ruleName the name of the validation rule
     * @param message the warning message
     */
    public void addWarning(String ruleName, String message) {
        warnings.add(new ValidationError(ruleName, message, ValidationLevel.WARNING));
    }

    /**
     * Adds an informational message to the result.
     * 
     * @param ruleName the name of the validation rule
     * @param message the informational message
     * @param node the GraphQL node where the info applies
     */
    public void addInfo(String ruleName, String message, graphql.language.Node node) {
        info.add(new ValidationError(ruleName, message, ValidationLevel.INFO, node));
    }

    /**
     * Adds an informational message to the result.
     * 
     * @param ruleName the name of the validation rule
     * @param message the informational message
     */
    public void addInfo(String ruleName, String message) {
        info.add(new ValidationError(ruleName, message, ValidationLevel.INFO));
    }

    /**
     * Adds a validation error directly.
     * 
     * @param error the validation error to add
     */
    public void addError(ValidationError error) {
        if (error != null) {
            errors.add(error);
        }
    }

    /**
     * Adds a validation warning directly.
     * 
     * @param warning the validation warning to add
     */
    public void addWarning(ValidationError warning) {
        if (warning != null) {
            warnings.add(warning);
        }
    }

    /**
     * Adds an informational message directly.
     * 
     * @param infoMessage the informational message to add
     */
    public void addInfo(ValidationError infoMessage) {
        if (infoMessage != null) {
            info.add(infoMessage);
        }
    }

    /**
     * Merges another validation result into this one.
     * 
     * @param other the validation result to merge
     */
    public void merge(ValidationResult other) {
        if (other != null) {
            errors.addAll(other.getErrors());
            warnings.addAll(other.getWarnings());
            info.addAll(other.getInfo());
        }
    }

    /**
     * Checks if the validation result is valid (no errors).
     * 
     * @return true if there are no errors
     */
    public boolean isValid() {
        return errors.isEmpty();
    }

    /**
     * Checks if the validation result has any warnings.
     * 
     * @return true if there are warnings
     */
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }

    /**
     * Checks if the validation result has any informational messages.
     * 
     * @return true if there are informational messages
     */
    public boolean hasInfo() {
        return !info.isEmpty();
    }

    /**
     * Checks if the validation result has any issues (errors, warnings, or info).
     * 
     * @return true if there are any issues
     */
    public boolean hasIssues() {
        return !errors.isEmpty() || !warnings.isEmpty() || !info.isEmpty();
    }

    /**
     * Gets the number of errors.
     * 
     * @return the number of errors
     */
    public int getErrorCount() {
        return errors.size();
    }

    /**
     * Gets the number of warnings.
     * 
     * @return the number of warnings
     */
    public int getWarningCount() {
        return warnings.size();
    }

    /**
     * Gets the number of informational messages.
     * 
     * @return the number of informational messages
     */
    public int getInfoCount() {
        return info.size();
    }

    /**
     * Gets the total number of issues.
     * 
     * @return the total number of issues
     */
    public int getTotalIssueCount() {
        return errors.size() + warnings.size() + info.size();
    }

    /**
     * Gets all errors as an immutable list.
     * 
     * @return immutable list of errors
     */
    public List<ValidationError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    /**
     * Gets all warnings as an immutable list.
     * 
     * @return immutable list of warnings
     */
    public List<ValidationError> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }

    /**
     * Gets all informational messages as an immutable list.
     * 
     * @return immutable list of informational messages
     */
    public List<ValidationError> getInfo() {
        return Collections.unmodifiableList(info);
    }

    /**
     * Gets all issues (errors, warnings, and info) as an immutable list.
     * 
     * @return immutable list of all issues
     */
    public List<ValidationError> getAllIssues() {
        List<ValidationError> all = new ArrayList<>();
        all.addAll(errors);
        all.addAll(warnings);
        all.addAll(info);
        return Collections.unmodifiableList(all);
    }

    /**
     * Gets all issues sorted by severity (ERROR, WARNING, INFO).
     * 
     * @return sorted list of all issues
     */
    public List<ValidationError> getAllIssuesSorted() {
        return getAllIssues().stream()
            .sorted((a, b) -> {
                // Sort by severity: ERROR > WARNING > INFO
                if (a.getLevel() != b.getLevel()) {
                    return a.getLevel().ordinal() - b.getLevel().ordinal();
                }
                // If same level, sort by rule name
                return a.getRuleName().compareTo(b.getRuleName());
            })
            .collect(Collectors.toList());
    }

    /**
     * Gets errors for a specific rule.
     * 
     * @param ruleName the name of the rule
     * @return list of errors for the specified rule
     */
    public List<ValidationError> getErrorsForRule(String ruleName) {
        return errors.stream()
            .filter(error -> error.getRuleName().equals(ruleName))
            .collect(Collectors.toList());
    }

    /**
     * Gets warnings for a specific rule.
     * 
     * @param ruleName the name of the rule
     * @return list of warnings for the specified rule
     */
    public List<ValidationError> getWarningsForRule(String ruleName) {
        return warnings.stream()
            .filter(warning -> warning.getRuleName().equals(ruleName))
            .collect(Collectors.toList());
    }

    /**
     * Gets informational messages for a specific rule.
     * 
     * @param ruleName the name of the rule
     * @return list of informational messages for the specified rule
     */
    public List<ValidationError> getInfoForRule(String ruleName) {
        return info.stream()
            .filter(infoMsg -> infoMsg.getRuleName().equals(ruleName))
            .collect(Collectors.toList());
    }

    /**
     * Clears all validation issues.
     */
    public void clear() {
        errors.clear();
        warnings.clear();
        info.clear();
    }

    /**
     * Returns a summary of the validation result.
     * 
     * @return summary string
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Validation Result: ");
        
        if (isValid()) {
            summary.append("VALID");
        } else {
            summary.append("INVALID");
        }
        
        summary.append(" (");
        summary.append(getErrorCount()).append(" errors, ");
        summary.append(getWarningCount()).append(" warnings, ");
        summary.append(getInfoCount()).append(" info)");
        
        return summary.toString();
    }

    /**
     * Returns a detailed string representation of the validation result.
     * 
     * @return detailed string representation
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getSummary()).append("\n");
        
        if (!errors.isEmpty()) {
            sb.append("\nErrors:\n");
            for (ValidationError error : errors) {
                sb.append("  ").append(error.toString()).append("\n");
            }
        }
        
        if (!warnings.isEmpty()) {
            sb.append("\nWarnings:\n");
            for (ValidationError warning : warnings) {
                sb.append("  ").append(warning.toString()).append("\n");
            }
        }
        
        if (!info.isEmpty()) {
            sb.append("\nInfo:\n");
            for (ValidationError infoMsg : info) {
                sb.append("  ").append(infoMsg.toString()).append("\n");
            }
        }
        
        return sb.toString();
    }
} 