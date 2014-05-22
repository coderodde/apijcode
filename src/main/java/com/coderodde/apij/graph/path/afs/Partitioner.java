package com.coderodde.apij.graph.path.afs;

import com.coderodde.apij.graph.model.Node;
import com.coderodde.apij.graph.model.support.DirectedGraphNode;
import java.util.List;
import java.util.Set;

public interface Partitioner {
    
    public List<Set<DirectedGraphNode>> partition(Set<DirectedGraphNode> nodes);
    
}
