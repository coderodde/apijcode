package com.coderodde.apij.graph.model;

import static com.coderodde.apij.util.Utils.checkBelongsToGraph;
import static com.coderodde.apij.util.Utils.checkNotNull;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * This class implements graphs.
 * 
 * @param <T> the actual node type.
 * 
 * @author Rodion Efremov
 * 
 * @version 1.6
 */
public class Graph<T extends Node<T>> implements Iterable<T> {
    
    /**
     * This is the identity of this graph. Must be unique.
     */
    private final String name;
    
    /**
     * The map from a node name to the node.
     */
    private final Map<String, T> map;
    
    /**
     * Caches the amount of edges in this graph.
     */
    protected int edgeCount;
    
    /**
     * Constructs a new empty graph with no nodes.
     * 
     * @param name the name (identity) of this graph.
     */
    public Graph(final String name) {
        checkNotNull(name, "The name of a graph may not be 'null'.");
        this.name = name;
        this.map = new HashMap<>();
    }
    
    /**
     * Adds a node to this graph.
     * 
     * @param node the node to add.
     */
    public void add(final T node) {
        final String nodeName = node.getName();
        
        // Already in this graph?
        if (map.containsKey(nodeName) == false) {
            map.put(nodeName, node);
            node.setOwnerGraph(this);
        }
    }
    
    public void addEdge(final T from, final T to) {
        checkNotNull(from, "The edge tail is 'null'.");
        checkNotNull(to, "The edge head is 'null'.");
        checkBelongsToGraph(from, this);
        checkBelongsToGraph(to, this);
        
        
        if (from.isConnectedTo(to) == false) {
            from.connectTo(to);
            ++edgeCount;
        }
    }
    
    public boolean containsNode(final T node) {
        return map.containsKey(node.getName());
    }
    
    /**
     * Returns the amount of nodes in this graph.
     * 
     * @return the amount of nodes in this graph.
     */
    public int size() {
        return map.size();
    }
    
    /**
     * Returns the amount of edges in this graph.
     * 
     * @return the amount of edges in this graph. 
     */
    public int edges() {
        return edgeCount;
    }
    
    /**
     * Returns the name of this graph.
     * 
     * @return the name of this graph.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns an iterator over this graph's nodes.
     * 
     * @return an iterator over this graph's nodes.
     */
    @Override
    public Iterator<T> iterator() {
        return new NodeIterator();
    }
    
    /**
     * Removes an edge from this graph.
     * 
     * @param from the tail of the edge.
     * @param to the head of the edge.
     */
    public void removeEdge(final T from, final T to) {
        checkNotNull(from, "The tail of an edge is 'null'.");
        checkNotNull(to, "The head of an edge is 'null'.");
        checkBelongsToGraph(from, this);
        checkBelongsToGraph(to, this);
        
        if (from.isConnectedTo(to)) {
            from.disconnect(to);
            --edgeCount;
        }
    }
    
    void incrementEdgeCount() {
        ++edgeCount;
    }
    
    void decrementEdgeCount() {
        --edgeCount;
    }
    
    private class NodeIterator implements Iterator<T> {
        
        private final Iterator<T> iterator = Graph.this.map.values().iterator();
        
        private T lastReturned;
        
        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public T next() {
            return (lastReturned = iterator.next());
        }
        
        public void remove() {
            if (lastReturned == null) {
                throw new NoSuchElementException("There is no current node.");
            }
            
            lastReturned.clear();
            lastReturned.setOwnerGraph(null);
            lastReturned = null;
        }
    }
}
