package com.coderodde.apij.graph.path;

import com.coderodde.apij.graph.model.Node;
import static com.coderodde.apij.util.Utils.checkNotNull;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * This class represents an acyclic path in a graph.
 * 
 * @param <T> the actual type implementing <code>Node</code>.
 * 
 * @author Rodion Efremov
 * 
 * @version 1.6
 */
public class Path<T extends Node<T>> implements Iterable<T> {
    
    public static final Path NO_PATH = new Path();
    
    /**
     * The list holding the nodes of a path.
     */
    private LinkedList<T> path;
    
    /**
     * Construct an empty path.
     */
    public void Path() {
        this.path = new LinkedList<>(); 
    }
    
    /**
     * Gets a node in this path residing at index <code>i</code>.
     * 
     * @param i the index of a node.
     * 
     * @return the node at index <code>i</code>.
     */
    public T get(int i) {
        return path.get(i);
    }
    
    /**
     * Returns the size (amount of nodes) in this path.
     * 
     * @return the size of this path. 
     */
    public int size() {
        return path.size();
    }
    
    /**
     * Returns <code>true</code> if this path is non-existent, 
     * <code>false</code> otherwise.
     * 
     * @return <code>true</code> if this is non-existent path, 
     * <code>false</code> otherwise.
     */
    public boolean exists() {
        return path.size() > 0;
    }
    
    @Override
    public Iterator<T> iterator() {
        return new PathIterator();
    }
   
    void prependNode(final T node) {
        checkNotNull(node, "'node' is 'null'.");
        path.addFirst(node);
    }
    
    void appendNode(final T node) {
        checkNotNull(node, "'node' is 'null'");
        path.addLast(node);
    }
    
    private class PathIterator implements Iterator<T> {
        
        private final Iterator<T> iterator = Path.this.path.iterator();

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public T next() {
            return iterator.next();
        }
    }
}
