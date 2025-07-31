package com.intuit.gqlex.linting.core;

import com.intuit.gqlex.linting.rules.LintRule;
import com.intuit.gqlex.linting.config.LintConfig;
import graphql.language.Document;
import graphql.parser.Parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Main orchestrator for GraphQL linting operations.
 * 
 * <p>This class manages and applies linting rules to GraphQL documents. It provides
 * a fluent API for configuring and executing linting operations.</p>
 * 
 * <p>The linting system is completely generic and agnostic to any specific GraphQL schema,
 * working with any query, mutation, or subscription structure.</p>
 * 
 * @author gqlex
 * @version 2.0.1
 * @since 2.0.1
 */
public class GraphQLLinter {
    private final List<LintRule> rules = new ArrayList<>();
    private final LintConfig config;
    private final Parser parser;

    /**
     * Creates a new GraphQL linter with default configuration.
     */
    public GraphQLLinter() {
        this(new LintConfig());
    }

    /**
     * Creates a new GraphQL linter with the specified configuration.
     * 
     * @param config the linting configuration
     */
    public GraphQLLinter(LintConfig config) {
        this.config = config != null ? config : new LintConfig();
        this.parser = new Parser();
    }

    /**
     * Adds a linting rule to this linter.
     * 
     * @param rule the rule to add
     * @return this linter for method chaining
     */
    public GraphQLLinter addRule(LintRule rule) {
        if (rule != null) {
            rules.add(rule);
        }
        return this;
    }

    /**
     * Adds multiple linting rules to this linter.
     * 
     * @param rules the rules to add
     * @return this linter for method chaining
     */
    public GraphQLLinter addRules(LintRule... rules) {
        if (rules != null) {
            for (LintRule rule : rules) {
                addRule(rule);
            }
        }
        return this;
    }

    /**
     * Adds multiple linting rules to this linter.
     * 
     * @param rules the list of rules to add
     * @return this linter for method chaining
     */
    public GraphQLLinter addRules(List<LintRule> rules) {
        if (rules != null) {
            for (LintRule rule : rules) {
                addRule(rule);
            }
        }
        return this;
    }

    /**
     * Removes a linting rule from this linter.
     * 
     * @param rule the rule to remove
     * @return this linter for method chaining
     */
    public GraphQLLinter removeRule(LintRule rule) {
        rules.remove(rule);
        return this;
    }

    /**
     * Removes a linting rule by name from this linter.
     * 
     * @param ruleName the name of the rule to remove
     * @return this linter for method chaining
     */
    public GraphQLLinter removeRule(String ruleName) {
        rules.removeIf(rule -> ruleName.equals(rule.getName()));
        return this;
    }

    /**
     * Clears all linting rules from this linter.
     * 
     * @return this linter for method chaining
     */
    public GraphQLLinter clearRules() {
        rules.clear();
        return this;
    }

    /**
     * Gets all linting rules in this linter.
     * 
     * @return an unmodifiable list of rules
     */
    public List<LintRule> getRules() {
        return Collections.unmodifiableList(rules);
    }

    /**
     * Gets the linting configuration.
     * 
     * @return the configuration
     */
    public LintConfig getConfig() {
        return config;
    }

    /**
     * Lints a GraphQL document.
     * 
     * @param document the document to lint
     * @return the linting result
     */
    public LintResult lint(Document document) {
        if (document == null) {
            return new LintResult();
        }

        LintContext context = new LintContext(document, config);
        LintResult result = new LintResult();

        // Apply all enabled rules
        for (LintRule rule : rules) {
            try {
                if (rule.isEnabled(context)) {
                    rule.lint(context, result);
                }
            } catch (Exception e) {
                // Generic error handling - no field-specific logic
                result.addIssue(LintIssue.generic(rule.getName(), 
                    "Rule execution failed: " + e.getMessage()));
            }
        }

        return result;
    }

    /**
     * Lints a GraphQL query string.
     * 
     * @param queryString the query string to lint
     * @return the linting result
     */
    public LintResult lint(String queryString) {
        if (queryString == null || queryString.trim().isEmpty()) {
            LintResult result = new LintResult();
            result.addIssue(LintIssue.syntax("EMPTY_QUERY", "Query string is null or empty"));
            return result;
        }

        try {
            Document document = parser.parseDocument(queryString);
            return lint(document);
        } catch (Exception e) {
            // Generic parsing error handling
            LintResult result = new LintResult();
            result.addIssue(LintIssue.syntax("PARSE_ERROR", 
                "Failed to parse GraphQL query: " + e.getMessage()));
            return result;
        }
    }

    /**
     * Lints a GraphQL document with custom context.
     * 
     * @param document the document to lint
     * @param context the custom linting context
     * @return the linting result
     */
    public LintResult lint(Document document, LintContext context) {
        if (document == null) {
            return new LintResult();
        }

        if (context == null) {
            context = new LintContext(document, config);
        }

        LintResult result = new LintResult();

        // Apply all enabled rules
        for (LintRule rule : rules) {
            try {
                if (rule.isEnabled(context)) {
                    rule.lint(context, result);
                }
            } catch (Exception e) {
                // Generic error handling - no field-specific logic
                result.addIssue(LintIssue.generic(rule.getName(), 
                    "Rule execution failed: " + e.getMessage()));
            }
        }

        return result;
    }

    /**
     * Lints a GraphQL query string with custom context.
     * 
     * @param queryString the query string to lint
     * @param context the custom linting context
     * @return the linting result
     */
    public LintResult lint(String queryString, LintContext context) {
        if (queryString == null || queryString.trim().isEmpty()) {
            LintResult result = new LintResult();
            result.addIssue(LintIssue.syntax("EMPTY_QUERY", "Query string is null or empty"));
            return result;
        }

        try {
            Document document = parser.parseDocument(queryString);
            return lint(document, context);
        } catch (Exception e) {
            // Generic parsing error handling
            LintResult result = new LintResult();
            result.addIssue(LintIssue.syntax("PARSE_ERROR", 
                "Failed to parse GraphQL query: " + e.getMessage()));
            return result;
        }
    }

    /**
     * Checks if the linter has any rules configured.
     * 
     * @return true if the linter has rules, false otherwise
     */
    public boolean hasRules() {
        return !rules.isEmpty();
    }

    /**
     * Gets the number of rules configured in this linter.
     * 
     * @return the rule count
     */
    public int getRuleCount() {
        return rules.size();
    }

    /**
     * Gets a rule by name.
     * 
     * @param ruleName the name of the rule to find
     * @return the rule if found, null otherwise
     */
    public LintRule getRule(String ruleName) {
        if (ruleName == null) {
            return null;
        }
        
        return rules.stream()
            .filter(rule -> ruleName.equals(rule.getName()))
            .findFirst()
            .orElse(null);
    }

    /**
     * Checks if a rule with the given name is configured.
     * 
     * @param ruleName the name of the rule to check
     * @return true if the rule is configured, false otherwise
     */
    public boolean hasRule(String ruleName) {
        return getRule(ruleName) != null;
    }

    /**
     * Gets all rules in a specific category.
     * 
     * @param category the category to filter by
     * @return a list of rules in the specified category
     */
    public List<LintRule> getRulesByCategory(String category) {
        if (category == null) {
            return Collections.emptyList();
        }
        
        return rules.stream()
            .filter(rule -> category.equals(rule.getCategory()))
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * Creates a new linter with the same configuration but no rules.
     * 
     * @return a new linter instance
     */
    public GraphQLLinter copy() {
        return new GraphQLLinter(config);
    }

    /**
     * Creates a new linter with the same configuration and rules.
     * 
     * @return a new linter instance
     */
    public GraphQLLinter deepCopy() {
        GraphQLLinter copy = new GraphQLLinter(config);
        copy.addRules(rules);
        return copy;
    }

    @Override
    public String toString() {
        return String.format("GraphQLLinter{rules=%d, config=%s}", 
            rules.size(), config);
    }
} 