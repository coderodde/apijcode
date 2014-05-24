package com.coderodde.apij.graph.path.afs;

import com.coderodde.apij.graph.model.Graph;
import com.coderodde.apij.graph.model.support.DirectedGraphNode;
import com.coderodde.apij.graph.path.Path;
import static com.coderodde.apij.util.Utils.checkNotBelow;
import static com.coderodde.apij.util.Utils.checkNotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ArcFlagSystem {
    
    private static final int MIN_PARTITION_SIZE = 10;
    
    public Path<DirectedGraphNode> search(final DirectedGraphNode source,
                                          final DirectedGraphNode target) {
        return null;
    }
    
    public void preprocess(final Graph<DirectedGraphNode> graph,
                           final Partitioner partitioner,
                           final int firstLevelNodesPerRegion,
                           final int secondLevelNodesPerRegion) {
        checkNotNull(graph, "'graph' is null.");
        checkNotNull(partitioner, "'partitioner' is null.");
        
        checkNotBelow(firstLevelNodesPerRegion,
                      MIN_PARTITION_SIZE,
                      "First level region size is too small.");
        
        checkNotBelow(secondLevelNodesPerRegion, 
                      MIN_PARTITION_SIZE,
                      "Second level region size is too small.");
        
        partitioner.setMaxNodesPerRegion(firstLevelNodesPerRegion);
        
        final List<Set<DirectedGraphNode>> firstLevelPartition = 
                partitioner.partition(graph.view());
        
        partitioner.setMaxNodesPerRegion(secondLevelNodesPerRegion);
        
        final List<List<Set<DirectedGraphNode>>> secondLevelPartition =
                new ArrayList<>();
        
        for (Set<DirectedGraphNode> firstLevelRegion : firstLevelPartition) {
            final List<Set<DirectedGraphNode>> subPartition =
                    partitioner.partition(firstLevelRegion);
            secondLevelPartition.add(subPartition);
        }
    }
}
