package com.intuit.gqlex.validation.rules;

import com.intuit.gqlex.validation.core.GraphQLValidator;
import com.intuit.gqlex.validation.core.ValidationResult;
import com.intuit.gqlex.validation.core.ValidationLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for all validation rules.
 * These tests verify that all rules are completely generic
 * and work with any GraphQL query or mutation.
 */
@DisplayName("Validation Rules Tests")
public class ValidationRulesTest {
    
    private GraphQLValidator validator;
    
    @BeforeEach
    void setUp() {
        validator = new GraphQLValidator();
        validator.addRule(new StructuralRule());
        validator.addRule(new PerformanceRule());
        validator.addRule(new SecurityRule());
    }
    
    @Test
    @DisplayName("Structural Rule - Generic Field Validation")
    void testStructuralRuleGenericFieldValidation() {
        // Test with various field names
        String[] validQueries = {
            "query { user { id name } }",
            "query { product { id name price } }",
            "query { order { id items { product { name } } } }",
            "mutation { createUser(input: {name: \"John\"}) { id } }",
            "subscription { userUpdated { id name } }"
        };
        
        for (String query : validQueries) {
            ValidationResult result = validator.validate(query);
            assertTrue(result.isValid(), "Valid query should pass structural validation: " + query);
        }
    }
    
    @Test
    @DisplayName("Performance Rule - Generic Performance Validation")
    void testPerformanceRuleGenericPerformanceValidation() {
        // Test with queries that should trigger performance warnings
        String deepQuery = "query { " +
            "user { " +
            "  profile { " +
            "    details { " +
            "      preferences { " +
            "        settings { " +
            "          options { " +
            "            values { " +
            "              items { " +
            "                data { " +
            "                  content { " +
            "                    metadata { " +
            "                      info { " +
            "                        description { " +
            "                          text { " +
            "                            value " +
            "                          } " +
            "                        } " +
            "                      } " +
            "                    } " +
            "                  } " +
            "                } " +
            "              } " +
            "            } " +
            "          } " +
            "        } " +
            "      } " +
            "    } " +
            "  } " +
            "} " +
            "}";
        
        ValidationResult result = validator.validate(deepQuery);
        
        // Should have performance warnings but still be valid
        assertTrue(result.isValid(), "Deep query should be structurally valid");
        assertTrue(result.hasWarnings(), "Deep query should have performance warnings");
        
        // Check for performance warnings
        boolean hasPerformanceWarning = result.getWarnings().stream()
            .anyMatch(warning -> warning.getRuleName().equals("PERFORMANCE"));
        assertTrue(hasPerformanceWarning, "Should have performance warnings");
    }
    
    @Test
    @DisplayName("Security Rule - Generic Security Validation")
    void testSecurityRuleGenericSecurityValidation() {
        // Test with queries that should trigger security warnings
        String introspectionQuery = "query { __schema { types { name } } }";
        
        ValidationResult result = validator.validate(introspectionQuery);
        
        // Should have security errors for introspection
        assertFalse(result.isValid(), "Introspection query should fail security validation");
        assertTrue(result.getErrorCount() > 0, "Should have security errors");
        
        // Check for security errors
        boolean hasSecurityError = result.getErrors().stream()
            .anyMatch(error -> error.getRuleName().equals("SECURITY"));
        assertTrue(hasSecurityError, "Should have security errors");
    }
    
    @Test
    @DisplayName("All Rules - Generic Integration")
    void testAllRulesGenericIntegration() {
        // Test with a complex query that should trigger multiple rule types
        String complexQuery = 
            "query {\n" +
            "  user(id: 1) {\n" +
            "    profile {\n" +
            "      details {\n" +
            "        preferences {\n" +
            "          settings {\n" +
            "            options {\n" +
            "              values {\n" +
            "                items {\n" +
            "                  data {\n" +
            "                    content {\n" +
            "                      metadata {\n" +
            "                        info {\n" +
            "                          description {\n" +
            "                            text {\n" +
            "                              value\n" +
            "                            }\n" +
            "                          }\n" +
            "                        }\n" +
            "                      }\n" +
            "                    }\n" +
            "                  }\n" +
            "                }\n" +
            "              }\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";
        
        ValidationResult result = validator.validate(complexQuery);
        
        // Should be structurally valid but have performance warnings
        assertTrue(result.isValid(), "Complex query should be structurally valid");
        assertTrue(result.hasWarnings(), "Complex query should have performance warnings");
        
        // Check for different rule categories
        boolean hasStructuralRule = result.getAllIssues().stream()
            .anyMatch(issue -> issue.getRuleName().equals("STRUCTURAL"));
        boolean hasPerformanceRule = result.getAllIssues().stream()
            .anyMatch(issue -> issue.getRuleName().equals("PERFORMANCE"));
        
        assertTrue(hasStructuralRule || hasPerformanceRule, 
            "Should have issues from structural or performance rules");
    }
    
    @Test
    @DisplayName("Generic Rule Categories")
    void testGenericRuleCategories() {
        // Test rule categorization
        StructuralRule structuralRule = new StructuralRule();
        PerformanceRule performanceRule = new PerformanceRule();
        SecurityRule securityRule = new SecurityRule();
        
        assertEquals("STRUCTURAL", structuralRule.getCategory());
        assertEquals("PERFORMANCE", performanceRule.getCategory());
        assertEquals("SECURITY", securityRule.getCategory());
        
        assertEquals(ValidationLevel.ERROR, structuralRule.getLevel());
        assertEquals(ValidationLevel.WARNING, performanceRule.getLevel());
        assertEquals(ValidationLevel.ERROR, securityRule.getLevel());
    }
    
    @Test
    @DisplayName("Generic Rule Management")
    void testGenericRuleManagement() {
        // Test rule management with different rule types
        GraphQLValidator customValidator = new GraphQLValidator();
        
        assertEquals(0, customValidator.getRuleCount());
        
        customValidator.addRule(new StructuralRule());
        assertEquals(1, customValidator.getRuleCount());
        
        customValidator.addRule(new PerformanceRule());
        assertEquals(2, customValidator.getRuleCount());
        
        customValidator.addRule(new SecurityRule());
        assertEquals(3, customValidator.getRuleCount());
        
        // Test rule filtering by category
        assertEquals(1, customValidator.getRulesByCategory("STRUCTURAL").size());
        assertEquals(1, customValidator.getRulesByCategory("PERFORMANCE").size());
        assertEquals(1, customValidator.getRulesByCategory("SECURITY").size());
        
        // Test rule filtering by level
        assertEquals(2, customValidator.getRulesByLevel(ValidationLevel.ERROR).size());
        assertEquals(1, customValidator.getRulesByLevel(ValidationLevel.WARNING).size());
    }
} 