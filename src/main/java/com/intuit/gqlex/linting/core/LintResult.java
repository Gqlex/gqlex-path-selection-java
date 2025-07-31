package com.intuit.gqlex.linting.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Collects and manages linting issues found during GraphQL document analysis.
 * 
 * <p>This class provides methods to add, retrieve, and analyze linting issues
 * by their severity level. It maintains separate collections for errors, warnings,
 * and informational issues.</p>
 * 
 * <p>The linting system is completely generic and agnostic to any specific GraphQL schema,
 * working with any query, mutation, or subscription structure.</p>
 * 
 * @author gqlex
 * @version 2.0.1
 * @since 2.0.1
 */
public class LintResult {
    private final List<LintIssue> errors = new ArrayList<>();
    private final List<LintIssue> warnings = new ArrayList<>();
    private final List<LintIssue> info = new ArrayList<>();

    /**
     * Creates a new empty lint result.
     */
    public LintResult() {}

    /**
     * Adds an error issue to this result.
     * 
     * @param ruleName the name of the rule that generated this issue
     * @param message the descriptive message about the issue
     * @param node the GraphQL node that caused the issue
     */
    public void addError(String ruleName, String message, graphql.language.Node node) {
        errors.add(new LintIssue(ruleName, message, LintLevel.ERROR, node));
    }

    /**
     * Adds a warning issue to this result.
     * 
     * @param ruleName the name of the rule that generated this issue
     * @param message the descriptive message about the issue
     * @param node the GraphQL node that caused the issue
     */
    public void addWarning(String ruleName, String message, graphql.language.Node node) {
        warnings.add(new LintIssue(ruleName, message, LintLevel.WARNING, node));
    }

    /**
     * Adds an informational issue to this result.
     * 
     * @param ruleName the name of the rule that generated this issue
     * @param message the descriptive message about the issue
     * @param node the GraphQL node that caused the issue
     */
    public void addInfo(String ruleName, String message, graphql.language.Node node) {
        info.add(new LintIssue(ruleName, message, LintLevel.INFO, node));
    }

    /**
     * Adds a linting issue to this result.
     * 
     * @param issue the linting issue to add
     */
    public void addIssue(LintIssue issue) {
        if (issue == null) {
            return;
        }
        
        switch (issue.getLevel()) {
            case ERROR:
                errors.add(issue);
                break;
            case WARNING:
                warnings.add(issue);
                break;
            case INFO:
                info.add(issue);
                break;
        }
    }

    /**
     * Merges another lint result into this one.
     * 
     * @param other the lint result to merge
     */
    public void merge(LintResult other) {
        if (other == null) {
            return;
        }
        
        errors.addAll(other.errors);
        warnings.addAll(other.warnings);
        info.addAll(other.info);
    }

    /**
     * Checks if this result has any errors.
     * 
     * @return true if there are any errors
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Checks if this result has any warnings.
     * 
     * @return true if there are any warnings
     */
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }

    /**
     * Checks if this result has any informational issues.
     * 
     * @return true if there are any informational issues
     */
    public boolean hasInfo() {
        return !info.isEmpty();
    }

    /**
     * Checks if this result has any issues (errors, warnings, or info).
     * 
     * @return true if there are any issues
     */
    public boolean hasIssues() {
        return hasErrors() || hasWarnings() || hasInfo();
    }

    /**
     * Gets the number of error issues.
     * 
     * @return the error count
     */
    public int getErrorCount() {
        return errors.size();
    }

    /**
     * Gets the number of warning issues.
     * 
     * @return the warning count
     */
    public int getWarningCount() {
        return warnings.size();
    }

    /**
     * Gets the number of informational issues.
     * 
     * @return the info count
     */
    public int getInfoCount() {
        return info.size();
    }

    /**
     * Gets the total number of issues.
     * 
     * @return the total issue count
     */
    public int getTotalIssueCount() {
        return getErrorCount() + getWarningCount() + getInfoCount();
    }

    /**
     * Gets all error issues.
     * 
     * @return an unmodifiable list of error issues
     */
    public List<LintIssue> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    /**
     * Gets all warning issues.
     * 
     * @return an unmodifiable list of warning issues
     */
    public List<LintIssue> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }

    /**
     * Gets all informational issues.
     * 
     * @return an unmodifiable list of informational issues
     */
    public List<LintIssue> getInfo() {
        return Collections.unmodifiableList(info);
    }

    /**
     * Gets all issues (errors, warnings, and info).
     * 
     * @return an unmodifiable list of all issues
     */
    public List<LintIssue> getAllIssues() {
        List<LintIssue> all = new ArrayList<>();
        all.addAll(errors);
        all.addAll(warnings);
        all.addAll(info);
        return Collections.unmodifiableList(all);
    }

    /**
     * Gets all issues of a specific level.
     * 
     * @param level the level to filter by
     * @return an unmodifiable list of issues with the specified level
     */
    public List<LintIssue> getIssuesByLevel(LintLevel level) {
        if (level == null) {
            return Collections.emptyList();
        }
        
        switch (level) {
            case ERROR:
                return getErrors();
            case WARNING:
                return getWarnings();
            case INFO:
                return getInfo();
            default:
                return Collections.emptyList();
        }
    }

    /**
     * Gets all issues from a specific rule.
     * 
     * @param ruleName the name of the rule to filter by
     * @return an unmodifiable list of issues from the specified rule
     */
    public List<LintIssue> getIssuesByRule(String ruleName) {
        if (ruleName == null) {
            return Collections.emptyList();
        }
        
        return getAllIssues().stream()
            .filter(issue -> ruleName.equals(issue.getRuleName()))
            .collect(Collectors.toList());
    }

    /**
     * Clears all issues from this result.
     */
    public void clear() {
        errors.clear();
        warnings.clear();
        info.clear();
    }

    /**
     * Gets a summary of this lint result.
     * 
     * @return a string summary of the issues
     */
    public String getSummary() {
        int errorCount = getErrorCount();
        int warningCount = getWarningCount();
        int infoCount = getInfoCount();
        
        StringBuilder summary = new StringBuilder();
        summary.append("Lint Result: ");
        
        if (errorCount > 0) {
            summary.append(errorCount).append(" error(s)");
        }
        
        if (warningCount > 0) {
            if (errorCount > 0) summary.append(", ");
            summary.append(warningCount).append(" warning(s)");
        }
        
        if (infoCount > 0) {
            if (errorCount > 0 || warningCount > 0) summary.append(", ");
            summary.append(infoCount).append(" info");
        }
        
        if (errorCount == 0 && warningCount == 0 && infoCount == 0) {
            summary.append("No issues found");
        }
        
        return summary.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LintResult{");
        sb.append("errors=").append(errors.size());
        sb.append(", warnings=").append(warnings.size());
        sb.append(", info=").append(info.size());
        sb.append("}");
        return sb.toString();
    }
} 