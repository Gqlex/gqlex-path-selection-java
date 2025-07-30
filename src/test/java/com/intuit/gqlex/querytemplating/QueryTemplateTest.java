package com.intuit.gqlex.querytemplating;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Comprehensive tests for QueryTemplate functionality
 * 
 * Tests cover:
 * - Variable substitution
 * - Conditional blocks
 * - File loading
 * - Error handling
 * - Edge cases
 * 
 * @author gqlex-team
 * @version 1.0.0
 */
class QueryTemplateTest {
    
    @Test
    void testBasicVariableSubstitution() throws TemplateException {
        String templateString = "query GetUser($userId: ID!) {\n" +
            "  user(id: ${userId}) {\n" +
            "    id\n" +
            "    name\n" +
            "    email\n" +
            "  }\n" +
            "}";
        
        QueryTemplate template = QueryTemplate.fromString(templateString);
        template.setVariable("userId", "12345");
        
        String result = template.render();
        
        assertTrue(result.contains("user(id: \"12345\")"));
        assertTrue(result.contains("query GetUser($userId: ID!)"));
    }
    
    @Test
    void testMultipleVariableSubstitution() throws TemplateException {
        String templateString = "query GetUser($userId: ID!, $includeProfile: Boolean!) {\n" +
            "  user(id: ${userId}) {\n" +
            "    id\n" +
            "    name\n" +
            "    email\n" +
            "    profile @include(if: ${includeProfile}) {\n" +
            "      bio\n" +
            "      avatar\n" +
            "    }\n" +
            "  }\n" +
            "}";
        
        QueryTemplate template = QueryTemplate.fromString(templateString);
        template.setVariable("userId", "12345")
               .setVariable("includeProfile", true);
        
        String result = template.render();
        
        assertTrue(result.contains("user(id: \"12345\")"));
        assertTrue(result.contains("profile @include(if: true)"));
    }
    
    @Test
    void testConditionalBlockIncluded() throws TemplateException {
        String templateString = "query GetUser($userId: ID!) {\n" +
            "  user(id: ${userId}) {\n" +
            "    id\n" +
            "    name\n" +
            "    email\n" +
            "    #if($includeProfile)\n" +
            "    profile {\n" +
            "      bio\n" +
            "      avatar\n" +
            "    }\n" +
            "    #end\n" +
            "  }\n" +
            "}";
        
        QueryTemplate template = QueryTemplate.fromString(templateString);
        template.setVariable("userId", "12345")
               .setCondition("includeProfile", true);
        
        String result = template.render();
        
        assertTrue(result.contains("profile {"));
        assertTrue(result.contains("bio"));
        assertTrue(result.contains("avatar"));
    }
    
    @Test
    void testConditionalBlockExcluded() throws TemplateException {
        String templateString = "query GetUser($userId: ID!) {\n" +
            "  user(id: ${userId}) {\n" +
            "    id\n" +
            "    name\n" +
            "    email\n" +
            "    #if($includeProfile)\n" +
            "    profile {\n" +
            "      bio\n" +
            "      avatar\n" +
            "    }\n" +
            "    #end\n" +
            "  }\n" +
            "}";
        
        QueryTemplate template = QueryTemplate.fromString(templateString);
        template.setVariable("userId", "12345")
               .setCondition("includeProfile", false);
        
        String result = template.render();
        
        assertFalse(result.contains("profile {"));
        assertFalse(result.contains("bio"));
        assertFalse(result.contains("avatar"));
    }
    
    @Test
    void testMutationTemplate() throws TemplateException {
        String templateString = "mutation UpdateUser($userId: ID!, $input: UserInput!) {\n" +
            "  updateUser(id: ${userId}, input: ${input}) {\n" +
            "    id\n" +
            "    name\n" +
            "    email\n" +
            "    #if($includeProfile)\n" +
            "    profile {\n" +
            "      bio\n" +
            "      avatar\n" +
            "    }\n" +
            "    #end\n" +
            "  }\n" +
            "}";
        
        QueryTemplate template = QueryTemplate.fromString(templateString);
        template.setVariable("userId", "12345")
               .setVariable("input", "{name: \"John Doe\", email: \"john@example.com\"}")
               .setCondition("includeProfile", true);
        
        String result = template.render();
        
        assertTrue(result.contains("mutation UpdateUser"));
        assertTrue(result.contains("updateUser(id: \"12345\", input: {name: \"John Doe\", email: \"john@example.com\"})"));
        assertTrue(result.contains("profile {"));
    }
    
    @Test
    void testSubscriptionTemplate() throws TemplateException {
        String templateString = "subscription UserUpdates($userId: ID!) {\n" +
            "  userUpdates(userId: ${userId}) {\n" +
            "    id\n" +
            "    name\n" +
            "    email\n" +
            "    #if($includeActivity)\n" +
            "    activity {\n" +
            "      type\n" +
            "      timestamp\n" +
            "    }\n" +
            "    #end\n" +
            "  }\n" +
            "}";
        
        QueryTemplate template = QueryTemplate.fromString(templateString);
        template.setVariable("userId", "12345")
               .setCondition("includeActivity", true);
        
        String result = template.render();
        
        assertTrue(result.contains("subscription UserUpdates"));
        assertTrue(result.contains("userUpdates(userId: \"12345\")"));
        assertTrue(result.contains("activity {"));
    }
    
    @Test
    void testMissingVariableThrowsException() {
        String templateString = "query GetUser($userId: ID!) {\n" +
            "  user(id: ${userId}) {\n" +
            "    id\n" +
            "    name\n" +
            "    email\n" +
            "  }\n" +
            "}";
        
        QueryTemplate template = QueryTemplate.fromString(templateString);
        // Don't set the userId variable
        
        assertThrows(TemplateException.class, () -> template.render());
    }
    
    @Test
    void testDifferentValueTypes() throws TemplateException {
        String templateString = "query Test($stringVar: String!, $intVar: Int!, $boolVar: Boolean!) {\n" +
            "  test(\n" +
            "    stringValue: ${stringVar}\n" +
            "    intValue: ${intVar}\n" +
            "    boolValue: ${boolVar}\n" +
            "  ) {\n" +
            "    id\n" +
            "  }\n" +
            "}";
        
        QueryTemplate template = QueryTemplate.fromString(templateString);
        template.setVariable("stringVar", "hello")
               .setVariable("intVar", 42)
               .setVariable("boolVar", false);
        
        String result = template.render();
        
        assertTrue(result.contains("stringValue: \"hello\""));
        assertTrue(result.contains("intValue: 42"));
        assertTrue(result.contains("boolValue: false"));
    }
    
    @Test
    void testNullValueHandling() throws TemplateException {
        String templateString = "query Test($var: String) {\n" +
            "  test(value: ${var}) {\n" +
            "    id\n" +
            "  }\n" +
            "}";
        
        QueryTemplate template = QueryTemplate.fromString(templateString);
        template.setVariable("var", null);
        
        String result = template.render();
        
        assertTrue(result.contains("value: null"));
    }
} 