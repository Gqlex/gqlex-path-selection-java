package com.intuit.gqlex.gqlxpath.lazy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Loads only the required sections of GraphQL documents based on xpath analysis
 */
public class DocumentSectionLoader {
    
    private final Map<String, DocumentSection> sectionCache = new ConcurrentHashMap<>();
    private final XPathAnalyzer xPathAnalyzer;
    
    public DocumentSectionLoader() {
        this.xPathAnalyzer = new XPathAnalyzer();
    }
    
    /**
     * Load only the required section of the document based on xpath
     */
    public DocumentSection loadSection(String documentId, String xpath) {
        String cacheKey = documentId + ":" + xpath;
        
        return sectionCache.computeIfAbsent(cacheKey, key -> {
            try {
                return parseSection(documentId, xpath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load section for xpath: " + xpath, e);
            }
        });
    }
    
    /**
     * Parse only the section needed for the xpath
     */
    private DocumentSection parseSection(String documentId, String xpath) throws IOException {
        // Analyze xpath to determine required sections
        XPathAnalysis analysis = xPathAnalyzer.analyzeXPath(xpath);
        
        // Load only required tokens/sections
        String content = loadRequiredContent(documentId, analysis);
        
        // Build partial AST for required section
        return buildPartialAST(documentId, content, analysis);
    }
    
    /**
     * Load only the required content from document
     */
    private String loadRequiredContent(String documentId, XPathAnalysis analysis) throws IOException {
        Set<String> requiredSections = analysis.getRequiredSectionsSet();
        
        if (requiredSections.isEmpty()) {
            // If no specific sections required, load minimal content
            return loadMinimalContent(documentId);
        }
        
        // Load specific sections
        StringBuilder content = new StringBuilder();
        for (String section : requiredSections) {
            String sectionContent = loadSectionContent(documentId, section);
            if (sectionContent != null) {
                content.append(sectionContent).append("\n");
            }
        }
        
        return content.toString();
    }
    
    /**
     * Load minimal content for basic xpath queries
     */
    private String loadMinimalContent(String documentId) throws IOException {
        try (FileChannel channel = FileChannel.open(Paths.get(documentId))) {
            // Read first 1KB to get basic structure
            int size = Math.min(1024, (int) channel.size());
            ByteBuffer buffer = ByteBuffer.allocate(size);
            channel.read(buffer);
            
            return new String(buffer.array()).trim();
        }
    }
    
    /**
     * Load content for a specific section
     */
    private String loadSectionContent(String documentId, String section) throws IOException {
        try (FileChannel channel = FileChannel.open(Paths.get(documentId))) {
            // Find section in document
            long offset = findSectionOffset(channel, section);
            if (offset == -1) {
                return null;
            }
            
            // Read section content
            int size = estimateSectionSize(channel, offset);
            ByteBuffer buffer = ByteBuffer.allocate(size);
            channel.position(offset);
            channel.read(buffer);
            
            return new String(buffer.array()).trim();
        }
    }
    
    /**
     * Find the offset of a section in the document
     */
    private long findSectionOffset(FileChannel channel, String section) throws IOException {
        // Simple pattern matching to find section
        String[] patterns = {
            "field:" + section.replace("field:", ""),
            "fragment " + section.replace("fragment:", ""),
            "query " + section.replace("operation:", ""),
            "mutation " + section.replace("operation:", ""),
            "subscription " + section.replace("operation:", "")
        };
        
        for (String pattern : patterns) {
            long offset = findPatternInChannel(channel, pattern);
            if (offset != -1) {
                return offset;
            }
        }
        
        return -1;
    }
    
    /**
     * Find a pattern in the file channel
     */
    private long findPatternInChannel(FileChannel channel, String pattern) throws IOException {
        Pattern regex = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        ByteBuffer buffer = ByteBuffer.allocate(8192); // 8KB buffer
        long position = 0;
        
        while (position < channel.size()) {
            buffer.clear();
            channel.position(position);
            int bytesRead = channel.read(buffer);
            
            if (bytesRead == -1) break;
            
            String chunk = new String(buffer.array(), 0, bytesRead);
            Matcher matcher = regex.matcher(chunk);
            
            if (matcher.find()) {
                return position + matcher.start();
            }
            
            position += bytesRead - pattern.length(); // Overlap to catch patterns at boundaries
        }
        
        return -1;
    }
    
    /**
     * Estimate the size of a section
     */
    private int estimateSectionSize(FileChannel channel, long offset) throws IOException {
        // Read ahead to find section boundaries
        ByteBuffer buffer = ByteBuffer.allocate(4096); // 4KB buffer
        channel.position(offset);
        int bytesRead = channel.read(buffer);
        
        if (bytesRead == -1) return 0;
        
        String content = new String(buffer.array(), 0, bytesRead);
        
        // Find section boundaries (simplified)
        int endIndex = findSectionEnd(content);
        if (endIndex != -1) {
            return endIndex;
        }
        
        return Math.min(4096, (int) (channel.size() - offset));
    }
    
    /**
     * Find the end of a section
     */
    private int findSectionEnd(String content) {
        // Simple heuristics to find section end
        int braceCount = 0;
        boolean inString = false;
        char stringChar = 0;
        
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            
            if (inString) {
                if (c == stringChar) {
                    inString = false;
                }
                continue;
            }
            
            if (c == '"' || c == '\'') {
                inString = true;
                stringChar = c;
                continue;
            }
            
            if (c == '{') {
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0) {
                    return i + 1;
                }
            }
        }
        
        return -1;
    }
    
    /**
     * Build partial AST for the required section
     */
    private DocumentSection buildPartialAST(String documentId, String content, XPathAnalysis analysis) {
        DocumentSection.SectionType sectionType = determineSectionType(analysis);
        DocumentSection section = new DocumentSection(documentId, 0, content.length(), content, sectionType);
        
        // Parse content and add nodes
        List<DocumentSection.GraphQLNode> nodes = parseNodes(content, analysis);
        for (DocumentSection.GraphQLNode node : nodes) {
            section.addNode(node);
        }
        
        return section;
    }
    
    /**
     * Determine the section type based on analysis
     */
    private DocumentSection.SectionType determineSectionType(XPathAnalysis analysis) {
        if (analysis.requiresFragmentResolution()) {
            return DocumentSection.SectionType.FRAGMENT;
        } else if (analysis.requiresFieldResolution()) {
            return DocumentSection.SectionType.FIELD;
        } else if (analysis.requiresArgumentResolution()) {
            return DocumentSection.SectionType.ARGUMENT;
        } else if (analysis.requiresDirectiveResolution()) {
            return DocumentSection.SectionType.DIRECTIVE;
        } else {
            return DocumentSection.SectionType.OPERATION;
        }
    }
    
    /**
     * Parse nodes from content
     */
    private List<DocumentSection.GraphQLNode> parseNodes(String content, XPathAnalysis analysis) {
        List<DocumentSection.GraphQLNode> nodes = new ArrayList<>();
        
        // Simple regex-based parsing for performance
        if (analysis.requiresFieldResolution()) {
            nodes.addAll(parseFieldNodes(content));
        }
        
        if (analysis.requiresFragmentResolution()) {
            nodes.addAll(parseFragmentNodes(content));
        }
        
        if (analysis.requiresArgumentResolution()) {
            nodes.addAll(parseArgumentNodes(content));
        }
        
        if (analysis.requiresDirectiveResolution()) {
            nodes.addAll(parseDirectiveNodes(content));
        }
        
        return nodes;
    }
    
    /**
     * Parse field nodes from content
     */
    private List<DocumentSection.GraphQLNode> parseFieldNodes(String content) {
        List<DocumentSection.GraphQLNode> nodes = new ArrayList<>();
        Pattern fieldPattern = Pattern.compile("\\b([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\{?");
        Matcher matcher = fieldPattern.matcher(content);
        
        int position = 0;
        while (matcher.find()) {
            String fieldName = matcher.group(1);
            if (!isReservedWord(fieldName)) {
                nodes.add(new DocumentSection.GraphQLNode(
                    DocumentSection.NodeType.FIELD,
                    fieldName,
                    matcher.group(),
                    position + matcher.start()
                ));
            }
            position = matcher.end();
        }
        
        return nodes;
    }
    
    /**
     * Parse fragment nodes from content
     */
    private List<DocumentSection.GraphQLNode> parseFragmentNodes(String content) {
        List<DocumentSection.GraphQLNode> nodes = new ArrayList<>();
        Pattern fragmentPattern = Pattern.compile("fragment\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s+on\\s+([a-zA-Z_][a-zA-Z0-9_]*)");
        Matcher matcher = fragmentPattern.matcher(content);
        
        while (matcher.find()) {
            nodes.add(new DocumentSection.GraphQLNode(
                DocumentSection.NodeType.FRAGMENT_SPREAD,
                matcher.group(1),
                matcher.group(2),
                matcher.start()
            ));
        }
        
        return nodes;
    }
    
    /**
     * Parse argument nodes from content
     */
    private List<DocumentSection.GraphQLNode> parseArgumentNodes(String content) {
        List<DocumentSection.GraphQLNode> nodes = new ArrayList<>();
        Pattern argPattern = Pattern.compile("([a-zA-Z_][a-zA-Z0-9_]*)\\s*:\\s*([^,\\s]+)");
        Matcher matcher = argPattern.matcher(content);
        
        while (matcher.find()) {
            nodes.add(new DocumentSection.GraphQLNode(
                DocumentSection.NodeType.ARGUMENT,
                matcher.group(1),
                matcher.group(2),
                matcher.start()
            ));
        }
        
        return nodes;
    }
    
    /**
     * Parse directive nodes from content
     */
    private List<DocumentSection.GraphQLNode> parseDirectiveNodes(String content) {
        List<DocumentSection.GraphQLNode> nodes = new ArrayList<>();
        Pattern directivePattern = Pattern.compile("@([a-zA-Z_][a-zA-Z0-9_]*)");
        Matcher matcher = directivePattern.matcher(content);
        
        while (matcher.find()) {
            nodes.add(new DocumentSection.GraphQLNode(
                DocumentSection.NodeType.DIRECTIVE,
                matcher.group(1),
                matcher.group(),
                matcher.start()
            ));
        }
        
        return nodes;
    }
    
    /**
     * Check if a word is a GraphQL reserved word
     */
    private boolean isReservedWord(String word) {
        Set<String> reservedWords = Set.of(
            "query", "mutation", "subscription", "fragment", "on", "type", "interface",
            "union", "enum", "input", "scalar", "directive", "schema", "extend"
        );
        return reservedWords.contains(word.toLowerCase());
    }
    
    /**
     * Clear cache for a specific document
     */
    public void clearCache(String documentId) {
        sectionCache.entrySet().removeIf(entry -> entry.getKey().startsWith(documentId + ":"));
    }
    
    /**
     * Clear all cache
     */
    public void clearAllCache() {
        sectionCache.clear();
    }
    
    /**
     * Get cache statistics
     */
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("cacheSize", sectionCache.size());
        stats.put("cachedSections", new ArrayList<>(sectionCache.keySet()));
        return stats;
    }
} 