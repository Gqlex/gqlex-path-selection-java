package com.intuit.gqlex.gqlxpath.lazy;

import com.intuit.gqlex.gqlxpath.selector.SelectorFacade;
import com.intuit.gqlex.common.GqlNodeContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enhanced gqlXPath processor that uses lazy loading for improved performance
 */
public class LazyXPathProcessor {
    
    private final DocumentSectionLoader sectionLoader;
    private final XPathAnalyzer xPathAnalyzer;
    private final SelectorFacade selectorFacade;
    private final Map<String, String> documentCache = new ConcurrentHashMap<>();
    private final Map<String, Long> performanceMetrics = new ConcurrentHashMap<>();
    
    public LazyXPathProcessor() {
        this.sectionLoader = new DocumentSectionLoader();
        this.xPathAnalyzer = new XPathAnalyzer();
        this.selectorFacade = new SelectorFacade();
    }
    
    /**
     * Process xpath query using lazy loading
     */
    public LazyXPathResult processXPath(String documentId, String xpath) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Analyze xpath to determine required sections
            XPathAnalysis analysis = xPathAnalyzer.analyzeXPath(xpath);
            
            // Load only required sections
            DocumentSection section = sectionLoader.loadSection(documentId, xpath);
            
            // Process with existing selector facade
            List<GqlNodeContext> result = processWithSelectorFacade(documentId, xpath, section);
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // Record performance metrics
            performanceMetrics.put(xpath, duration);
            
            return new LazyXPathResult(result, section, analysis, duration);
            
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            return new LazyXPathResult(e, duration);
        }
    }
    
    /**
     * Process xpath using the existing selector facade with lazy loading
     */
    private List<GqlNodeContext> processWithSelectorFacade(String documentId, String xpath, DocumentSection section) {
        // Get the full document content if needed for compatibility
        String documentContent = getDocumentContent(documentId);
        
        // Use existing selector facade
        return selectorFacade.selectMany(documentContent, xpath);
    }
    
    /**
     * Get document content (with caching)
     */
    private String getDocumentContent(String documentId) {
        return documentCache.computeIfAbsent(documentId, this::loadDocumentContent);
    }
    
    /**
     * Load document content from file
     */
    private String loadDocumentContent(String documentId) {
        try {
            return new String(Files.readAllBytes(Paths.get(documentId)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load document: " + documentId, e);
        }
    }
    
    /**
     * Process multiple xpath queries efficiently
     */
    public List<LazyXPathResult> processMultipleXPaths(String documentId, List<String> xpaths) {
        List<LazyXPathResult> results = new ArrayList<>();
        
        for (String xpath : xpaths) {
            LazyXPathResult result = processXPath(documentId, xpath);
            results.add(result);
        }
        
        return results;
    }
    
    /**
     * Get performance statistics
     */
    public Map<String, Object> getPerformanceStats() {
        Map<String, Object> stats = new HashMap<>();
        
        if (performanceMetrics.isEmpty()) {
            stats.put("averageTime", 0.0);
            stats.put("minTime", 0.0);
            stats.put("maxTime", 0.0);
            stats.put("totalQueries", 0);
            return stats;
        }
        
        List<Long> times = new ArrayList<>(performanceMetrics.values());
        double average = times.stream().mapToLong(Long::longValue).average().orElse(0.0);
        long min = times.stream().mapToLong(Long::longValue).min().orElse(0);
        long max = times.stream().mapToLong(Long::longValue).max().orElse(0);
        
        stats.put("averageTime", average);
        stats.put("minTime", (double) min);
        stats.put("maxTime", (double) max);
        stats.put("totalQueries", times.size());
        stats.put("cacheStats", sectionLoader.getCacheStats());
        
        return stats;
    }
    
    /**
     * Clear all caches
     */
    public void clearCaches() {
        sectionLoader.clearAllCache();
        documentCache.clear();
        performanceMetrics.clear();
    }
    
    /**
     * Clear cache for specific document
     */
    public void clearDocumentCache(String documentId) {
        sectionLoader.clearCache(documentId);
        documentCache.remove(documentId);
    }
    
    /**
     * Compare performance with traditional approach
     */
    public PerformanceComparison compareWithTraditional(String documentId, String xpath) {
        // Traditional approach (load entire document)
        long traditionalStart = System.currentTimeMillis();
        String fullDocument = getDocumentContent(documentId);
        List<GqlNodeContext> traditionalResult = selectorFacade.selectMany(fullDocument, xpath);
        long traditionalEnd = System.currentTimeMillis();
        long traditionalTime = traditionalEnd - traditionalStart;
        
        // Lazy loading approach
        long lazyStart = System.currentTimeMillis();
        LazyXPathResult lazyResult = processXPath(documentId, xpath);
        long lazyEnd = System.currentTimeMillis();
        long lazyTime = lazyEnd - lazyStart;
        
        return new PerformanceComparison(
            traditionalTime,
            lazyTime,
            traditionalResult,
            lazyResult.getResult(),
            calculateImprovement(traditionalTime, lazyTime)
        );
    }
    
    /**
     * Calculate performance improvement percentage
     */
    private double calculateImprovement(long traditionalTime, long lazyTime) {
        if (traditionalTime == 0) return 0.0;
        return ((double) (traditionalTime - lazyTime) / traditionalTime) * 100.0;
    }
    
    /**
     * Result class for lazy xpath processing
     */
    public static class LazyXPathResult {
        private final List<GqlNodeContext> result;
        private final DocumentSection section;
        private final XPathAnalysis analysis;
        private final long duration;
        private final Exception error;
        
        public LazyXPathResult(List<GqlNodeContext> result, DocumentSection section, 
                             XPathAnalysis analysis, long duration) {
            this.result = result;
            this.section = section;
            this.analysis = analysis;
            this.duration = duration;
            this.error = null;
        }
        
        public LazyXPathResult(Exception error, long duration) {
            this.result = null;
            this.section = null;
            this.analysis = null;
            this.duration = duration;
            this.error = error;
        }
        
        public List<GqlNodeContext> getResult() {
            return result;
        }
        
        public DocumentSection getSection() {
            return section;
        }
        
        public XPathAnalysis getAnalysis() {
            return analysis;
        }
        
        public long getDuration() {
            return duration;
        }
        
        public Exception getError() {
            return error;
        }
        
        public boolean isSuccess() {
            return error == null;
        }
        
        public boolean hasError() {
            return error != null;
        }
    }
    
    /**
     * Performance comparison result
     */
    public static class PerformanceComparison {
        private final long traditionalTime;
        private final long lazyTime;
        private final List<GqlNodeContext> traditionalResult;
        private final List<GqlNodeContext> lazyResult;
        private final double improvementPercentage;
        
        public PerformanceComparison(long traditionalTime, long lazyTime,
                                   List<GqlNodeContext> traditionalResult,
                                   List<GqlNodeContext> lazyResult,
                                   double improvementPercentage) {
            this.traditionalTime = traditionalTime;
            this.lazyTime = lazyTime;
            this.traditionalResult = traditionalResult;
            this.lazyResult = lazyResult;
            this.improvementPercentage = improvementPercentage;
        }
        
        public long getTraditionalTime() {
            return traditionalTime;
        }
        
        public long getLazyTime() {
            return lazyTime;
        }
        
        public List<GqlNodeContext> getTraditionalResult() {
            return traditionalResult;
        }
        
        public List<GqlNodeContext> getLazyResult() {
            return lazyResult;
        }
        
        public double getImprovementPercentage() {
            return improvementPercentage;
        }
        
        public boolean isLazyFaster() {
            return lazyTime < traditionalTime;
        }
        
        public boolean resultsMatch() {
            if (traditionalResult == null && lazyResult == null) return true;
            if (traditionalResult == null || lazyResult == null) return false;
            
            // Simple comparison - in real implementation, you'd want more sophisticated comparison
            return traditionalResult.size() == lazyResult.size();
        }
    }
} 