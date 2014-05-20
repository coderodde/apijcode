package com.coderodde.apij.graph.path.support;

import com.coderodde.apij.ds.pq.PriorityQueue;
import com.coderodde.apij.ds.pq.support.DaryHeap;
import com.coderodde.apij.graph.model.Node;
import com.coderodde.apij.graph.model.WeightFunction;
import com.coderodde.apij.graph.path.HeuristicFunction;
import com.coderodde.apij.graph.path.Path;
import com.coderodde.apij.graph.path.PathFinder;
import static com.coderodde.apij.util.Utils.checkNotNull;
import java.util.HashSet;
import java.util.Set;

public class AStarFinder<T extends Node<T>, W extends Comparable<? super W>> 
extends PathFinder<T, W> {
    
    /**
     * This is the "open set". It contains the discovered, but not expanded 
     * nodes.
     */
    private PriorityQueue<T, W> OPEN;
    
    /**
     * This is the "closed set". It contains the expanded nodes.
     */
    private Set<T> CLOSED = new HashSet<>();
    
    private T source;
    
    public AStarFinder() {
        // This the default: d-ary heap with d = 2.
        this(new DaryHeap<T, W>(2));
    }
    
    public AStarFinder(final PriorityQueue<T, W> heap) {
        checkNotNull(heap, "'heap' is 'null'.");
        heap.clear();
        this.OPEN = heap;
    }
    
    final AStarTargetSelector<T, W> from(final T source) {
        checkNotNull(source, "'source' is 'null'.");
        this.source = source;
        return new AStarTargetSelector<>(this);
    }
    
    final T getSource() {
        return source;
    }
    
    Path<T> searchImpl(final T from, 
                       final T to, 
                       final WeightFunction<T, W> wf,
                       final HeuristicFunction<T, W> hf) {
        System.out.println("This is A*!");
        return null;
    }
}
