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
    
    /**
     * The graph this node belongs to.
     */
    protected Graph<T> ownerGraph;
    
    /**
     * Returns an <code>Iterable</code> over this node's parent nodes.
     * 
     * @return an <code>Iterable</code> over this node's parent nodes. 
     */
    public abstract Iterable<T> parentIterable();
    
    /**
     * Returns the name of this node.
     * 
     * @return the name of this node. 
     */
    public abstract String getName();
    
    /**
     * Creates an undirected edge between <code>this</code> and
     * <code>child</code> or a directed arc from <code>this</code> to
     * <code>child</code> depending on the actual implementation of
     * <code>T</code>.
     * 
     * @param child the child node.
     */
    public abstract void connectTo(final T child);
    
    /**
     * Removes the edge starting from <code>this</code> going to 
     * <code>child</code>.
     * 
     * @param child the child node.
     */
    public abstract void disconnect(final T child);
    
    /**
     * Tests whether there is an edge from <code>this</code> to 
     * <code>child</code>.
     * 
     * @param child the child node.
     * 
     * @return <code>true</code> if there is an edge from <code>this</code> to
     * <code>child</code>, <code>false</code> otherwise.
     */
    public abstract boolean isConnectedTo(final T child);
    
    /**
     * Returns the reference to the owner graph, or <code>null</code> if there
     * is no any set.
     * 
     * @return the reference to the owner graph or <code>null</code> if there
     * is no such.
     */
    public abstract Graph<T> getOwnerGraph();
    
    /**
     * Removes all edges incident on this node.
     */
    public abstract void clear();
    
    /**
     * Sets the owner graph for this node.
     * 
     * @param ownerGraph the owner graph.
     */
    protected abstract void setOwnerGraph(final Graph<T> ownerGraph);
    
    /**
     * Increments the owner graph's edge counter.
     */
    protected void incEdgeCount() {
        ++ownerGraph.edgeCount;
    }
    
    /**
     * Decrements the owner graph's edge counter.
     */
    protected void decEdgeCount() {
        --ownerGraph.edgeCount;
    }
}
