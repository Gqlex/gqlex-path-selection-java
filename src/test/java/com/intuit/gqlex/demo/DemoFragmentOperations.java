package com.intuit.gqlex.demo;

import com.intuit.gqlex.transformation.GraphQLTransformer;
import com.intuit.gqlex.transformation.TransformationResult;

public class DemoFragmentOperations {
    
    public static void main(String[] args) {
        System.out.println("=== FRAGMENT OPERATIONS DEMO ===\n");
        
        // Example 1: Fragment Inlining
        demoFragmentInlining();
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // Example 2: Fragment Extraction
        demoFragmentExtraction();
    }
    
    public static void demoFragmentInlining() {
        System.out.println("1. FRAGMENT INLINING - BEFORE:");
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
        
        System.out.println(queryWithFragments);
        
        System.out.println("\nRUNNING: transformer.inlineAllFragments().transform()");
        
        try {
            GraphQLTransformer transformer = new GraphQLTransformer(queryWithFragments);
            TransformationResult result = transformer.inlineAllFragments().transform();
            
            System.out.println("\nAFTER (fragments inlined):");
            if (result.isSuccess()) {
                System.out.println(result.getQueryString());
                
                System.out.println("\nWHAT HAPPENED:");
                System.out.println("✓ ...UserProfile → replaced with email + profile { bio avatar }");
                System.out.println("✓ ...UserPosts → replaced with posts { id title }");
                System.out.println("✓ All fragment definitions removed");
                System.out.println("✓ Query is now self-contained");
            } else {
                System.out.println("ERROR: " + result.getErrors());
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void demoFragmentExtraction() {
        System.out.println("2. FRAGMENT EXTRACTION - BEFORE:");
        String queryWithInlineFields = "query GetHero($episode: Episode) {\n" +
            "  hero(episode: $episode) {\n" +
            "    id\n" +
            "    name\n" +
            "    appearsIn\n" +
            "    friends {\n" +
            "      id\n" +
            "      name\n" +
            "      appearsIn\n" +
            "    }\n" +
            "  }\n" +
            "}";
        
        System.out.println(queryWithInlineFields);
        
        System.out.println("\nRUNNING: transformer.extractFragment(\"//query/hero\", \"HeroFields\", \"Character\").transform()");
        
        try {
            GraphQLTransformer transformer = new GraphQLTransformer(queryWithInlineFields);
            TransformationResult result = transformer
                .extractFragment("//query/hero", "HeroFields", "Character")
                .transform();
            
            System.out.println("\nAFTER (fragment extracted):");
            if (result.isSuccess()) {
                System.out.println(result.getQueryString());
                
                System.out.println("\nWHAT HAPPENED:");
                System.out.println("✓ Selection set under 'hero' extracted to fragment 'HeroFields'");
                System.out.println("✓ Fragment has type condition 'Character'");
                System.out.println("✓ Original query now references ...HeroFields");
                System.out.println("✓ Fields: id, name, appearsIn, friends { id, name, appearsIn }");
            } else {
                System.out.println("ERROR: " + result.getErrors());
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
