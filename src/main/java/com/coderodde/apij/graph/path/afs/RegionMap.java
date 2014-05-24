package com.coderodde.apij.graph.path.afs;

import com.coderodde.apij.graph.model.support.DirectedGraphNode;
import java.util.HashMap;
import java.util.Map;

class RegionMap {
    
    /**
     * The actual map from nodes to region numbers.
     */
    private Map<DirectedGraphNode, Integer> map;
    
    RegionMap() {
        this.map = new HashMap<>();
    }
    
    void put(final DirectedGraphNode node, final int regionNumber) {
        map.put(node, regionNumber);
    }
    
    int get(final DirectedGraphNode node) {
        return map.get(node);
    }
    
    void clear() {
        map.clear();
    }
}
