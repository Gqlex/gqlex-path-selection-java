package com.intuit.gqlex.gqlxpath.lazy;

import java.util.*;

/**
 * Generic XPath analysis for ANY GraphQL structure
 * 
 * This class analyzes XPath expressions to determine:
 * - Required document sections
 * - Complexity level
 * - Resource requirements
 * - Optimization opportunities
 * 
 * No hardcoded assumptions about specific GraphQL schemas or field names
 */
public class XPathAnalysis {
    
    private final String xpath;
    private final List<String> requiredSections;
    private final ComplexityLevel complexity;
    private final Map<String, Object> metadata;
    private final List<String> optimizationHints;
    
    // Compatibility fields for existing code
    private final List<XPathComponent> components = new ArrayList<>();
    private final Map<String, String> predicates = new HashMap<>();
    private final Set<String> requiredSectionsSet = new HashSet<>();
    
    public XPathAnalysis(String xpath) {
        this.xpath = xpath;
        this.requiredSections = analyzeRequiredSections(xpath);
        this.complexity = determineComplexity(xpath);
        this.metadata = analyzeMetadata(xpath);
        this.optimizationHints = generateOptimizationHints(xpath);
        
        // Initialize compatibility collections
        this.requiredSectionsSet.addAll(this.requiredSections);
    }
    
    // Compatibility constructor for existing code
    public XPathAnalysis() {
        this.xpath = "";
        this.requiredSections = new ArrayList<>();
        this.complexity = ComplexityLevel.UNKNOWN;
        this.metadata = new HashMap<>();
        this.optimizationHints = new ArrayList<>();
    }
    
    // Compatibility methods for existing code
    public void addComponent(XPathComponent component) {
        this.components.add(component);
    }
    
    public void addRequiredSection(String section) {
        this.requiredSections.add(section);
        this.requiredSectionsSet.add(section);
    }
    
    public void addPredicate(String key, String value) {
        this.predicates.put(key, value);
    }
    
    public List<XPathComponent> getComponents() {
        return this.components;
    }
    
    public Map<String, String> getPredicates() {
        return this.predicates;
    }
    
    // Compatibility methods for DocumentSectionLoader
    public boolean requiresFragmentResolution() {
        return requiredSections.stream().anyMatch(s -> s.contains("fragment"));
    }
    
    public boolean requiresFieldResolution() {
        return requiredSections.stream().anyMatch(s -> s.contains("field"));
    }
    
    public boolean requiresArgumentResolution() {
        return requiredSections.stream().anyMatch(s -> s.contains("argument"));
    }
    
    public boolean requiresDirectiveResolution() {
        return requiredSections.stream().anyMatch(s -> s.contains("directive"));
    }
    
    /**
     * Analyze XPath to determine required document sections
     */
    private List<String> analyzeRequiredSections(String xpath) {
        List<String> sections = new ArrayList<>();
        
        if (xpath == null || xpath.trim().isEmpty()) {
            return sections;
        }
        
        // Generic section detection based on XPath structure
        if (xpath.contains("//query") || xpath.contains("query")) {
            sections.add("query");
        }
        if (xpath.contains("//mutation") || xpath.contains("mutation")) {
            sections.add("mutation");
        }
        if (xpath.contains("//subscription") || xpath.contains("subscription")) {
            sections.add("subscription");
        }
        if (xpath.contains("//fragment") || xpath.contains("fragment")) {
            sections.add("fragment");
        }
        if (xpath.contains("//schema") || xpath.contains("schema")) {
            sections.add("schema");
        }
        if (xpath.contains("//type") || xpath.contains("type")) {
            sections.add("type");
        }
        if (xpath.contains("//input") || xpath.contains("input")) {
            sections.add("input");
        }
        if (xpath.contains("//enum") || xpath.contains("enum")) {
            sections.add("enum");
        }
        if (xpath.contains("//scalar") || xpath.contains("scalar")) {
            sections.add("scalar");
        }
        if (xpath.contains("//interface") || xpath.contains("interface")) {
            sections.add("interface");
        }
        if (xpath.contains("//union") || xpath.contains("union")) {
            sections.add("union");
        }
        if (xpath.contains("//directive") || xpath.contains("directive")) {
            sections.add("directive");
        }
        
        // If no specific sections found, default to query
        if (sections.isEmpty()) {
            sections.add("query");
        }
        
        return sections;
    }
    
    /**
     * Determine XPath complexity level
     */
    private ComplexityLevel determineComplexity(String xpath) {
        if (xpath == null || xpath.trim().isEmpty()) {
            return ComplexityLevel.UNKNOWN;
        }
        
        // Count complexity indicators
        int complexityScore = 0;
        
        // Simple patterns
        if (xpath.matches("^//[a-zA-Z_][a-zA-Z0-9_]*$")) {
            complexityScore += 1;
        }
        
        // Field patterns
        if (xpath.matches("^//[a-zA-Z_][a-zA-Z0-9_]*/[a-zA-Z_][a-zA-Z0-9_]*$")) {
            complexityScore += 2;
        }
        
        // Predicates
        if (xpath.contains("[") && xpath.contains("]")) {
            complexityScore += 5;
        }
        
        // Functions
        if (xpath.contains("(") && xpath.contains(")")) {
            complexityScore += 3;
        }
        
        // Axes
        if (xpath.contains("::")) {
            complexityScore += 4;
        }
        
        // Wildcards
        if (xpath.contains("*")) {
            complexityScore += 2;
        }
        
        // Multiple paths
        if (xpath.contains("|")) {
            complexityScore += 6;
        }
        
        // Nested expressions
        if (xpath.contains("//") && xpath.split("//").length > 2) {
            complexityScore += 3;
        }
        
        // Determine level based on score
        if (complexityScore <= 2) {
            return ComplexityLevel.SIMPLE;
        } else if (complexityScore <= 5) {
            return ComplexityLevel.MEDIUM;
        } else if (complexityScore <= 10) {
            return ComplexityLevel.COMPLEX;
        } else {
            return ComplexityLevel.VERY_COMPLEX;
        }
    }
    
    /**
     * Analyze XPath metadata
     */
    private Map<String, Object> analyzeMetadata(String xpath) {
        Map<String, Object> metadata = new HashMap<>();
        
        if (xpath == null || xpath.trim().isEmpty()) {
            return metadata;
        }
        
        // Basic statistics
        metadata.put("length", xpath.length());
        metadata.put("depth", countDepth(xpath));
        metadata.put("hasPredicates", xpath.contains("[") && xpath.contains("]"));
        metadata.put("hasFunctions", xpath.contains("(") && xpath.contains(")"));
        metadata.put("hasAxes", xpath.contains("::"));
        metadata.put("hasWildcards", xpath.contains("*"));
        metadata.put("hasMultiplePaths", xpath.contains("|"));
        
        // Performance indicators
        metadata.put("estimatedComplexity", estimatePerformanceImpact(xpath));
        metadata.put("cacheable", isCacheable(xpath));
        metadata.put("parallelizable", isParallelizable(xpath));
        
        return metadata;
    }
    
    /**
     * Count XPath depth (number of levels)
     */
    private int countDepth(String xpath) {
        if (xpath == null) return 0;
        return xpath.split("//").length - 1;
    }
    
    /**
     * Estimate performance impact
     */
    private String estimatePerformanceImpact(String xpath) {
        if (xpath == null) return "UNKNOWN";
        
        if (xpath.matches("^//[a-zA-Z_][a-zA-Z0-9_]*$")) {
            return "LOW";
        } else if (xpath.contains("[") || xpath.contains("(")) {
            return "MEDIUM";
        } else if (xpath.contains("|") || xpath.contains("::")) {
            return "HIGH";
        } else {
            return "MEDIUM";
        }
    }
    
    /**
     * Check if XPath is cacheable
     */
    private boolean isCacheable(String xpath) {
        if (xpath == null) return false;
        
        // Simple XPaths are more cacheable
        return xpath.matches("^//[a-zA-Z_][a-zA-Z0-9_]*$") ||
               xpath.matches("^//[a-zA-Z_][a-zA-Z0-9_]*/[a-zA-Z_][a-zA-Z0-9_]*$");
    }
    
    /**
     * Check if XPath can be parallelized
     */
    private boolean isParallelizable(String xpath) {
        if (xpath == null) return false;
        
        // XPaths with multiple paths can be parallelized
        return xpath.contains("|");
    }
    
    /**
     * Generate optimization hints
     */
    private List<String> generateOptimizationHints(String xpath) {
        List<String> hints = new ArrayList<>();
        
        if (xpath == null || xpath.trim().isEmpty()) {
            return hints;
        }
        
        // Performance hints
        if (xpath.contains("|")) {
            hints.add("Consider parallel processing for multiple paths");
        }
        
        if (xpath.contains("[") && xpath.contains("]")) {
            hints.add("Predicates may benefit from indexed access");
        }
        
        if (xpath.contains("//") && xpath.split("//").length > 3) {
            hints.add("Deep nesting may benefit from lazy loading");
        }
        
        if (xpath.contains("*")) {
            hints.add("Wildcards may impact performance - consider specific paths");
        }
        
        if (isCacheable(xpath)) {
            hints.add("This XPath is highly cacheable");
        }
        
        return hints;
    }
    
    // Getters
    public String getXpath() { return xpath; }
    public List<String> getRequiredSections() { return requiredSections; }
    public Set<String> getRequiredSectionsSet() { return requiredSectionsSet; } // Compatibility method
    public ComplexityLevel getComplexity() { return complexity; }
    public Map<String, Object> getMetadata() { return metadata; }
    public List<String> getOptimizationHints() { return optimizationHints; }
    
    /**
     * Complexity levels
     */
    public enum ComplexityLevel {
        UNKNOWN,
        SIMPLE,
        MEDIUM,
        COMPLEX,
        VERY_COMPLEX
    }
    
    @Override
    public String toString() {
        return String.format("XPathAnalysis{xpath='%s', complexity=%s, sections=%s}", 
                           xpath, complexity, requiredSections);
    }
} 