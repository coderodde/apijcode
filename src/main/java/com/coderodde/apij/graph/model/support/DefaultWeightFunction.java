package com.coderodde.apij.graph.model.support;

import com.coderodde.apij.graph.model.Node;
import com.coderodde.apij.graph.model.WeightFunction;
import static com.coderodde.apij.util.Utils.checkNotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * This class implements the weight function.
 * 
 * @author Rodion Efremov
 * @version 1.6
 * 
 * @param <T> the node type.
 * @param <W> the weight type.
 */
public class DefaultWeightFunction<T extends Node<T>, W> 
implements WeightFunction<T, W> {
    
    private final Map<T, Map<T, W>> map = new HashMap<>();
    
    /**
     * Assigns a weight to the edge <tt>(from, to)</tt>.
     * 
     * @param from the tail node of the edge.
     * @param to the head node of the edge.
     * @param weight the weight of the edge.
     */
    public void put(final T from, final T to, final W weight) {
        checkNotNull(from, "'from' is 'null'.");
        checkNotNull(to, "'to' is 'null'.");
        checkNotNull(weight, "'weight' is 'null'.");
        
        if (map.containsKey(from) == false) {
            map.put(from, new HashMap<T, W>());
        }
        
        map.get(from).put(to, weight);
    }
    
    /**
     * Gets the weight of the edge <tt>(from, to)</tt>.
     * 
     * @param from the tail node of the edge.
     * @param to the head node of the edge.
     * 
     * @return the weight of the specified edge. 
     */
    public W get(final T from, final T to) {
        checkNotNull(from, "'from' is 'null'.");
        checkNotNull(to, "'to' is 'null'.");
        
        if (map.containsKey(from)) {
            W result = map.get(from).get(to);
            
            if (result != null) {
                return result;
            }
        }
        
        if (from instanceof UndirectedGraphNode) {
            if (map.containsKey(to)) {
                return map.get(to).get(from);
            }
        }
        
        throw new IllegalStateException(
                "No weight for edge (" + from.getName() + ", " +
                        to.getName() + ").");
    }
}
