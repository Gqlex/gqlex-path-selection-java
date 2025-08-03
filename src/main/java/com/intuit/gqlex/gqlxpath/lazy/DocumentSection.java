package com.intuit.gqlex.gqlxpath.lazy;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a section of a GraphQL document that can be loaded lazily
 */
public class DocumentSection {
    
    private final String documentId;
    private final long offset;
    private final int size;
    private final String content;
    private final SectionType type;
    private final List<GraphQLNode> nodes;
    
    public DocumentSection(String documentId, long offset, int size, String content, SectionType type) {
        this.documentId = documentId;
        this.offset = offset;
        this.size = size;
        this.content = content;
        this.type = type;
        this.nodes = new ArrayList<>();
    }
    
    /**
     * Get the document ID this section belongs to
     */
    public String getDocumentId() {
        return documentId;
    }
    
    /**
     * Get the offset of this section in the document
     */
    public long getOffset() {
        return offset;
    }
    
    /**
     * Get the size of this section in bytes
     */
    public int getSize() {
        return size;
    }
    
    /**
     * Get the content of this section
     */
    public String getContent() {
        return content;
    }
    
    /**
     * Get the type of this section
     */
    public SectionType getType() {
        return type;
    }
    
    /**
     * Get all nodes in this section
     */
    public List<GraphQLNode> getNodes() {
        return new ArrayList<>(nodes);
    }
    
    /**
     * Add a node to this section
     */
    public void addNode(GraphQLNode node) {
        nodes.add(node);
    }
    
    /**
     * Check if this section contains a specific node
     */
    public boolean containsNode(GraphQLNode node) {
        return nodes.contains(node);
    }
    
    /**
     * Get nodes of a specific type
     */
    public List<GraphQLNode> getNodesByType(NodeType type) {
        List<GraphQLNode> result = new ArrayList<>();
        for (GraphQLNode node : nodes) {
            if (node.getType() == type) {
                result.add(node);
            }
        }
        return result;
    }
    
    @Override
    public String toString() {
        return "DocumentSection{" +
                "documentId='" + documentId + '\'' +
                ", offset=" + offset +
                ", size=" + size +
                ", type=" + type +
                ", nodesCount=" + nodes.size() +
                '}';
    }
    
    /**
     * Enum representing different types of document sections
     */
    public enum SectionType {
        FIELD,
        FRAGMENT,
        OPERATION,
        ARGUMENT,
        DIRECTIVE,
        VARIABLE,
        ALIAS
    }
    
    /**
     * Enum representing different types of GraphQL nodes
     */
    public enum NodeType {
        FIELD,
        FRAGMENT_SPREAD,
        INLINE_FRAGMENT,
        ARGUMENT,
        DIRECTIVE,
        VARIABLE,
        ALIAS
    }
    
    /**
     * Simple GraphQL node representation for lazy loading
     */
    public static class GraphQLNode {
        private final NodeType type;
        private final String name;
        private final String value;
        private final int position;
        
        public GraphQLNode(NodeType type, String name, String value, int position) {
            this.type = type;
            this.name = name;
            this.value = value;
            this.position = position;
        }
        
        public NodeType getType() {
            return type;
        }
        
        public String getName() {
            return name;
        }
        
        public String getValue() {
            return value;
        }
        
        public int getPosition() {
            return position;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            
            GraphQLNode that = (GraphQLNode) o;
            
            if (position != that.position) return false;
            if (type != that.type) return false;
            if (!name.equals(that.name)) return false;
            return value.equals(that.value);
        }
        
        @Override
        public int hashCode() {
            int result = type.hashCode();
            result = 31 * result + name.hashCode();
            result = 31 * result + value.hashCode();
            result = 31 * result + position;
            return result;
        }
        
        @Override
        public String toString() {
            return "GraphQLNode{" +
                    "type=" + type +
                    ", name='" + name + '\'' +
                    ", value='" + value + '\'' +
                    ", position=" + position +
                    '}';
        }
    }
} 