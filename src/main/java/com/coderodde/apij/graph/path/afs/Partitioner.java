package com.coderodde.apij.graph.path.afs;

import com.coderodde.apij.graph.model.support.DirectedGraphNode;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public abstract class Partitioner {
 
    protected int maxNodesPerRegion;
    
    public abstract List<Set<DirectedGraphNode>> 
        partition(Collection<DirectedGraphNode> nodes);
    
    public void setMaxNodesPerRegion(final int maxNodesPerRegion) {
        this.maxNodesPerRegion = maxNodesPerRegion;
    }
}
