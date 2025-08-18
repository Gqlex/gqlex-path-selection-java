package com.intuit.gqlex.gqlxpath.lazy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

/**
 * Simple tests for com.intuit.gqlex.gqlxpath.lazy package
 * Focus on basic functionality and constructor testing
 */
@Tag("lazy")
@Tag("simple")
@Tag("fast")
class SimpleLazyPackageTest {

    private LazyXPathProcessor processor;
    private XPathAnalyzer analyzer;
    private DocumentSectionLoader sectionLoader;

    @BeforeEach
    void setUp() {
        processor = new LazyXPathProcessor();
        analyzer = new XPathAnalyzer();
        sectionLoader = new DocumentSectionLoader();
    }

    // ===== Basic Constructor Tests =====

    @Test
    @DisplayName("LazyXPathProcessor - Constructor")
    void testLazyXPathProcessorConstructor() {
        assertNotNull(processor);
    }

    @Test
    @DisplayName("XPathAnalyzer - Constructor")
    void testXPathAnalyzerConstructor() {
        assertNotNull(analyzer);
    }

    @Test
    @DisplayName("DocumentSectionLoader - Constructor")
    void testDocumentSectionLoaderConstructor() {
        assertNotNull(sectionLoader);
    }

    // ===== XPathAnalyzer Tests =====

    @Test
    @DisplayName("XPathAnalyzer - Basic XPath analysis")
    void testBasicXPathAnalysis() {
        String xpath = "//hero";
        XPathAnalysis analysis = analyzer.analyzeXPath(xpath);
        
        assertNotNull(analysis);
        assertNotNull(analysis.getComponents());
        assertTrue(analysis.getComponents().size() > 0);
    }

    @Test
    @DisplayName("XPathAnalyzer - Empty XPath handling")
    void testEmptyXPathHandling() {
        String xpath = "";
        XPathAnalysis analysis = analyzer.analyzeXPath(xpath);
        
        assertNotNull(analysis);
        assertNotNull(analysis.getComponents());
        assertEquals(0, analysis.getComponents().size());
    }

    @Test
    @DisplayName("XPathAnalyzer - Simple XPath handling")
    void testSimpleXPathHandling() {
        String xpath = "//hero";
        XPathAnalysis analysis = analyzer.analyzeXPath(xpath);
        
        assertNotNull(analysis);
        assertNotNull(analysis.getComponents());
        assertTrue(analysis.getComponents().size() > 0);
    }

    // ===== XPathComponent Tests =====

    @Test
    @DisplayName("XPathComponent - Constructor and getters")
    void testXPathComponentConstructorAndGetters() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("type", "hero");
        
        XPathComponent component = new XPathComponent(XPathComponentType.FIELD, "hero", 0, attributes);
        
        assertEquals("hero", component.getValue());
        assertEquals(XPathComponentType.FIELD, component.getType());
        assertEquals(0, component.getPosition());
        assertEquals(attributes, component.getAttributes());
    }

    @Test
    @DisplayName("XPathComponent - Different component types")
    void testXPathComponentDifferentTypes() {
        XPathComponent fieldComponent = new XPathComponent(XPathComponentType.FIELD, "hero", 0);
        XPathComponent fragmentComponent = new XPathComponent(XPathComponentType.FRAGMENT, "HeroFields", 1);
        XPathComponent argumentComponent = new XPathComponent(XPathComponentType.ARGUMENT, "episode", 2);
        XPathComponent directiveComponent = new XPathComponent(XPathComponentType.DIRECTIVE, "include", 3);
        XPathComponent operationComponent = new XPathComponent(XPathComponentType.OPERATION, "query", 4);
        XPathComponent aliasComponent = new XPathComponent(XPathComponentType.ALIAS, "mainHero", 5);
        XPathComponent variableComponent = new XPathComponent(XPathComponentType.VARIABLE, "episode", 6);
        
        assertEquals(XPathComponentType.FIELD, fieldComponent.getType());
        assertEquals(XPathComponentType.FRAGMENT, fragmentComponent.getType());
        assertEquals(XPathComponentType.ARGUMENT, argumentComponent.getType());
        assertEquals(XPathComponentType.DIRECTIVE, directiveComponent.getType());
        assertEquals(XPathComponentType.OPERATION, operationComponent.getType());
        assertEquals(XPathComponentType.ALIAS, aliasComponent.getType());
        assertEquals(XPathComponentType.VARIABLE, variableComponent.getType());
    }

    // ===== XPathComponentType Tests =====

    @Test
    @DisplayName("XPathComponentType - All enum values")
    void testXPathComponentTypeEnumValues() {
        assertNotNull(XPathComponentType.FIELD);
        assertNotNull(XPathComponentType.FRAGMENT);
        assertNotNull(XPathComponentType.ARGUMENT);
        assertNotNull(XPathComponentType.DIRECTIVE);
        assertNotNull(XPathComponentType.OPERATION);
        assertNotNull(XPathComponentType.ALIAS);
        assertNotNull(XPathComponentType.VARIABLE);
    }

    // ===== XPathAnalysis Tests =====

    @Test
    @DisplayName("XPathAnalysis - Constructor and basic operations")
    void testXPathAnalysisConstructorAndBasicOperations() {
        XPathAnalysis analysis = new XPathAnalysis();
        
        assertNotNull(analysis.getComponents());
        assertNotNull(analysis.getRequiredSections());
        assertNotNull(analysis.getPredicates());
        
        assertEquals(0, analysis.getComponents().size());
        assertEquals(0, analysis.getRequiredSections().size());
        assertEquals(0, analysis.getPredicates().size());
    }

    @Test
    @DisplayName("XPathAnalysis - Adding components")
    void testXPathAnalysisAddingComponents() {
        XPathAnalysis analysis = new XPathAnalysis();
        
        XPathComponent component = new XPathComponent(XPathComponentType.FIELD, "hero", 0);
        analysis.addComponent(component);
        
        assertEquals(1, analysis.getComponents().size());
        assertEquals(component, analysis.getComponents().get(0));
    }

    @Test
    @DisplayName("XPathAnalysis - Adding required sections")
    void testXPathAnalysisAddingRequiredSections() {
        XPathAnalysis analysis = new XPathAnalysis();
        
        analysis.addRequiredSection("field:hero");
        analysis.addRequiredSection("fragment:HeroFields");
        
        assertEquals(2, analysis.getRequiredSections().size());
        assertTrue(analysis.getRequiredSections().contains("field:hero"));
        assertTrue(analysis.getRequiredSections().contains("fragment:HeroFields"));
    }

    @Test
    @DisplayName("XPathAnalysis - Adding predicates")
    void testXPathAnalysisAddingPredicates() {
        XPathAnalysis analysis = new XPathAnalysis();
        
        analysis.addPredicate("type", "hero");
        analysis.addPredicate("episode", "NEWHOPE");
        
        assertEquals(2, analysis.getPredicates().size());
        assertEquals("hero", analysis.getPredicates().get("type"));
        assertEquals("NEWHOPE", analysis.getPredicates().get("episode"));
    }

    // ===== LazyXPathProcessor Inner Classes Tests =====

    @Test
    @DisplayName("LazyXPathProcessor - DocumentSection inner class")
    void testDocumentSectionInnerClass() {
        LazyXPathProcessor.DocumentSection section = new LazyXPathProcessor.DocumentSection("test", "content", 0L, 100L);
        
        assertEquals("test", section.getType());
        assertEquals("content", section.getContent());
        assertEquals(0L, section.getStartPosition());
        assertEquals(100L, section.getEndPosition());
        assertEquals(7, section.getSize()); // "content" length
    }

    @Test
    @DisplayName("LazyXPathProcessor - LazyXPathResult success constructor")
    void testLazyXPathResultSuccessConstructor() {
        List<com.intuit.gqlex.common.GqlNodeContext> mockResult = new ArrayList<>();
        LazyXPathProcessor.DocumentSection section = new LazyXPathProcessor.DocumentSection("test", "content", 0L, 100L);
        
        LazyXPathProcessor.LazyXPathResult result = new LazyXPathProcessor.LazyXPathResult(
            mockResult, section, null, 100L);
        
        assertNotNull(result.getResult());
        assertNull(result.getError());
        assertEquals(section, result.getSection());
        assertEquals(100L, result.getDuration());
        assertTrue(result.isSuccess());
        assertFalse(result.hasError());
    }

    @Test
    @DisplayName("LazyXPathProcessor - LazyXPathResult error constructor")
    void testLazyXPathResultErrorConstructor() {
        Exception error = new RuntimeException("Test error");
        LazyXPathProcessor.LazyXPathResult result = new LazyXPathProcessor.LazyXPathResult(error, 100L);
        
        assertNull(result.getResult());
        assertEquals(error, result.getError());
        assertNull(result.getSection());
        assertEquals(100L, result.getDuration());
        assertFalse(result.isSuccess());
        assertTrue(result.hasError());
    }

    @Test
    @DisplayName("LazyXPathProcessor - PerformanceComparison constructor and methods")
    void testPerformanceComparisonConstructorAndMethods() {
        List<com.intuit.gqlex.common.GqlNodeContext> traditionalResult = Arrays.asList();
        List<com.intuit.gqlex.common.GqlNodeContext> lazyResult = Arrays.asList();
        
        LazyXPathProcessor.PerformanceComparison comparison = new LazyXPathProcessor.PerformanceComparison(
            100L, 50L, traditionalResult, lazyResult, 50.0);
        
        assertEquals(100L, comparison.getTraditionalTime());
        assertEquals(50L, comparison.getLazyTime());
        assertEquals(traditionalResult, comparison.getTraditionalResult());
        assertEquals(lazyResult, comparison.getLazyResult());
        assertEquals(50.0, comparison.getImprovementPercentage());
        assertTrue(comparison.resultsMatch());
    }

    @Test
    @DisplayName("LazyXPathProcessor - PerformanceComparison resultsMatch edge cases")
    void testPerformanceComparisonResultsMatchEdgeCases() {
        // Both null
        LazyXPathProcessor.PerformanceComparison comparison1 = new LazyXPathProcessor.PerformanceComparison(
            100L, 50L, null, null, 50.0);
        assertTrue(comparison1.resultsMatch());
        
        // One null
        LazyXPathProcessor.PerformanceComparison comparison2 = new LazyXPathProcessor.PerformanceComparison(
            100L, 50L, Arrays.asList(), null, 50.0);
        assertFalse(comparison2.resultsMatch());
        
        // Different sizes
        LazyXPathProcessor.PerformanceComparison comparison3 = new LazyXPathProcessor.PerformanceComparison(
            100L, 50L, Arrays.asList(), Arrays.asList(), 50.0);
        assertTrue(comparison3.resultsMatch());
    }

    // ===== Performance Stats Tests =====

    @Test
    @DisplayName("LazyXPathProcessor - Performance stats initialization")
    void testPerformanceStatsInitialization() {
        Map<String, Object> stats = processor.getPerformanceStats();
        
        assertNotNull(stats);
        assertEquals(0.0, stats.get("averageTime"));
        assertEquals(0.0, stats.get("minTime"));
        assertEquals(0.0, stats.get("maxTime"));
        assertEquals(0, stats.get("totalQueries"));
        assertEquals(0, stats.get("cacheHits"));
        assertEquals(0, stats.get("cacheSize"));
    }

    @Test
    @DisplayName("LazyXPathProcessor - Cache operations")
    void testCacheOperations() {
        // Test cache clearing
        processor.clearCaches();
        
        // Test document-specific cache clearing
        processor.clearDocumentCache("test");
        
        // Verify caches are empty
        Map<String, Object> stats = processor.getPerformanceStats();
        assertEquals(0, stats.get("cacheSize"));
    }
}
