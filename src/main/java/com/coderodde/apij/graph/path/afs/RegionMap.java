package com.coderodde.apij.graph.path.afs;

import com.coderodde.apij.graph.model.support.DirectedGraphNode;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author rodionefremov
 */
class RegionMap {
    
    /**
     * The actual map mapping a node to its region's number.
     */
    private final Map<DirectedGraphNode, Integer> map;
    
    RegionMap() {
        this.map = new HashMap<>();
    }
    
    void put(final DirectedGraphNode node, final int regionNumber) {
        map.put(node, regionNumber);
    }
    
    int get(final DirectedGraphNode node) {
        return map.get(node);
    }
    
    int size() {
        return map.size();
    }
    
    void clear() {
        map.clear();
    }
}
