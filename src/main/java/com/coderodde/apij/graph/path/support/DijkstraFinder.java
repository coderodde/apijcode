package com.coderodde.apij.graph.path.support;

import com.coderodde.apij.ds.pq.PriorityQueue;
import com.coderodde.apij.ds.pq.support.DaryHeap;
import com.coderodde.apij.graph.model.Node;
import com.coderodde.apij.graph.model.WeightFunction;
import com.coderodde.apij.graph.path.Path;
import com.coderodde.apij.graph.path.PathFinder;
import static com.coderodde.apij.util.Utils.checkBelongsToGraph;
import static com.coderodde.apij.util.Utils.checkNotNull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DijkstraFinder<T extends Node<T>>
extends PathFinder<T, DijkstraFinder<T>> {
    
    /**
     * This is the "open set". It contains the discovered, but not expanded 
     * nodes.
     */
    private PriorityQueue<T, Double> OPEN;
    
    /**
     * This is the "closed set". It contains the expanded nodes.
     */
    private Set<T> CLOSED = new HashSet<>();
    
    /**
     * Holds g-scores for the nodes.
     */
    private Map<T, Double> GSCORE = new HashMap<>();
    
    /**
     * Maps every found node to its parent node in the shortest path tree.
     */
    private Map<T, T> PARENT = new HashMap<>();
    
    private T source;
    
    public DijkstraFinder() {
        // This the default: d-ary heap with d = 2.
        this(new DaryHeap<T, Double>(2));
    }
    
    public DijkstraFinder(final PriorityQueue<T, Double> heap) {
        checkNotNull(heap, "'heap' is 'null'.");
        heap.clear();
        this.OPEN = heap;
    }
    
    public final DijkstraTargetSelector<T> from(final T source) {
        checkNotNull(source, "'source' is 'null'.");
        checkBelongsToGraph(source);
        this.source = source;
        return new DijkstraTargetSelector<>(this);
    }
    
    final T getSource() {
        return source;
    }
    
    Path<T> searchImpl(final T from, 
                       final T to, 
                       final WeightFunction<T> wf) {
        OPEN.clear();
        CLOSED.clear();
        GSCORE.clear();
        PARENT.clear();
        
        OPEN.add(from, 0.0);
        GSCORE.put(from, 0.0);
        PARENT.put(from, null);
        
        while (OPEN.isEmpty() == false) {
            final T current = OPEN.extractMinimum();
        
            if (current.equals(to)) {
                return constructPath(to, PARENT);
            }
            
            CLOSED.add(current);
            
            for (final T child : current) {
                if (CLOSED.contains(child)) {
                    continue;
                }
                
                double tmpg = GSCORE.get(current) + wf.get(current, child);
                
                if (GSCORE.containsKey(child) == false) {
                    OPEN.add(child, tmpg);
                    GSCORE.put(child, tmpg);
                    PARENT.put(child, current);
                } else if (tmpg < GSCORE.get(child)) {
                    OPEN.decreasePriority(child, tmpg);
                    GSCORE.put(child, tmpg);
                    PARENT.put(child, current);
                }
            }
        }
        
        return Path.NO_PATH;
    }

    @Override
    public DijkstraFinder<T> findPath() {
        return this;
    }
}
