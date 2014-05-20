package com.coderodde.apij.graph.model;

import com.coderodde.apij.graph.model.support.UndirectedGraphNode;
import static com.coderodde.apij.util.Utils.checkNotNull;
import java.util.HashMap;
import java.util.Map;

public class WeightFunction<T extends Node<T>, W> {
    
    private final Map<T, Map<T, W>> map = new HashMap<>();
    
    public void put(final T from, final T to, final W weight) {
        checkNotNull(from, "'from' is 'null'.");
        checkNotNull(to, "'to' is 'null'.");
        checkNotNull(weight, "'weight' is 'null'.");
        
        if (map.containsKey(from) == false) {
            map.put(from, new HashMap<T, W>());
        }
        
        map.get(from).put(to, weight);
    }
    
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
