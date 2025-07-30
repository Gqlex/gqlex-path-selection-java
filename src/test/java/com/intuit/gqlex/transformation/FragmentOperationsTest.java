package com.intuit.gqlex.transformation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for Fragment Operations
 * 
 * Tests cover:
 * - Inline fragments operation
 * - Extract fragment operation
 * - Complex fragment scenarios
 * - Error handling
 * - Generic and agnostic behavior
 * 
 * @author gqlex-team
 * @version 1.0.0
 */
class FragmentOperationsTest {
    
    @Test
    void testInlineFragmentsBasic() {
        String queryWithFragments = "query GetUser($userId: ID!) {\n" +
            "  user(id: $userId) {\n" +
            "    id\n" +
            "    name\n" +
            "    ...UserProfile\n" +
            "    ...UserPosts\n" +
            "  }\n" +
            "}\n" +
            "\n" +
            "fragment UserProfile on User {\n" +
            "  email\n" +
            "  profile {\n" +
            "    bio\n" +
            "    avatar\n" +
            "  }\n" +
            "}\n" +
            "\n" +
            "fragment UserPosts on User {\n" +
            "  posts {\n" +
            "    id\n" +
            "    title\n" +
            "  }\n" +
            "}";
        
        GraphQLTransformer transformer = new GraphQLTransformer(queryWithFragments);
        TransformationResult result = transformer.inlineAllFragments().transform();
        
        assertTrue(result.isSuccess());
        String transformedQuery = result.getQueryString();
        
        // Check that fragments are inlined
        assertTrue(transformedQuery.contains("email"));
        assertTrue(transformedQuery.contains("profile"));
        
        // Check that fragment definition is removed
        assertFalse(transformedQuery.contains("fragment UserProfile"));
        assertFalse(transformedQuery.contains("...UserProfile"));
    }
    
    @Test
    void testInlineFragmentsNested() {
        String queryWithNestedFragments = "query GetUser($userId: ID!) {\n" +
            "  user(id: $userId) {\n" +
            "    id\n" +
            "    name\n" +
            "    ...UserProfile\n" +
            "  }\n" +
            "}\n" +
            "\n" +
            "fragment UserProfile on User {\n" +
            "  email\n" +
            "  profile {\n" +
            "    ...ProfileDetails\n" +
            "  }\n" +
            "}\n" +
            "\n" +
            "fragment ProfileDetails on Profile {\n" +
            "  bio\n" +
            "  avatar\n" +
            "  location {\n" +
            "    city\n" +
            "    country\n" +
            "  }\n" +
            "}";
        
        GraphQLTransformer transformer = new GraphQLTransformer(queryWithNestedFragments);
        TransformationResult result = transformer.inlineAllFragments().transform();
        
        assertTrue(result.isSuccess());
        String transformedQuery = result.getQueryString();
        

        
        // Check that nested fragments are inlined
        assertTrue(transformedQuery.contains("email"));
        assertTrue(transformedQuery.contains("profile"));
        assertTrue(transformedQuery.contains("bio"));
        assertTrue(transformedQuery.contains("avatar"));
        assertTrue(transformedQuery.contains("location"));
        assertTrue(transformedQuery.contains("city"));
        assertTrue(transformedQuery.contains("country"));
        
        // Check that all fragment definitions are removed
        assertFalse(transformedQuery.contains("fragment UserProfile"));
        assertFalse(transformedQuery.contains("fragment ProfileDetails"));
        assertFalse(transformedQuery.contains("...UserProfile"));
        assertFalse(transformedQuery.contains("...ProfileDetails"));
    }
    
    @Test
    void testInlineFragmentsWithArguments() {
        String queryWithFragmentArgs = "query GetUser($userId: ID!, $includeProfile: Boolean!) {\n" +
            "  user(id: $userId) {\n" +
            "    id\n" +
            "    name\n" +
            "    ...UserProfile @include(if: $includeProfile)\n" +
            "  }\n" +
            "}\n" +
            "\n" +
            "fragment UserProfile on User {\n" +
            "  email\n" +
            "  profile {\n" +
            "    bio\n" +
            "    avatar\n" +
            "  }\n" +
            "}";
        
        GraphQLTransformer transformer = new GraphQLTransformer(queryWithFragmentArgs);
        TransformationResult result = transformer.inlineAllFragments().transform();
        
        assertTrue(result.isSuccess());
        String transformedQuery = result.getQueryString();
        
        // Check that fragments with arguments are inlined
        assertTrue(transformedQuery.contains("email"));
        assertTrue(transformedQuery.contains("profile"));
        assertTrue(transformedQuery.contains("bio"));
        assertTrue(transformedQuery.contains("avatar"));
        assertTrue(transformedQuery.contains("@include(if: $includeProfile)"));
        
        // Check that fragment definition is removed
        assertFalse(transformedQuery.contains("fragment UserProfile"));
        assertFalse(transformedQuery.contains("...UserProfile"));
    }
    
    @Test
    void testInlineFragmentsNoFragments() {
        String queryWithoutFragments = "query GetUser($userId: ID!) {\n" +
            "  user(id: $userId) {\n" +
            "    id\n" +
            "    name\n" +
            "    email\n" +
            "  }\n" +
            "}";
        
        GraphQLTransformer transformer = new GraphQLTransformer(queryWithoutFragments);
        TransformationResult result = transformer.inlineAllFragments().transform();
        
        assertTrue(result.isSuccess());
        String transformedQuery = result.getQueryString();
        
        // Check that query remains unchanged
        assertEquals(queryWithoutFragments.trim(), transformedQuery.trim());
    }
    
    @Test
    void testExtractFragmentBasic() {
        String queryWithSelectionSet = "query GetUser($userId: ID!) {\n" +
            "  user(id: $userId) {\n" +
            "    id\n" +
            "    name\n" +
            "    email\n" +
            "    profile {\n" +
            "      bio\n" +
            "      avatar\n" +
            "    }\n" +
            "  }\n" +
            "}";
        
        GraphQLTransformer transformer = new GraphQLTransformer(queryWithSelectionSet);
        TransformationResult result = transformer.extractFragment("//query/user", "UserProfile", "User").transform();
        
        assertTrue(result.isSuccess());
        String transformedQuery = result.getQueryString();
        

        
        // Check that fragment is created
        assertTrue(transformedQuery.contains("fragment UserProfile on User"));
        assertTrue(transformedQuery.contains("...UserProfile"));
        
        // Check that original fields are in the fragment
        assertTrue(transformedQuery.contains("id"));
        assertTrue(transformedQuery.contains("name"));
        assertTrue(transformedQuery.contains("email"));
        assertTrue(transformedQuery.contains("profile"));
        assertTrue(transformedQuery.contains("bio"));
        assertTrue(transformedQuery.contains("avatar"));
    }
    
    @Test
    void testExtractFragmentNested() {
        String queryWithNestedFields = "query GetUser($userId: ID!) {\n" +
            "  user(id: $userId) {\n" +
            "    id\n" +
            "    name\n" +
            "    profile {\n" +
            "      bio\n" +
            "      avatar\n" +
            "      location {\n" +
            "        city\n" +
            "        country\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";
        
        GraphQLTransformer transformer = new GraphQLTransformer(queryWithNestedFields);
        TransformationResult result = transformer.extractFragment("//query/user", "UserProfile", "User").transform();
        
        assertTrue(result.isSuccess());
        String transformedQuery = result.getQueryString();
        
        // Check that fragment is created with nested fields
        assertTrue(transformedQuery.contains("fragment UserProfile on User"));
        assertTrue(transformedQuery.contains("...UserProfile"));
        assertTrue(transformedQuery.contains("profile"));
        assertTrue(transformedQuery.contains("location"));
        assertTrue(transformedQuery.contains("city"));
        assertTrue(transformedQuery.contains("country"));
    }
    
    @Test
    void testExtractFragmentWithArguments() {
        String queryWithArgs = "query GetUser($userId: ID!, $includeProfile: Boolean!) {\n" +
            "  user(id: $userId) {\n" +
            "    id\n" +
            "    name\n" +
            "    email\n" +
            "    profile @include(if: $includeProfile) {\n" +
            "      bio\n" +
            "      avatar\n" +
            "    }\n" +
            "  }\n" +
            "}";
        
        GraphQLTransformer transformer = new GraphQLTransformer(queryWithArgs);
        TransformationResult result = transformer.extractFragment("//query/user", "UserProfile", "User").transform();
        
        assertTrue(result.isSuccess());
        String transformedQuery = result.getQueryString();
        
        // Check that fragment is created with arguments preserved
        assertTrue(transformedQuery.contains("fragment UserProfile on User"));
        assertTrue(transformedQuery.contains("...UserProfile"));
        assertTrue(transformedQuery.contains("@include(if: $includeProfile)"));
    }
    
    @Test
    void testExtractFragmentInvalidPath() {
        String query = "query GetUser($userId: ID!) {\n" +
            "  user(id: $userId) {\n" +
            "    id\n" +
            "    name\n" +
            "  }\n" +
            "}";
        
        GraphQLTransformer transformer = new GraphQLTransformer(query);
        TransformationResult result = transformer.extractFragment("//query/nonexistent", "UserProfile", "User").transform();
        
        // Should still be valid but unchanged
        assertTrue(result.isSuccess());
        String transformedQuery = result.getQueryString();
        assertEquals(query.trim(), transformedQuery.trim());
    }
    
    @Test
    void testFragmentOperationsCombined() {
        String queryWithFragments = "query GetUser($userId: ID!) {\n" +
            "  user(id: $userId) {\n" +
            "    id\n" +
            "    name\n" +
            "    ...UserProfile\n" +
            "  }\n" +
            "}\n" +
            "\n" +
            "fragment UserProfile on User {\n" +
            "  email\n" +
            "  profile {\n" +
            "    bio\n" +
            "    avatar\n" +
            "  }\n" +
            "}";
        
        GraphQLTransformer transformer = new GraphQLTransformer(queryWithFragments);
        TransformationResult result = transformer
            .inlineAllFragments()
            .extractFragment("//query/user", "UserBasic", "User")
            .transform();
        
        assertTrue(result.isSuccess());
        String transformedQuery = result.getQueryString();
        
        // Check that fragments are inlined first, then extracted
        assertTrue(transformedQuery.contains("fragment UserBasic on User"));
        assertTrue(transformedQuery.contains("...UserBasic"));
        assertTrue(transformedQuery.contains("id"));
        assertTrue(transformedQuery.contains("name"));
        assertTrue(transformedQuery.contains("email"));
        assertTrue(transformedQuery.contains("profile"));
    }
    
    @Test
    void testFragmentOperationsMutation() {
        String mutationWithFragments = "mutation UpdateUser($userId: ID!, $input: UserInput!) {\n" +
            "  updateUser(id: $userId, input: $input) {\n" +
            "    id\n" +
            "    name\n" +
            "    ...UserProfile\n" +
            "  }\n" +
            "}\n" +
            "\n" +
            "fragment UserProfile on User {\n" +
            "  email\n" +
            "  profile {\n" +
            "    bio\n" +
            "    avatar\n" +
            "  }\n" +
            "}";
        
        GraphQLTransformer transformer = new GraphQLTransformer(mutationWithFragments);
        TransformationResult result = transformer.inlineAllFragments().transform();
        
        assertTrue(result.isSuccess());
        String transformedQuery = result.getQueryString();
        
        // Check that fragments work in mutations
        assertTrue(transformedQuery.contains("mutation UpdateUser"));
        assertTrue(transformedQuery.contains("updateUser"));
        assertTrue(transformedQuery.contains("email"));
        assertTrue(transformedQuery.contains("profile"));
        assertFalse(transformedQuery.contains("fragment UserProfile"));
    }
    
    @Test
    void testFragmentOperationsSubscription() {
        String subscriptionWithFragments = "subscription UserUpdates($userId: ID!) {\n" +
            "  userUpdates(userId: $userId) {\n" +
            "    id\n" +
            "    name\n" +
            "    ...UserProfile\n" +
            "  }\n" +
            "}\n" +
            "\n" +
            "fragment UserProfile on User {\n" +
            "  email\n" +
            "  profile {\n" +
            "    bio\n" +
            "    avatar\n" +
            "  }\n" +
            "}";
        
        GraphQLTransformer transformer = new GraphQLTransformer(subscriptionWithFragments);
        TransformationResult result = transformer.inlineAllFragments().transform();
        
        assertTrue(result.isSuccess());
        String transformedQuery = result.getQueryString();
        
        // Check that fragments work in subscriptions
        assertTrue(transformedQuery.contains("subscription UserUpdates"));
        assertTrue(transformedQuery.contains("userUpdates"));
        assertTrue(transformedQuery.contains("email"));
        assertTrue(transformedQuery.contains("profile"));
        assertFalse(transformedQuery.contains("fragment UserProfile"));
    }
} 