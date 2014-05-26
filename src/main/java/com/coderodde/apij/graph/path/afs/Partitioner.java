package com.coderodde.apij.graph.path.afs;

import com.coderodde.apij.graph.model.support.DirectedGraphNode;
import java.util.Collection;
import java.util.List;

public abstract class Partitioner {
 
    protected int maxNodesPerRegion;
    
    public abstract List<List<DirectedGraphNode>> 
        partition(Collection<DirectedGraphNode> nodes);
    
    public void setMaxNodesPerRegion(final int maxNodesPerRegion) {
        this.maxNodesPerRegion = maxNodesPerRegion;
    }
}
