package com.coderodde.apij.graph.path.afs;

import com.coderodde.apij.graph.model.support.DirectedGraphNode;
import java.util.HashMap;
import java.util.Map;

class ArcFlags {
    
    private Map<DirectedGraphNode, Map<DirectedGraphNode, ArcFlagVector>> map;

    ArcFlags() {
        this.map = new HashMap<>();
    }
    
    void put(final DirectedGraphNode tail, 
             final DirectedGraphNode head, 
             final ArcFlagVector v) {
        if (map.containsKey(tail) == false) {
            map.put(tail, new HashMap<DirectedGraphNode, ArcFlagVector>());
        }
        
        map.get(tail).put(head, v);
    }
    
    ArcFlagVector get(final DirectedGraphNode tail,
                      final DirectedGraphNode head) {
        return map.get(tail).get(head);
    }
    
    void clear() {
        map.clear();
    }
}
