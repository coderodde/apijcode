package com.coderodde.apij.graph.path.support;

import com.coderodde.apij.ds.pq.PriorityQueue;
import com.coderodde.apij.ds.pq.support.DaryHeap;
import com.coderodde.apij.graph.model.Node;
import com.coderodde.apij.graph.model.WeightFunction;
import com.coderodde.apij.graph.path.HeuristicFunction;
import com.coderodde.apij.graph.path.Path;
import com.coderodde.apij.graph.path.PathFinder;
import com.coderodde.apij.graph.path.SearchData;
import static com.coderodde.apij.util.Utils.checkBelongsToGraph;
import static com.coderodde.apij.util.Utils.checkNotNull;
import static com.coderodde.apij.util.Utils.checkSameGraphs;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AStarFinder<T extends Node<T>>
extends PathFinder<T> {
    
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
    
    public AStarFinder() {
        // This the default: d-ary heap with d = 2.
        this(new DaryHeap<T, Double>(2));
    }
    
    public AStarFinder(final PriorityQueue<T, Double> heap) {
        checkNotNull(heap, "'heap' is 'null'.");
        heap.clear();
        this.OPEN = heap;
    }

    @Override
    public Path<T> search(SearchData... data) {
        T source = null;
        T target = null;
        WeightFunction<T> wf = null;
        HeuristicFunction<T> hf = null;
        
        for (final SearchData sd : data) {
            switch (sd.getType()) {
                case SOURCE: 
                    source = (T) sd.getData();
                    break;
                    
                case TARGET:
                    target = (T) sd.getData();
                    break;
                    
                case WEIGHT_FUNCTION:
                    wf = (WeightFunction<T>) sd.getData();
                    break;
                    
                case HEURISTIC_FUNCTION:
                    hf = (HeuristicFunction<T>) sd.getData();
                    break;
            }
        }
        
        checkSameGraphs(source, target);
        checkNotNull(wf, "weight function is null.");
        checkNotNull(hf, "heuristic function is null.");
        
        hf.setTarget(target);
        
        OPEN.clear();
        CLOSED.clear();
        GSCORE.clear();
        PARENT.clear();
        
        OPEN.add(source, hf.estimateFrom(source));
        GSCORE.put(source, 0.0);
        PARENT.put(source, null);
        
        while (OPEN.isEmpty() == false) {
            final T current = OPEN.extractMinimum();
        
            if (current.equals(target)) {
                return constructPath(target, PARENT);
            }
            
            CLOSED.add(current);
            
            for (final T child : current) {
                if (CLOSED.contains(child)) {
                    continue;
                }
                
                double tmpg = GSCORE.get(current) + wf.get(current, child);
                
                if (GSCORE.containsKey(child) == false) {
                    OPEN.add(child, tmpg + hf.estimateFrom(child));
                    GSCORE.put(child, tmpg);
                    PARENT.put(child, current);
                } else if (tmpg < GSCORE.get(child)) {
                    OPEN.decreasePriority(child, tmpg + hf.estimateFrom(child));
                    GSCORE.put(child, tmpg);
                    PARENT.put(child, current);
                }
            }
        }
        
        return Path.NO_PATH;
    }
}
