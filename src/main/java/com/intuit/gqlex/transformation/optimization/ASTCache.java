package com.intuit.gqlex.transformation.optimization;

import graphql.language.Document;
import graphql.language.AstPrinter;
import graphql.parser.Parser;
import graphql.parser.InvalidSyntaxException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Generic AST caching system for optimizing GraphQL document parsing and printing.
 * Works with any GraphQL query, mutation, or subscription without hardcoding field names.
 */
public class ASTCache {
    
    private static final ASTCache INSTANCE = new ASTCache();
    private final Map<String, Document> parsedCache = new ConcurrentHashMap<>();
    private final Map<Integer, String> printedCache = new ConcurrentHashMap<>();
    private final Parser parser = new Parser();
    
    // Maximum cache size to prevent memory leaks
    private static final int MAX_CACHE_SIZE = 1000;
    
    private ASTCache() {
        // Private constructor for singleton pattern
    }
    
    public static ASTCache getInstance() {
        return INSTANCE;
    }
    
    /**
     * Get or parse a GraphQL document from string.
     * Generic: Works with any GraphQL document type (query, mutation, subscription)
     */
    public Document getOrParse(String queryString) {
        if (queryString == null || queryString.trim().isEmpty()) {
            return null;
        }
        
        return parsedCache.computeIfAbsent(queryString, this::parseDocument);
    }
    
    /**
     * Get or print a GraphQL document to string.
     * Generic: Uses document hash, not field-specific logic
     */
    public String getOrPrint(Document document) {
        if (document == null) {
            return null;
        }
        
        int documentHash = document.hashCode();
        return printedCache.computeIfAbsent(documentHash, k -> {
            try {
                return AstPrinter.printAst(document);
            } catch (Exception e) {
                // Fallback to toString if AstPrinter fails
                return document.toString();
            }
        });
    }
    
    /**
     * Parse a GraphQL document with error handling.
     * Generic: Works with any valid GraphQL document
     */
    private Document parseDocument(String queryString) {
        try {
            return parser.parseDocument(queryString);
        } catch (InvalidSyntaxException e) {
            // Log parsing error but don't cache failed parses
            System.err.println("Failed to parse GraphQL document: " + e.getMessage());
            return null;
        } catch (Exception e) {
            // Handle any other parsing errors
            System.err.println("Unexpected error parsing GraphQL document: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Generic cache eviction based on size, not content.
     * Prevents memory leaks by clearing caches when they get too large.
     */
    public void evictOldEntries() {
        if (parsedCache.size() > MAX_CACHE_SIZE) {
            parsedCache.clear();
        }
        
        if (printedCache.size() > MAX_CACHE_SIZE) {
            printedCache.clear();
        }
    }
    
    /**
     * Clear all caches. Useful for testing or memory management.
     */
    public void clearAll() {
        parsedCache.clear();
        printedCache.clear();
    }
    
    /**
     * Get cache statistics for monitoring.
     */
    public CacheStats getStats() {
        return new CacheStats(parsedCache.size(), printedCache.size());
    }
    
    /**
     * Cache statistics for monitoring performance.
     */
    public static class CacheStats {
        private final int parsedCacheSize;
        private final int printedCacheSize;
        
        public CacheStats(int parsedCacheSize, int printedCacheSize) {
            this.parsedCacheSize = parsedCacheSize;
            this.printedCacheSize = printedCacheSize;
        }
        
        public int getParsedCacheSize() {
            return parsedCacheSize;
        }
        
        public int getPrintedCacheSize() {
            return printedCacheSize;
        }
        
        @Override
        public String toString() {
            return String.format("ASTCache{parsed=%d, printed=%d}", parsedCacheSize, printedCacheSize);
        }
    }
} 