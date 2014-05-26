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
public class DirectedGraphNode extends Node<DirectedGraphNode> {

    /**
     * The name must be unique within a holder graph as it defines a node's
     * identity.
     */
    private final String name;
    
    /**
     * An adjacency list for incoming arcs.
     */
    private final Set<DirectedGraphNode> in;
    
    /**
     * An adjacency list for outgoing arcs.
     */
    private final Set<DirectedGraphNode> out;
    
    /**
     * Constructs a new <code>UndirectedGraphNode</code> with name 
     * <code>name</code>.
     * 
     * @param name the name of a new node.
     */
    public DirectedGraphNode(final String name) {
        checkNotNull(name, "The name of a node may not be 'null'.");
        this.name = name;
        this.in =  new HashSet<>();
        this.out = new HashSet<>();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<DirectedGraphNode> parentIterable() {
        return new Iterable<DirectedGraphNode>() {
            @Override
            public Iterator<DirectedGraphNode> iterator() {
                return new ParentIterator();
            }
        };
    }

    /**
     * Returns an <code>Iterator</code> over this node's children.
     * 
     * @return an <code>Iterator</code> over this node's children. 
     */
    @Override
    public Iterator<DirectedGraphNode> iterator() {
        return new ChildIterator();
    }

    /**
     * Connects this node to <code>child</code>.
     * 
     * @param child the edge target node.
     */
    @Override
    public void connectTo(final DirectedGraphNode child) {
        checkNotNull(child, "'child' is 'null'.");
        checkBelongsToGraph(this);
        checkBelongsToGraph(child);
        checkSameGraphs(this.getOwnerGraph(), child.getOwnerGraph());
        
        if (isConnectedTo(child)) {
            return;
        }
        
        this.out.add(child);
        child.in.add(this);
        incEdgeCount();
    }

    /**
     * Disconnect <code>child</code> and <code>this</code>.
     * 
     * @param child the node to disconnect from.
     */
    @Override
    public void disconnect(DirectedGraphNode child) {
        checkNotNull(child, "'child' is 'null'.");
        checkBelongsToGraph(this);
        checkBelongsToGraph(child);
        checkSameGraphs(this.getOwnerGraph(), child.getOwnerGraph());
        
        if (this.isConnectedTo(child)) {
            this.out.remove(child);
            child.in.remove(this);
            decEdgeCount();
        }
    }

    @Override
    public boolean isConnectedTo(DirectedGraphNode child) {
        return out.contains(child);
    }
    
    /**
     * Returns the reference to the owner graph or <code>null</code> if there is
     * no such.
     * 
     * @return the owner graph.
     */
    @Override
    public Graph<DirectedGraphNode> getOwnerGraph() {
        return this.ownerGraph;
    }

    /**
     * Sets the owner graph for this node.
     * 
     * @param graph the graph to set as an owner.
     */
    @Override
    public void setOwnerGraph(Graph<DirectedGraphNode> graph) {
        if (this.getOwnerGraph() != null) {
            this.clear();
            this.getOwnerGraph().removeNode(this);
        }
        
        this.ownerGraph = graph;
    }
    
    @Override
    public void clear() {
        final Iterator<DirectedGraphNode> iterator = iterator();
        
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
            this.decEdgeCount();
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DirectedGraphNode)) {
            return false;
        }
        
        return ((DirectedGraphNode) o).getName().equals(this.getName());
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public String toString() {
        return "[Node " + getName() + "]";
    }
    
    /**
     * This inner class implements the iterator over enclosing's node's 
     * children.
     */
    private class ChildIterator implements Iterator<DirectedGraphNode> {

        /**
         * The actual iterator, no need to reinvent a wheel.
         */
        private Iterator<DirectedGraphNode> iterator = 
                DirectedGraphNode.this.out.iterator();
        
        /**
         * Caches the node returned by <code>next()</code> most recently.
         */
        private DirectedGraphNode lastReturned;
        
        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public DirectedGraphNode next() {
            return (lastReturned = iterator.next());
        }
        
        @Override
        public void remove() {
            if (lastReturned == null) {
                throw new NoSuchElementException("There is no current node.");
            }
            
            iterator.remove();
            lastReturned.in.remove(DirectedGraphNode.this);
            lastReturned = null;
        }
    }
    
    /**
     * This inner class implements the iterator over enclosing's node's 
     * children.
     */
    private class ParentIterator implements Iterator<DirectedGraphNode> {

        /**
         * The actual iterator, no need to reinvent a wheel.
         */
        private Iterator<DirectedGraphNode> iterator = 
                DirectedGraphNode.this.in.iterator();
        
        /**
         * Caches the node returned by <code>next()</code> most recently.
         */
        private DirectedGraphNode lastReturned;
        
        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public DirectedGraphNode next() {
            return (lastReturned = iterator.next());
        }
        
        @Override
        public void remove() {
            if (lastReturned == null) {
                throw new NoSuchElementException("There is no current node.");
            }
            
            iterator.remove();
            lastReturned.out.remove(DirectedGraphNode.this);
            lastReturned = null;
        }
    }
}
