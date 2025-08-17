package com.intuit.gqlex.common;

import com.intuit.gqlex.gqlxpath.selector.SearchContext;
import graphql.language.Node;

import java.util.Stack;

public class GqlNodeContext/* implements Cloneable*/{

    private final Node parentNode;

    private GqlNode node;

    private int level;
    private Stack<GqlNode> nodeStack;

    private SearchContext searchContext;

    public SearchContext getSearchContext() {
        return searchContext;
    }

    /*@Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }*/

    public GqlNodeContext(Node node, Node parentNode, DocumentElementType documentElementType, Stack<GqlNode> nodeStack, int level) {
        this.nodeStack = nodeStack;
        this.level = level;
        this.node = new GqlNode(node,documentElementType);
        this.parentNode = parentNode;

        searchContext = new SearchContext();
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setNodeStack(Stack<GqlNode> nodeStack) {
        this.nodeStack = nodeStack;
    }

    public int getLevel() {
        return level;
    }

    public Stack<GqlNode> getNodeStack() {
        return nodeStack;
    }

    public void setNode(GqlNode node) {
        this.node = node;
    }

    public GqlNodeContext(Node node, Node parentNode, DocumentElementType type) {
        this(node,parentNode,type, null, 0);
    }

    public Node getNode() {
        return this.node.getNode();
    }

    public DocumentElementType getType() {
        return this.node.getType();
    }

    public Node getParentNode() {
        return parentNode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("GqlNodeContext{");
        
        // Node information
        if (node != null) {
            sb.append("type=").append(node.getType());
            if (node.getNode() != null) {
                String nodeClassName = node.getNode().getClass().getSimpleName();
                sb.append(", nodeClass=").append(nodeClassName);
                
                // Add specific node information based on type
                if (node.getNode() instanceof graphql.language.Field) {
                    graphql.language.Field field = (graphql.language.Field) node.getNode();
                    sb.append(", fieldName=").append(field.getName());
                } else if (node.getNode() instanceof graphql.language.Argument) {
                    graphql.language.Argument arg = (graphql.language.Argument) node.getNode();
                    sb.append(", argName=").append(arg.getName());
                } else if (node.getNode() instanceof graphql.language.Directive) {
                    graphql.language.Directive dir = (graphql.language.Directive) node.getNode();
                    sb.append(", directiveName=").append(dir.getName());
                }
            }
        }
        
        // Level and context information
        sb.append(", level=").append(level);
        
        // Parent node information
        if (parentNode != null) {
            String parentClassName = parentNode.getClass().getSimpleName();
            sb.append(", parentClass=").append(parentClassName);
        }
        
        // Node stack information
        if (nodeStack != null) {
            sb.append(", stackSize=").append(nodeStack.size());
        }
        
        // Search context information
        if (searchContext != null) {
            sb.append(", searchContext=").append(searchContext.toString());
        }
        
        sb.append("}");
        return sb.toString();
    }

    public String toShortString() {
        return "GqlNodeContext{" +
                "{ node=" + node + "}" +
                "{ level=" + level + "}" +
                "{ searchContext=" + searchContext + "}"+
                '}';
    }
}
