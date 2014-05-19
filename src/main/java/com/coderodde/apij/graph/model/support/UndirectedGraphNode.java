package com.coderodde.apij.graph.model.support;

import com.coderodde.apij.graph.model.Graph;
import com.coderodde.apij.graph.model.Node;
import static com.coderodde.apij.util.Utils.checkBelongsToGraph;
import static com.coderodde.apij.util.Utils.checkNotNull;
import static com.coderodde.apij.util.Utils.checkSameGraphs;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 *
 * @author rodionefremov
 */
public class UndirectedGraphNode extends Node<UndirectedGraphNode> {

    /**
     * The name must be unique within a holder graph as it defines a node's
     * identity.
     */
    private final String name;
    
    /**
     * A so called "adjacency list".
     */
    private final Set<UndirectedGraphNode> neighbourSet;
    
    /**
     * Constructs a new <code>UndirectedGraphNode</code> with name 
     * <code>name</code>.
     * 
     * @param name the name of a new node.
     */
    public UndirectedGraphNode(final String name) {
        checkNotNull(name, "The name of a node may not be 'null'.");
        this.name = name;
        this.neighbourSet = new HashSet<>();
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * Returns an <code>Iterable</code> over this node's parents.
     * 
     * @return an <code>Iterable</code> over this node's parents.
     */
    @Override
    public Iterable<UndirectedGraphNode> parentIterable() {
        return new NeighbourIterable();
    }

    /**
     * Returns an <code>Iterator</code> over this node's children.
     * 
     * @return an <code>Iterator</code> over this node's children. 
     */
    @Override
    public Iterator<UndirectedGraphNode> iterator() {
        return new NeighbourIterator();
    }

    /**
     * Connects this node to <code>child</code>.
     * 
     * @param child the edge target node.
     */
    @Override
    public void connectTo(final UndirectedGraphNode child) {
        checkNotNull(child, "'child' is 'null'.");
        checkBelongsToGraph(this);
        checkBelongsToGraph(child);
        checkSameGraphs(this.getOwnerGraph(), child.getOwnerGraph());
        
        if (isConnectedTo(child)) {
            return;
        }
        
        this.neighbourSet.add(child);
        child.neighbourSet.add(this);
        incEdgeCount();
    }

    /**
     * Disconnect <code>child</code> and <code>this</code>.
     * 
     * @param child the node to disconnect from.
     */
    @Override
    public void disconnect(UndirectedGraphNode child) {
        checkNotNull(child, "'child' is 'null'.");
        checkSameGraphs(this.getOwnerGraph(), child.getOwnerGraph());
        this.neighbourSet.remove(child);
        child.neighbourSet.remove(this);
    }

    public boolean isConnectedTo(UndirectedGraphNode child) {
        return neighbourSet.contains(child);
    }
    
    /**
     * Returns the reference to the owner graph or <code>null</code> if there is
     * no such.
     * 
     * @return the owner graph.
     */
    @Override
    public Graph<UndirectedGraphNode> getOwnerGraph() {
        return this.ownerGraph;
    }

    /**
     * Sets the owner graph for this node.
     * 
     * @param graph the graph to set as an owner.
     */
    @Override
    public void setOwnerGraph(Graph<UndirectedGraphNode> graph) {
        if (this.getOwnerGraph() != null) {
            this.clear();
        }
        
        this.ownerGraph = graph;
    }
    
    @Override
    public void clear() {
        for (UndirectedGraphNode neighbor : neighbourSet) {
            disconnect(neighbor);
        }
    }
    
    private class NeighbourIterable implements Iterable<UndirectedGraphNode> {

        @Override
        public Iterator<UndirectedGraphNode> iterator() {
            return new NeighbourIterator();
        }        
    }
    
    private class NeighbourIterator implements Iterator<UndirectedGraphNode> {

        /**
         * The actual iterator, no need to reinvent a wheel.
         */
        private Iterator<UndirectedGraphNode> iterator = 
                UndirectedGraphNode.this.neighbourSet.iterator();
        
        /**
         * Caches the node returned by <code>next()</code> most recently.
         */
        private UndirectedGraphNode lastReturned;
        
        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public UndirectedGraphNode next() {
            return (lastReturned = iterator.next());
        }
        
        @Override
        public void remove() {
            if (lastReturned == null) {
                throw new NoSuchElementException("There is no current node.");
            }
            
            UndirectedGraphNode.this.neighbourSet.remove(lastReturned);
            lastReturned.neighbourSet.remove(UndirectedGraphNode.this);
            lastReturned = null;
        }
    }
}
