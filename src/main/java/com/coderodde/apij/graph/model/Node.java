package com.coderodde.apij.graph.model;

/**
 * This interface defines the API for graph nodes.
 * 
 * @author rodionefremov
 * 
 * @version 1.6
 * 
 * @param <T> the actual implementing node type.
 */
public abstract class Node<T extends Node<T>> implements Iterable<T> {
    
    protected Graph<T> ownerGraph;
    
    /**
     * Returns an <code>Iterable</code> over this node's parent nodes.
     * @return 
     */
    public abstract Iterable<T> parentIterable();
    
    public abstract String getName();
    
    public abstract void connectTo(final T child);
    
    public abstract void disconnect(final T child);
    
    public abstract boolean isConnectedTo(final T child);
    
    public abstract Graph<T> getOwnerGraph();
    
    public void setOwnerGraph(final Graph<T> ownerGraph) {
        this.ownerGraph = ownerGraph;
    }
    
    public abstract void clear();
    
    protected void incEdgeCount() {
        ++ownerGraph.edgeCount;
    }
    
    protected void decEdgeCount() {
        --ownerGraph.edgeCount;
    }
}
