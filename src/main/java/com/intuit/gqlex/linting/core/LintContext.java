package com.intuit.gqlex.linting.core;

import graphql.language.*;
import graphql.parser.Parser;
import com.intuit.gqlex.linting.config.LintConfig;

import java.util.*;
import java.util.function.Predicate;

/**
 * Provides context and utility methods for linting rules to interact with the GraphQL document.
 * 
 * <p>This class provides methods for traversing the AST, finding specific nodes, and accessing
 * configuration settings. It's completely generic and agnostic to any specific GraphQL schema.</p>
 * 
 * @author gqlex
 * @version 2.0.1
 * @since 2.0.1
 */
public class LintContext {
    private final Document document;
    private final LintConfig config;
    private final Map<String, Object> metadata = new HashMap<>();
    private final Parser parser;

    /**
     * Creates a new linting context with a document and configuration.
     * 
     * @param document the GraphQL document to lint
     * @param config the linting configuration
     */
    public LintContext(Document document, LintConfig config) {
        this.document = document;
        this.config = config != null ? config : new LintConfig();
        this.parser = new Parser();
    }

    /**
     * Creates a new linting context with a document and default configuration.
     * 
     * @param document the GraphQL document to lint
     */
    public LintContext(Document document) {
        this(document, new LintConfig());
    }

    /**
     * Creates a new linting context from a query string.
     * 
     * @param queryString the GraphQL query string
     * @param config the linting configuration
     */
    public LintContext(String queryString, LintConfig config) {
        this.parser = new Parser();
        this.document = parser.parseDocument(queryString);
        this.config = config != null ? config : new LintConfig();
    }

    /**
     * Creates a new linting context from a query string with default configuration.
     * 
     * @param queryString the GraphQL query string
     */
    public LintContext(String queryString) {
        this(queryString, new LintConfig());
    }

    /**
     * Gets the GraphQL document being linted.
     * 
     * @return the document
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Gets the linting configuration.
     * 
     * @return the configuration
     */
    public LintConfig getConfig() {
        return config;
    }

    /**
     * Gets a configuration value by key.
     * 
     * @param key the configuration key
     * @param type the expected type
     * @param defaultValue the default value if not found
     * @param <T> the type parameter
     * @return the configuration value or default
     */
    public <T> T getConfigValue(String key, Class<T> type, T defaultValue) {
        return config.getValue(key, type, defaultValue);
    }

    /**
     * Gets a configuration value by key.
     * 
     * @param key the configuration key
     * @param type the expected type
     * @param <T> the type parameter
     * @return the configuration value or null
     */
    public <T> T getConfigValue(String key, Class<T> type) {
        return config.getValue(key, type);
    }

    /**
     * Sets metadata for this context.
     * 
     * @param key the metadata key
     * @param value the metadata value
     */
    public void setMetadata(String key, Object value) {
        metadata.put(key, value);
    }

    /**
     * Gets metadata from this context.
     * 
     * @param key the metadata key
     * @return the metadata value or null
     */
    public Object getMetadata(String key) {
        return metadata.get(key);
    }

    /**
     * Traverses all nodes in the document and calls the visitor for each node.
     * 
     * @param visitor the node visitor
     */
    public void traverseNodes(NodeVisitor visitor) {
        if (document == null || visitor == null) {
            return;
        }
        traverseNode(document, visitor);
    }

    /**
     * Finds all fields that match the given predicate.
     * 
     * @param predicate the field predicate
     * @return a list of matching fields
     */
    public List<Field> findFields(Predicate<Field> predicate) {
        List<Field> fields = new ArrayList<>();
        traverseNodes(node -> {
            if (node instanceof Field && predicate.test((Field) node)) {
                fields.add((Field) node);
            }
        });
        return fields;
    }

    /**
     * Finds all arguments that match the given predicate.
     * 
     * @param predicate the argument predicate
     * @return a list of matching arguments
     */
    public List<Argument> findArguments(Predicate<Argument> predicate) {
        List<Argument> arguments = new ArrayList<>();
        traverseNodes(node -> {
            if (node instanceof Argument && predicate.test((Argument) node)) {
                arguments.add((Argument) node);
            }
        });
        return arguments;
    }

    /**
     * Finds all directives that match the given predicate.
     * 
     * @param predicate the directive predicate
     * @return a list of matching directives
     */
    public List<Directive> findDirectives(Predicate<Directive> predicate) {
        List<Directive> directives = new ArrayList<>();
        traverseNodes(node -> {
            if (node instanceof Directive && predicate.test((Directive) node)) {
                directives.add((Directive) node);
            }
        });
        return directives;
    }

    /**
     * Finds all fragment definitions.
     * 
     * @return a list of fragment definitions
     */
    public List<FragmentDefinition> findFragmentDefinitions() {
        return document.getDefinitions().stream()
            .filter(def -> def instanceof FragmentDefinition)
            .map(def -> (FragmentDefinition) def)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * Finds all inline fragments.
     * 
     * @return a list of inline fragments
     */
    public List<InlineFragment> findInlineFragments() {
        List<InlineFragment> fragments = new ArrayList<>();
        traverseNodes(node -> {
            if (node instanceof InlineFragment) {
                fragments.add((InlineFragment) node);
            }
        });
        return fragments;
    }

    /**
     * Finds all variable definitions.
     * 
     * @return a list of variable definitions
     */
    public List<VariableDefinition> findVariableDefinitions() {
        List<VariableDefinition> variables = new ArrayList<>();
        document.getDefinitions().stream()
            .filter(def -> def instanceof OperationDefinition)
            .map(def -> (OperationDefinition) def)
            .forEach(op -> variables.addAll(op.getVariableDefinitions()));
        return variables;
    }

    /**
     * Calculates the maximum depth of the document.
     * 
     * @return the maximum depth
     */
    public int calculateMaxDepth() {
        AtomicInteger maxDepth = new AtomicInteger(0);
        AtomicInteger currentDepth = new AtomicInteger(0);
        
        traverseNodes(node -> {
            if (node instanceof SelectionSet) {
                currentDepth.incrementAndGet();
                maxDepth.updateAndGet(current -> Math.max(current, currentDepth.get()));
            } else if (node instanceof Field && ((Field) node).getSelectionSet() == null) {
                // Leaf field - depth calculation complete for this branch
                currentDepth.decrementAndGet();
            }
        });
        
        return maxDepth.get();
    }

    /**
     * Calculates the total number of fields in the document.
     * 
     * @return the field count
     */
    public int calculateFieldCount() {
        AtomicInteger count = new AtomicInteger(0);
        traverseNodes(node -> {
            if (node instanceof Field) {
                count.incrementAndGet();
            }
        });
        return count.get();
    }

    /**
     * Calculates the total number of arguments in the document.
     * 
     * @return the argument count
     */
    public int calculateArgumentCount() {
        AtomicInteger count = new AtomicInteger(0);
        traverseNodes(node -> {
            if (node instanceof Argument) {
                count.incrementAndGet();
            }
        });
        return count.get();
    }

    /**
     * Checks if the document contains introspection queries.
     * 
     * @return true if introspection queries are found
     */
    public boolean containsIntrospectionQueries() {
        return findFields(field -> field.getName().startsWith("__")).size() > 0;
    }

    /**
     * Gets all operation definitions in the document.
     * 
     * @return a list of operation definitions
     */
    public List<OperationDefinition> getOperations() {
        return document.getDefinitions().stream()
            .filter(def -> def instanceof OperationDefinition)
            .map(def -> (OperationDefinition) def)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * Gets all query operations in the document.
     * 
     * @return a list of query operations
     */
    public List<OperationDefinition> getQueries() {
        return getOperations().stream()
            .filter(op -> op.getOperation() == OperationDefinition.Operation.QUERY)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * Gets all mutation operations in the document.
     * 
     * @return a list of mutation operations
     */
    public List<OperationDefinition> getMutations() {
        return getOperations().stream()
            .filter(op -> op.getOperation() == OperationDefinition.Operation.MUTATION)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * Gets all subscription operations in the document.
     * 
     * @return a list of subscription operations
     */
    public List<OperationDefinition> getSubscriptions() {
        return getOperations().stream()
            .filter(op -> op.getOperation() == OperationDefinition.Operation.SUBSCRIPTION)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * Recursively traverses a node and its children.
     * 
     * @param node the node to traverse
     * @param visitor the node visitor
     */
    private void traverseNode(Node node, NodeVisitor visitor) {
        if (node == null) {
            return;
        }
        
        // Visit the current node
        visitor.visit(node);
        
        // Traverse children based on node type
        if (node instanceof Document) {
            Document doc = (Document) node;
            doc.getDefinitions().forEach(def -> traverseNode(def, visitor));
        } else if (node instanceof OperationDefinition) {
            OperationDefinition op = (OperationDefinition) node;
            if (op.getSelectionSet() != null) {
                traverseNode(op.getSelectionSet(), visitor);
            }
            op.getVariableDefinitions().forEach(var -> traverseNode(var, visitor));
            op.getDirectives().forEach(dir -> traverseNode(dir, visitor));
        } else if (node instanceof SelectionSet) {
            SelectionSet selectionSet = (SelectionSet) node;
            selectionSet.getSelections().forEach(sel -> traverseNode(sel, visitor));
        } else if (node instanceof Field) {
            Field field = (Field) node;
            if (field.getSelectionSet() != null) {
                traverseNode(field.getSelectionSet(), visitor);
            }
            field.getArguments().forEach(arg -> traverseNode(arg, visitor));
            field.getDirectives().forEach(dir -> traverseNode(dir, visitor));
        } else if (node instanceof FragmentDefinition) {
            FragmentDefinition fragment = (FragmentDefinition) node;
            if (fragment.getSelectionSet() != null) {
                traverseNode(fragment.getSelectionSet(), visitor);
            }
            fragment.getDirectives().forEach(dir -> traverseNode(dir, visitor));
        } else if (node instanceof InlineFragment) {
            InlineFragment fragment = (InlineFragment) node;
            if (fragment.getSelectionSet() != null) {
                traverseNode(fragment.getSelectionSet(), visitor);
            }
            fragment.getDirectives().forEach(dir -> traverseNode(dir, visitor));
        } else if (node instanceof FragmentSpread) {
            FragmentSpread spread = (FragmentSpread) node;
            spread.getDirectives().forEach(dir -> traverseNode(dir, visitor));
        } else if (node instanceof Directive) {
            Directive directive = (Directive) node;
            directive.getArguments().forEach(arg -> traverseNode(arg, visitor));
        } else if (node instanceof Argument) {
            Argument argument = (Argument) node;
            if (argument.getValue() != null) {
                traverseNode(argument.getValue(), visitor);
            }
        } else if (node instanceof VariableDefinition) {
            VariableDefinition variable = (VariableDefinition) node;
            if (variable.getDefaultValue() != null) {
                traverseNode(variable.getDefaultValue(), visitor);
            }
            if (variable.getType() != null) {
                traverseNode(variable.getType(), visitor);
            }
            variable.getDirectives().forEach(dir -> traverseNode(dir, visitor));
        }
    }

    /**
     * Functional interface for visiting nodes during traversal.
     */
    @FunctionalInterface
    public interface NodeVisitor {
        /**
         * Visits a node during traversal.
         * 
         * @param node the node to visit
         */
        void visit(Node node);
    }

    /**
     * Simple atomic integer for thread-safe counting.
     */
    private static class AtomicInteger {
        private int value;

        public AtomicInteger(int initialValue) {
            this.value = initialValue;
        }

        public int get() {
            return value;
        }

        public void incrementAndGet() {
            value++;
        }

        public int decrementAndGet() {
            value--;
            return value;
        }

        public int updateAndGet(java.util.function.IntUnaryOperator updateFunction) {
            value = updateFunction.applyAsInt(value);
            return value;
        }
    }
} 