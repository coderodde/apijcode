package com.coderodde.apij.graph.path.support;

import com.coderodde.apij.ds.pq.PriorityQueue;
import com.coderodde.apij.ds.pq.support.DaryHeap;
import com.coderodde.apij.graph.model.Node;
import com.coderodde.apij.graph.model.WeightFunction;
import com.coderodde.apij.graph.path.HeuristicFunction;
import com.coderodde.apij.graph.path.Path;
import com.coderodde.apij.graph.path.PathFinder;
import com.coderodde.apij.graph.path.SearchData;
import static com.coderodde.apij.util.Utils.checkNotNull;
import static com.coderodde.apij.util.Utils.checkSameGraphs;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BidirectionalAStarFinder<T extends Node<T>> 
extends PathFinder<T> {
    
    /**
     * This is the "open set" for the forward search.
     */
    private PriorityQueue<T, Double> OPENA;
    
    /**
     * This is the "open set" for the backward search.
     */
    private PriorityQueue<T, Double> OPENB;
    
    /**
     * This is the "closed set" for forward search.
     */
    private Set<T> CLOSEDA = new HashSet<>();
    
    /**
     * This is the "closed set" for backward search.
     */
    private Set<T> CLOSEDB = new HashSet<>();
    
    /**
     * Holds g-scores for the nodes in forward search.
     */
    private Map<T, Double> GSCOREA = new HashMap<>();
    
    /**
     * Holds g-scores for the nodes in backward search.
     */
    private Map<T, Double> GSCOREB = new HashMap<>();
    
    /**
     * Maps every found node to its parent node in the shortest path tree in 
     * forward search.
     */
    private Map<T, T> PARENTA = new HashMap<>();
    
    /**
     * The parent map for the nodes in backward search.
     */
    private Map<T, T> PARENTB = new HashMap<>();
    
    private HeuristicFunction<T> HFA;
    
    private HeuristicFunction<T> HFB;
    
    public BidirectionalAStarFinder() {
        // This the default: d-ary heap with d = 2.
        this(new DaryHeap<T, Double>(2));
    }
    
    public BidirectionalAStarFinder(final PriorityQueue<T, Double> heap) {
        checkNotNull(heap, "'heap' is 'null'.");
        heap.clear();
        this.OPENA = heap;
        this.OPENB = heap.spawn();
    }
    
    @Override
    public Path<T> search(SearchData... data) {
        T source = null;
        T target = null;
        WeightFunction<T> wf = null;
        HFA = null;
        HFB = null;
        
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
                    HFA = (HeuristicFunction<T>) sd.getData();
                    break;
                    
                case HEURISTIC_FUNCTION_BACKWARD:
                    HFB = (HeuristicFunction<T>) sd.getData();
                    break;
            }
        }
        
        checkSameGraphs(source, target);
        checkNotNull(wf, "weight function is null.");
        checkNotNull(HFA, "Forward heuristic function is null.");
        checkNotNull(HFB, "Backward heuristic function is null.");
        
        HFA.setTarget(target);
        HFB.setTarget(source);
        
        OPENA.clear();
        CLOSEDA.clear();
        GSCOREA.clear();
        PARENTA.clear();
        
        OPENB.clear();
        CLOSEDB.clear();
        GSCOREB.clear();
        PARENTB.clear();
        
        OPENA.add(source, HFA.estimateFrom(source));
        GSCOREA.put(source, 0.0);
        PARENTA.put(source, null);
        
        OPENB.add(target, HFB.estimateFrom(target));
        GSCOREB.put(target, 0.0);
        PARENTB.put(target, null);
        
        T touch = null;
        double m = Double.POSITIVE_INFINITY;
        
        while (OPENA.size() * OPENB.size() > 0) {
            
            if (touch != null) {
                final T topA = OPENA.min();
                final T topB = OPENB.min();
                final double fa = OPENA.getPriorityOf(topA);
                final double fb = OPENB.getPriorityOf(topB);
                
                if (Math.max(fa, fb) >= m) {
                    return constructPathBidirectional(touch, PARENTA, PARENTB);
                }
            }
            
            if (OPENA.getPriorityOf(OPENA.min()) < 
                OPENB.getPriorityOf(OPENB.min())) {
                // Expand in forward search,
                final T current = OPENA.extractMinimum();
                CLOSEDA.add(current);
                
                for (final T child : current) {
                    if (CLOSEDA.contains(child)) {
                        continue;
                    }
                    
                    double tmpg = GSCOREA.get(current) + wf.get(current, child);
                    
                    if (GSCOREA.containsKey(child) == false) {
                        OPENA.add(child, tmpg + HFA.estimateFrom(child));
                        GSCOREA.put(child, tmpg);
                        PARENTA.put(child, current);
                        
                        if (CLOSEDB.contains(child)) {
                            if (m > tmpg + GSCOREB.get(child)) {
                                m = tmpg + GSCOREB.get(child);
                                touch = child;
                            }
                        }
                    } else if (tmpg < GSCOREA.get(child)){
                        OPENA.decreasePriority(child, 
                                               tmpg + HFA.estimateFrom(child));
                        GSCOREA.put(child, tmpg);
                        PARENTA.put(child, current);
                        
                        if (CLOSEDB.contains(child)) {
                            if (m > tmpg + GSCOREB.get(child)) {
                                m = tmpg + GSCOREB.get(child);
                                touch = child;
                            }
                        }
                        
                    }
                }
            } else {
                // Expand in backward search.
                final T current = OPENB.extractMinimum();
                CLOSEDB.add(current);
                
                for (final T parent : current.parentIterable()) {
                    if (CLOSEDB.contains(parent)) {
                        continue;
                    }
                    
                    double tmpg = GSCOREB.get(current) + 
                                  wf.get(parent, current);
                    
                    if (GSCOREB.containsKey(parent) == false) {
                        OPENB.add(parent, tmpg + HFB.estimateFrom(parent));
                        GSCOREB.put(parent, tmpg);
                        PARENTB.put(parent, current);
                        
                        if (CLOSEDA.contains(parent)) {
                            if (m > tmpg + GSCOREA.get(parent)) {
                                m = tmpg + GSCOREA.get(parent);
                                touch = parent;
                            }
                        }
                    } else if (tmpg < GSCOREB.get(parent)) {
                        OPENB.decreasePriority(parent, 
                                               tmpg + HFB.estimateFrom(parent));
                        GSCOREB.put(parent, tmpg);
                        PARENTB.put(parent, current);
                        
                        if (CLOSEDA.contains(parent)) {
                            if (m > tmpg + GSCOREA.get(parent)) {
                                m = tmpg + GSCOREA.get(parent);
                                touch = parent;
                            }
                        }
                        
                    }
                }
            }
        }
        
        if (touch != null) {
            System.out.println("YES!");
            return constructPathBidirectional(touch, PARENTA, PARENTB);
        }
        
        return Path.NO_PATH;
    }
}
