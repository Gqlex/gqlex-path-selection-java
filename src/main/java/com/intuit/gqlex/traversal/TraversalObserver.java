package com.intuit.gqlex.traversal;

import graphql.language.Node;

public interface TraversalObserver {
    void updateNodeEntry(Node node, Node parentNode, Context context, ObserverAction observerAction);

    void updateNodeExit(Node node, Node parentNode, Context context, ObserverAction observerAction);
}
