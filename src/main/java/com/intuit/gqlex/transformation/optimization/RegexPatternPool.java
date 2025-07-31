package com.intuit.gqlex.transformation.optimization;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Generic regex pattern pool for optimizing GraphQL string operations.
 * Works with any field names and GraphQL structures without hardcoding specific patterns.
 */
public class RegexPatternPool {
    
    private static final RegexPatternPool INSTANCE = new RegexPatternPool();
    private final Map<String, Pattern> patternCache = new ConcurrentHashMap<>();
    
    // Maximum cache size to prevent memory leaks
    private static final int MAX_CACHE_SIZE = 500;
    
    private RegexPatternPool() {
        // Private constructor for singleton pattern
    }
    
    public static RegexPatternPool getInstance() {
        return INSTANCE;
    }
    
    /**
     * Get or compile a regex pattern.
     * Generic: Caches any regex pattern, not field-specific ones
     */
    public Pattern getPattern(String regex) {
        if (regex == null || regex.trim().isEmpty()) {
            return null;
        }
        
        return patternCache.computeIfAbsent(regex, Pattern::compile);
    }
    
    /**
     * Replace all occurrences using cached pattern.
     * Generic: Works with any field name or GraphQL structure
     */
    public String replaceAll(String input, String regex, String replacement) {
        if (input == null || regex == null) {
            return input;
        }
        
        Pattern pattern = getPattern(regex);
        return pattern != null ? pattern.matcher(input).replaceAll(replacement) : input;
    }
    
    /**
     * Generic field name replacement - no hardcoded field names.
     * Uses word boundaries to avoid partial matches.
     */
    public String replaceFieldName(String queryString, String oldFieldName, String newFieldName) {
        if (queryString == null || oldFieldName == null || newFieldName == null) {
            return queryString;
        }
        
        // Generic pattern: word boundaries to avoid partial matches
        String pattern = "\\b" + Pattern.quote(oldFieldName) + "\\b";
        return replaceAll(queryString, pattern, newFieldName);
    }
    
    /**
     * Generic argument replacement - works with any argument name.
     * Matches any argument structure generically.
     */
    public String replaceArgument(String queryString, String fieldName, String argName, String newValue) {
        if (queryString == null || fieldName == null || argName == null) {
            return queryString;
        }
        
        // Generic pattern: matches any argument structure
        String pattern = "(" + Pattern.quote(fieldName) + "\\s*\\()([^)]*?\\b" + Pattern.quote(argName) + "\\s*:\\s*)[^,)]*";
        return queryString.replaceAll(pattern, "$1$2" + newValue);
    }
    
    /**
     * Generic field with selection set replacement.
     * Works with any field name and nested structure.
     */
    public String replaceFieldWithSelectionSet(String queryString, String fieldName, String newSelectionSet) {
        if (queryString == null || fieldName == null) {
            return queryString;
        }
        
        // Generic pattern: matches field with selection set
        String pattern = "\\b" + Pattern.quote(fieldName) + "\\s*\\{[^}]*\\}";
        return replaceAll(queryString, pattern, fieldName + " " + newSelectionSet);
    }
    
    /**
     * Generic fragment spread replacement.
     * Works with any fragment name and directives.
     */
    public String replaceFragmentSpread(String queryString, String fragmentName, String replacement) {
        if (queryString == null || fragmentName == null) {
            return queryString;
        }
        
        // Generic pattern: matches fragment spread with optional directives
        String pattern = "\\s*\\.\\.\\.\\s*" + Pattern.quote(fragmentName) + "(\\s*@[^\\n\\r]*)?";
        return replaceAll(queryString, pattern, replacement);
    }
    
    /**
     * Generic alias replacement.
     * Works with any field name and alias.
     */
    public String replaceAlias(String queryString, String fieldName, String newAlias) {
        if (queryString == null || fieldName == null || newAlias == null) {
            return queryString;
        }
        
        // Generic pattern: matches field with optional alias
        String pattern = "([a-zA-Z_][a-zA-Z0-9_]*)\\s*:\\s*" + Pattern.quote(fieldName) + "\\b";
        return replaceAll(queryString, pattern, newAlias + ": " + fieldName);
    }
    
    /**
     * Generic cache eviction based on size.
     * Prevents memory leaks by clearing cache when it gets too large.
     */
    public void evictOldEntries() {
        if (patternCache.size() > MAX_CACHE_SIZE) {
            patternCache.clear();
        }
    }
    
    /**
     * Clear all cached patterns. Useful for testing or memory management.
     */
    public void clearAll() {
        patternCache.clear();
    }
    
    /**
     * Get cache statistics for monitoring.
     */
    public int getCacheSize() {
        return patternCache.size();
    }
    
    /**
     * Check if a pattern is cached.
     */
    public boolean isCached(String regex) {
        return patternCache.containsKey(regex);
    }
} 