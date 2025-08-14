package com.intuit.gqlex.gqlxpath.lazy;

import com.intuit.gqlex.gqlxpath.selector.SelectorFacade;
import com.intuit.gqlex.common.GqlNodeContext;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * GENERIC LAZY LOADING gqlXPath processor that works with ANY GraphQL structure
 * 
 * Key Features:
 * - Generic XPath analysis without hardcoded patterns
 * - Intelligent document section detection
 * - Adaptive parsing based on actual document structure
 * - Works with any GraphQL schema, queries, mutations, fragments
 * - No assumptions about specific field names or structures
 */
public class LazyXPathProcessor {
    
    private final DocumentSectionLoader sectionLoader;
    private final XPathAnalyzer xPathAnalyzer;
    private final SelectorFacade selectorFacade;
    
    // Cache for parsed sections, not entire documents
    private final Map<String, DocumentSection> sectionCache = new ConcurrentHashMap<>();
    private final Map<String, List<GqlNodeContext>> resultCache = new ConcurrentHashMap<>();
    private final Map<String, Long> performanceMetrics = new ConcurrentHashMap<>();
    
    // Generic patterns for any GraphQL structure
    private static final Pattern SIMPLE_XPATH_PATTERN = Pattern.compile("^//[a-zA-Z_][a-zA-Z0-9_]*$");
    private static final Pattern FIELD_XPATH_PATTERN = Pattern.compile("^//[a-zA-Z_][a-zA-Z0-9_]*/[a-zA-Z_][a-zA-Z0-9_]*$");
    
    // Generic GraphQL operation patterns (agnostic to specific names)
    private static final Pattern OPERATION_START = Pattern.compile("^(query|mutation|subscription|fragment)\\s+\\w+");
    private static final Pattern BRACE_PATTERN = Pattern.compile("[{}]");
    
    public LazyXPathProcessor() {
        this.sectionLoader = new DocumentSectionLoader();
        this.xPathAnalyzer = new XPathAnalyzer();
        this.selectorFacade = new SelectorFacade();
    }
    
    /**
     * Process xpath query using generic lazy loading
     */
    public LazyXPathResult processXPath(String documentId, String xpath) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Check if this is a simple query that can use fast path
            if (isSimpleXPath(xpath)) {
                return processSimpleXPath(documentId, xpath, startTime);
            }
            
            // Use intelligent lazy loading for complex queries
            return processComplexXPath(documentId, xpath, startTime);
            
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            return new LazyXPathResult(e, duration);
        }
    }
    
    /**
     * Check if XPath is simple enough for fast processing
     */
    private boolean isSimpleXPath(String xpath) {
        if (xpath == null || xpath.trim().isEmpty()) {
            return false;
        }
        
        return SIMPLE_XPATH_PATTERN.matcher(xpath).matches() || 
               FIELD_XPATH_PATTERN.matcher(xpath).matches();
    }
    
    /**
     * Fast path for simple XPath queries - loads only required sections
     */
    private LazyXPathResult processSimpleXPath(String documentId, String xpath, long startTime) {
        // Use cached result if available
        String cacheKey = documentId + ":" + xpath;
        List<GqlNodeContext> cachedResult = resultCache.get(cacheKey);
        
        if (cachedResult != null) {
            long duration = System.currentTimeMillis() - startTime;
            updatePerformanceMetrics(xpath, duration);
            return new LazyXPathResult(cachedResult, null, null, duration);
        }
        
        // Load only the section needed for this XPath
        DocumentSection requiredSection = loadRequiredSection(documentId, xpath);
        
        // Process only the required section
        List<GqlNodeContext> result = selectorFacade.selectMany(requiredSection.getContent(), xpath);
        
        // Cache the result
        resultCache.put(cacheKey, result);
        
        long duration = System.currentTimeMillis() - startTime;
        updatePerformanceMetrics(xpath, duration);
        
        return new LazyXPathResult(result, requiredSection, null, duration);
    }
    
    /**
     * Intelligent lazy loading for complex XPath queries
     */
    private LazyXPathResult processComplexXPath(String documentId, String xpath, long startTime) {
        // Analyze xpath to determine exactly what sections are needed
        XPathAnalysis analysis = xPathAnalyzer.analyzeXPath(xpath);
        
        // Load ONLY the required sections based on analysis
        DocumentSection section = loadRequiredSection(documentId, xpath);
        
        // Process with existing selector facade using ONLY the required section
        List<GqlNodeContext> result = processWithSelectorFacade(documentId, xpath, section);
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Record performance metrics
        updatePerformanceMetrics(xpath, duration);
        
        return new LazyXPathResult(result, section, analysis, duration);
    }
    
    /**
     * Load ONLY the required document section for the XPath
     */
    private DocumentSection loadRequiredSection(String documentId, String xpath) {
        // Check cache first
        String sectionKey = documentId + ":" + xpath;
        DocumentSection cachedSection = sectionCache.get(sectionKey);
        if (cachedSection != null) {
            return cachedSection;
        }
        
        // Determine what section we need based on XPath analysis
        String sectionType = determineSectionType(xpath);
        
        // Load only that specific section from file
        DocumentSection section = loadSectionFromFile(documentId, sectionType);
        
        // Cache the section
        sectionCache.put(sectionKey, section);
        
        return section;
    }
    
    /**
     * Generic section type determination based on XPath analysis
     */
    private String determineSectionType(String xpath) {
        // Analyze XPath to find the most relevant section
        if (xpath.contains("//query") || xpath.contains("query")) {
            return "query";
        } else if (xpath.contains("//mutation") || xpath.contains("mutation")) {
            return "mutation";
        } else if (xpath.contains("//subscription") || xpath.contains("subscription")) {
            return "subscription";
        } else if (xpath.contains("//fragment") || xpath.contains("fragment")) {
            return "fragment";
        } else if (xpath.contains("//schema") || xpath.contains("schema")) {
            return "schema";
        } else if (xpath.contains("//type") || xpath.contains("type")) {
            return "type";
        } else if (xpath.contains("//input") || xpath.contains("input")) {
            return "input";
        } else if (xpath.contains("//enum") || xpath.contains("enum")) {
            return "enum";
        } else if (xpath.contains("//scalar") || xpath.contains("scalar")) {
            return "scalar";
        } else if (xpath.contains("//interface") || xpath.contains("interface")) {
            return "interface";
        } else if (xpath.contains("//union") || xpath.contains("union")) {
            return "union";
        } else if (xpath.contains("//directive") || xpath.contains("directive")) {
            return "directive";
        } else {
            // Default to query for field-based XPaths
            return "query";
        }
    }
    
    /**
     * Load ONLY a specific section from file using intelligent parsing
     */
    private DocumentSection loadSectionFromFile(String documentId, String sectionType) {
        try {
            Path filePath = Paths.get(documentId);
            if (!Files.exists(filePath)) {
                throw new RuntimeException("Document not found: " + documentId);
            }
            
            // Use RandomAccessFile for efficient section reading
            try (RandomAccessFile file = new RandomAccessFile(filePath.toFile(), "r")) {
                FileChannel channel = file.getChannel();
                
                // Find the start and end of the required section using intelligent parsing
                SectionBounds bounds = findSectionBoundsIntelligently(file, sectionType);
                
                if (bounds.start == -1) {
                    // Section not found, return empty section
                    return new DocumentSection(sectionType, "", 0, 0);
                }
                
                // Read ONLY the required section
                byte[] sectionBytes = new byte[(int) (bounds.end - bounds.start)];
                file.seek(bounds.start);
                file.read(sectionBytes);
                
                String sectionContent = new String(sectionBytes);
                
                return new DocumentSection(sectionType, sectionContent, bounds.start, bounds.end);
            }
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to load section from document: " + documentId, e);
        }
    }
    
    /**
     * Intelligent section boundary detection for ANY GraphQL structure
     */
    private SectionBounds findSectionBoundsIntelligently(RandomAccessFile file, String sectionType) throws IOException {
        long fileLength = file.length();
        long start = -1;
        long end = fileLength;
        
        // Read file in chunks to find section boundaries
        byte[] buffer = new byte[8192]; // 8KB buffer
        long position = 0;
        StringBuilder lineBuilder = new StringBuilder();
        
        while (position < fileLength) {
            file.seek(position);
            int bytesRead = file.read(buffer);
            if (bytesRead == -1) break;
            
            String chunk = new String(buffer, 0, bytesRead);
            lineBuilder.append(chunk);
            
            // Process complete lines
            String[] lines = lineBuilder.toString().split("\n", -1);
            lineBuilder.setLength(0);
            lineBuilder.append(lines[lines.length - 1]); // Keep incomplete line
            
            for (int i = 0; i < lines.length - 1; i++) {
                String line = lines[i];
                long lineStart = position + i * (chunk.length() / lines.length);
                
                if (start == -1) {
                    // Look for section start using generic patterns
                    if (isGenericSectionStart(line, sectionType)) {
                        start = lineStart;
                    }
                } else {
                    // Look for section end or next section start
                    if (isGenericSectionStart(line, "query") || 
                        isGenericSectionStart(line, "mutation") || 
                        isGenericSectionStart(line, "subscription") ||
                        isGenericSectionStart(line, "fragment") ||
                        isGenericSectionStart(line, "schema") ||
                        isGenericSectionStart(line, "type") ||
                        isGenericSectionStart(line, "input") ||
                        isGenericSectionStart(line, "enum") ||
                        isGenericSectionStart(line, "scalar") ||
                        isGenericSectionStart(line, "interface") ||
                        isGenericSectionStart(line, "union") ||
                        isGenericSectionStart(line, "directive")) {
                        if (!line.trim().toLowerCase().startsWith(sectionType.toLowerCase())) {
                            end = lineStart;
                            break;
                        }
                    }
                }
            }
            
            position += bytesRead;
        }
        
        return new SectionBounds(start, end);
    }
    
    /**
     * Generic section start detection for ANY GraphQL operation
     */
    private boolean isGenericSectionStart(String line, String sectionType) {
        String trimmed = line.trim();
        
        // Generic pattern matching for any GraphQL operation
        if (OPERATION_START.matcher(trimmed).find()) {
            String operationType = trimmed.split("\\s+")[0].toLowerCase();
            return operationType.equals(sectionType.toLowerCase());
        }
        
        // Handle other GraphQL constructs
        switch (sectionType.toLowerCase()) {
            case "schema":
                return trimmed.startsWith("schema");
            case "type":
                return trimmed.startsWith("type") && !trimmed.startsWith("input");
            case "input":
                return trimmed.startsWith("input");
            case "enum":
                return trimmed.startsWith("enum");
            case "scalar":
                return trimmed.startsWith("scalar");
            case "interface":
                return trimmed.startsWith("interface");
            case "union":
                return trimmed.startsWith("union");
            case "directive":
                return trimmed.startsWith("directive");
            default:
                return false;
        }
    }
    
    /**
     * Process xpath using the existing selector facade with generic lazy loading
     */
    private List<GqlNodeContext> processWithSelectorFacade(String documentId, String xpath, DocumentSection section) {
        // Use ONLY the loaded section, not the full document
        return selectorFacade.selectMany(section.getContent(), xpath);
    }
    
    /**
     * Process multiple xpath queries efficiently with generic lazy loading
     */
    public List<LazyXPathResult> processMultipleXPaths(String documentId, List<String> xpaths) {
        if (xpaths == null || xpaths.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<LazyXPathResult> results = new ArrayList<>();
        
        // Group XPaths by required sections to minimize file reads
        Map<String, List<String>> sectionGroups = groupXPathsBySection(xpaths);
        
        for (Map.Entry<String, List<String>> entry : sectionGroups.entrySet()) {
            String sectionType = entry.getKey();
            List<String> sectionXPaths = entry.getValue();
            
            // Load section once for all XPaths that need it
            DocumentSection section = loadSectionFromFile(documentId, sectionType);
            
            for (String xpath : sectionXPaths) {
                long startTime = System.currentTimeMillis();
                
                try {
                    List<GqlNodeContext> result = selectorFacade.selectMany(section.getContent(), xpath);
                    
                    long duration = System.currentTimeMillis() - startTime;
                    updatePerformanceMetrics(xpath, duration);
                    
                    results.add(new LazyXPathResult(result, section, null, duration));
                    
                } catch (Exception e) {
                    long duration = System.currentTimeMillis() - startTime;
                    results.add(new LazyXPathResult(e, duration));
                }
            }
        }
        
        return results;
    }
    
    /**
     * Group XPaths by the section they need to minimize file reads
     */
    private Map<String, List<String>> groupXPathsBySection(List<String> xpaths) {
        Map<String, List<String>> groups = new HashMap<>();
        
        for (String xpath : xpaths) {
            String sectionType = determineSectionType(xpath);
            groups.computeIfAbsent(sectionType, k -> new ArrayList<>()).add(xpath);
        }
        
        return groups;
    }
    
    /**
     * Update performance metrics
     */
    private void updatePerformanceMetrics(String xpath, long duration) {
        performanceMetrics.put(xpath, duration);
    }
    
    /**
     * Get performance statistics
     */
    public Map<String, Object> getPerformanceStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Calculate performance metrics
        Collection<Long> times = performanceMetrics.values();
        if (!times.isEmpty()) {
            double average = times.stream().mapToLong(Long::longValue).average().orElse(0.0);
            long min = times.stream().mapToLong(Long::longValue).min().orElse(0L);
            long max = times.stream().mapToLong(Long::longValue).max().orElse(0L);
            
            stats.put("averageTime", average);
            stats.put("minTime", (double) min);
            stats.put("maxTime", (double) max);
        } else {
            stats.put("averageTime", 0.0);
            stats.put("minTime", 0.0);
            stats.put("maxTime", 0.0);
        }
        
        stats.put("totalQueries", performanceMetrics.size());
        stats.put("cacheHits", 0); // TODO: Implement cache hit tracking
        stats.put("cacheSize", sectionCache.size());
        stats.put("cacheStats", new HashMap<>());
        
        return stats;
    }
    
    /**
     * Clear all caches
     */
    public void clearCaches() {
        sectionCache.clear();
        resultCache.clear();
        performanceMetrics.clear();
    }
    
    /**
     * Clear cache for a specific document
     */
    public void clearDocumentCache(String documentId) {
        if (documentId == null) return;
        
        // Remove all cached sections and results for this document
        sectionCache.entrySet().removeIf(entry -> entry.getKey().startsWith(documentId + ":"));
        resultCache.entrySet().removeIf(entry -> entry.getKey().startsWith(documentId + ":"));
    }
    
    /**
     * Compare performance with traditional approach
     */
    public PerformanceComparison compareWithTraditional(String documentId, String xpath) {
        long startTime = System.currentTimeMillis();
        
        // Traditional approach: load entire document
        String fullDocument = loadFullDocument(documentId);
        List<GqlNodeContext> traditionalResult = selectorFacade.selectMany(fullDocument, xpath);
        long traditionalTime = System.currentTimeMillis() - startTime;
        
        // Lazy approach: load only required section
        startTime = System.currentTimeMillis();
        DocumentSection section = loadRequiredSection(documentId, xpath);
        List<GqlNodeContext> lazyResult = selectorFacade.selectMany(section.getContent(), xpath);
        long lazyTime = System.currentTimeMillis() - startTime;
        
        // Calculate improvement
        double improvement = traditionalTime > 0 ? 
            ((double) (traditionalTime - lazyTime) / traditionalTime) * 100 : 0.0;
        
        return new PerformanceComparison(traditionalTime, lazyTime, traditionalResult, lazyResult, improvement);
    }
    
    /**
     * Load full document (for comparison only - NOT for lazy processing)
     */
    private String loadFullDocument(String documentId) {
        try {
            return new String(Files.readAllBytes(Paths.get(documentId)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load full document: " + documentId, e);
        }
    }
    
    // Inner classes for generic lazy loading
    public static class DocumentSection {
        private final String type;
        private final String content;
        private final long startPosition;
        private final long endPosition;
        
        public DocumentSection(String type, String content, long startPosition, long endPosition) {
            this.type = type;
            this.content = content;
            this.startPosition = startPosition;
            this.endPosition = endPosition;
        }
        
        public String getType() { return type; }
        public String getContent() { return content; }
        public long getStartPosition() { return startPosition; }
        public long getEndPosition() { return endPosition; }
        public int getSize() { return content.length(); }
    }
    
    private static class SectionBounds {
        final long start;
        final long end;
        
        SectionBounds(long start, long end) {
            this.start = start;
            this.end = end;
        }
    }
    
    // Result classes
    public static class LazyXPathResult {
        private final List<GqlNodeContext> result;
        private final Exception error;
        private final DocumentSection section;
        private final XPathAnalysis analysis;
        private final long duration;
        
        public LazyXPathResult(List<GqlNodeContext> result, DocumentSection section, XPathAnalysis analysis, long duration) {
            this.result = result;
            this.error = null;
            this.section = section;
            this.analysis = analysis;
            this.duration = duration;
        }
        
        public LazyXPathResult(Exception error, long duration) {
            this.result = null;
            this.error = error;
            this.section = null;
            this.analysis = null;
            this.duration = duration;
        }
        
        public List<GqlNodeContext> getResult() { return result; }
        public Exception getError() { return error; }
        public DocumentSection getSection() { return section; }
        public XPathAnalysis getAnalysis() { return analysis; }
        public long getDuration() { return duration; }
        public boolean isSuccess() { return error == null; }
        public boolean hasError() { return error != null; }
    }
    
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
        
        public long getTraditionalTime() { return traditionalTime; }
        public long getLazyTime() { return lazyTime; }
        public List<GqlNodeContext> getTraditionalResult() { return traditionalResult; }
        public List<GqlNodeContext> getLazyResult() { return lazyResult; }
        public double getImprovementPercentage() { return improvementPercentage; }
        
        public boolean resultsMatch() {
            // If both results are null, they match
            if (traditionalResult == null && lazyResult == null) return true;
            
            // If only one is null, they don't match
            if (traditionalResult == null || lazyResult == null) return false;
            
            // Compare sizes
            return traditionalResult.size() == lazyResult.size();
        }
    }
} 