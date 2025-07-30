package com.intuit.gqlex.transformation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.HashMap;

/**
 * Tests for the QueryTemplate class.
 */
class QueryTemplateTest {
    
    @Test
    void testBasicVariableSubstitution() {
        String template = "query Hero($episode: Episode) {\n" +
            "    hero(episode: ${episode}) {\n" +
            "        name\n" +
            "    }\n" +
            "}";
        
        QueryTemplate queryTemplate = new QueryTemplate(template);
        queryTemplate.setVariable("episode", "EMPIRE");
        
        String result = queryTemplate.render();
        
        assertNotNull(result);
        assertTrue(result.contains("EMPIRE"));
        assertFalse(result.contains("${episode}"));
    }
    
    @Test
    void testMultipleVariableSubstitution() {
        String template = "query User($userId: ID!, $includeProfile: Boolean!) {\n" +
            "    user(id: ${userId}) {\n" +
            "        id\n" +
            "        name\n" +
            "        profile @include(if: ${includeProfile}) {\n" +
            "            bio\n" +
            "        }\n" +
            "    }\n" +
            "}";
        
        QueryTemplate queryTemplate = new QueryTemplate(template);
        queryTemplate.setVariable("userId", "123");
        queryTemplate.setVariable("includeProfile", true);
        
        String result = queryTemplate.render();
        
        assertNotNull(result);
        assertTrue(result.contains("123"));
        assertTrue(result.contains("true"));
        assertFalse(result.contains("${userId}"));
        assertFalse(result.contains("${includeProfile}"));
    }
    
    @Test
    void testConditionalLogic() {
        String template = "query Dashboard {\n" +
            "    analytics {\n" +
            "        #if($showRevenue)\n" +
            "        revenue {\n" +
            "            total\n" +
            "            growth\n" +
            "        }\n" +
            "        #end\n" +
            "        #if($showUsers)\n" +
            "        users {\n" +
            "            active\n" +
            "            new\n" +
            "        }\n" +
            "        #end\n" +
            "    }\n" +
            "}";
        
        QueryTemplate queryTemplate = new QueryTemplate(template);
        queryTemplate.setCondition("showRevenue", true);
        queryTemplate.setCondition("showUsers", false);
        
        String result = queryTemplate.render();
        
        assertNotNull(result);
        assertTrue(result.contains("revenue"));
        assertTrue(result.contains("total"));
        assertTrue(result.contains("growth"));
        assertFalse(result.contains("users"));
        assertFalse(result.contains("active"));
        assertFalse(result.contains("new"));
    }
    
    @Test
    void testCombinedVariableAndConditional() {
        String template = "query Dashboard($dateRange: DateRange!) {\n" +
            "    analytics(dateRange: ${dateRange}) {\n" +
            "        #if($showRevenue)\n" +
            "        revenue {\n" +
            "            total\n" +
            "        }\n" +
            "        #end\n" +
            "        #if($showUsers)\n" +
            "        users {\n" +
            "            count: ${userCount}\n" +
            "        }\n" +
            "        #end\n" +
            "    }\n" +
            "}";
        
        QueryTemplate queryTemplate = new QueryTemplate(template);
        queryTemplate.setVariable("dateRange", "LAST_30_DAYS");
        queryTemplate.setVariable("userCount", 1000);
        queryTemplate.setCondition("showRevenue", true);
        queryTemplate.setCondition("showUsers", true);
        
        String result = queryTemplate.render();
        
        assertNotNull(result);
        assertTrue(result.contains("LAST_30_DAYS"));
        assertTrue(result.contains("1000"));
        assertTrue(result.contains("revenue"));
        assertTrue(result.contains("users"));
        assertFalse(result.contains("${dateRange}"));
        assertFalse(result.contains("${userCount}"));
    }
    
    @Test
    void testTemplateValidation() {
        String template = "query User($userId: ID!) {\n" +
            "    user(id: ${userId}) {\n" +
            "        name: ${userName}\n" +
            "    }\n" +
            "}";
        
        QueryTemplate queryTemplate = new QueryTemplate(template);
        
        // Should not be valid without all variables set
        assertFalse(queryTemplate.validate());
        
        // Set one variable
        queryTemplate.setVariable("userId", "123");
        assertFalse(queryTemplate.validate());
        
        // Set all variables
        queryTemplate.setVariable("userName", "John");
        assertTrue(queryTemplate.validate());
    }
    
    @Test
    void testMissingVariables() {
        String template = "query User($userId: ID!) {\n" +
            "    user(id: ${userId}) {\n" +
            "        name: ${userName}\n" +
            "        email: ${userEmail}\n" +
            "    }\n" +
            "}";
        
        QueryTemplate queryTemplate = new QueryTemplate(template);
        queryTemplate.setVariable("userId", "123");
        
        java.util.Set<String> missing = queryTemplate.getMissingVariables();
        
        assertNotNull(missing);
        assertEquals(2, missing.size());
        assertTrue(missing.contains("userName"));
        assertTrue(missing.contains("userEmail"));
    }
    
    @Test
    void testRenderAsDocument() {
        String template = "query Hero($episode: Episode) {\n" +
            "    hero(episode: ${episode}) {\n" +
            "        name\n" +
            "    }\n" +
            "}";
        
        QueryTemplate queryTemplate = new QueryTemplate(template);
        queryTemplate.setVariable("episode", "EMPIRE");
        
        graphql.language.Document document = queryTemplate.renderAsDocument();
        
        assertNotNull(document);
        assertNotNull(document.getDefinitions());
        assertFalse(document.getDefinitions().isEmpty());
    }
    
    @Test
    void testSetMultipleVariables() {
        String template = "query User($userId: ID!, $includeProfile: Boolean!) {\n" +
            "    user(id: ${userId}) {\n" +
            "        name\n" +
            "        profile @include(if: ${includeProfile}) {\n" +
            "            bio\n" +
            "        }\n" +
            "    }\n" +
            "}";
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("userId", "123");
        variables.put("includeProfile", true);
        
        QueryTemplate queryTemplate = new QueryTemplate(template);
        queryTemplate.setVariables(variables);
        
        assertTrue(queryTemplate.validate());
        
        String result = queryTemplate.render();
        assertTrue(result.contains("123"));
        assertTrue(result.contains("true"));
    }
    
    @Test
    void testSetMultipleConditions() {
        String template = "query Dashboard {\n" +
            "    analytics {\n" +
            "        #if($showRevenue)\n" +
            "        revenue {\n" +
            "            total\n" +
            "        }\n" +
            "        #end\n" +
            "        #if($showUsers)\n" +
            "        users {\n" +
            "            count\n" +
            "        }\n" +
            "        #end\n" +
            "    }\n" +
            "}";
        
        Map<String, Boolean> conditions = new HashMap<>();
        conditions.put("showRevenue", true);
        conditions.put("showUsers", false);
        
        QueryTemplate queryTemplate = new QueryTemplate(template);
        queryTemplate.setConditions(conditions);
        
        String result = queryTemplate.render();
        
        assertTrue(result.contains("revenue"));
        assertTrue(result.contains("total"));
        assertFalse(result.contains("users"));
        assertFalse(result.contains("count"));
    }
} 