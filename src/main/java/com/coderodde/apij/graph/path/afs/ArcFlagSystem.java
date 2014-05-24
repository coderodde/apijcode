package com.coderodde.apij.graph.path.afs;

import com.coderodde.apij.ds.pq.PriorityQueue;
import com.coderodde.apij.ds.pq.support.DaryHeap;
import com.coderodde.apij.graph.model.Graph;
import com.coderodde.apij.graph.model.WeightFunction;
import com.coderodde.apij.graph.model.support.DirectedGraphNode;
import com.coderodde.apij.graph.path.Path;
import static com.coderodde.apij.util.Utils.checkNotBelow;
import static com.coderodde.apij.util.Utils.checkNotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ArcFlagSystem {

    private static final int MIN_PARTITION_SIZE = 10;

    /**
     * This is the first level region map.
     */
    private final RegionMap firstLevelRegionMap;

    /**
     * This map maps each first level region <tt>R</tt> to a second level region
     * map partitioning <tt>R</tt>.
     */
    private final Map<Integer, RegionMap> secondLevelRegionMap;
    
    private final ArcFlags firstLevelArcFlags;
    
    private final Map<Integer, ArcFlags> secondLevelArcFlags;

    /**
     * The "open set".
     */
    private PriorityQueue<DirectedGraphNode, Double> OPEN;
    
    /**
     * The map mapping each discovered node to its shortest path length 
     * estimate.
     */
    private final Map<DirectedGraphNode, Double> GSCORE;

    private final Map<DirectedGraphNode, DirectedGraphNode> PARENT;
    
    private List<Set<DirectedGraphNode>> firstLevelRegions;
    
    private List<List<Set<DirectedGraphNode>>> secondLevelRegions;
    
    public ArcFlagSystem(final PriorityQueue<DirectedGraphNode, Double> queue) {
        checkNotNull(queue, "'queue' is null.");
        this.firstLevelRegionMap = new RegionMap();
        this.secondLevelRegionMap = new HashMap<>();
        
        this.firstLevelArcFlags = new ArcFlags();
        this.secondLevelArcFlags = new HashMap<>();
        
        this.OPEN = queue;
        this.GSCORE = new HashMap<>();
        this.PARENT = new HashMap<>();
    }

    public ArcFlagSystem() {
        this(new DaryHeap<DirectedGraphNode, Double>());
    }

    /**
     * The actual search algorithm.
     * 
     * @param source the source node.
     * @param target the target node.
     * @param weightFunction the weight function.
     * 
     * @return a shortest path from <code>source</code> to
     * <code>target</code>.
     */
    public Path<DirectedGraphNode> search
        (final DirectedGraphNode source,
         final DirectedGraphNode target,
         final WeightFunction<DirectedGraphNode> weightFunction) {
        OPEN.clear();
        GSCORE.clear();
        PARENT.clear();

        final int TARGET_REGION = firstLevelRegionMap.get(target);
        final int SUB_TARGET_REGION = 
                secondLevelRegionMap.get(TARGET_REGION).get(target);
        
        GSCORE.put(source, 0.0);
        OPEN.add(source, 0.0);
        
        while (OPEN.isEmpty() == false) {
            final DirectedGraphNode current = OPEN.extractMinimum();
            
            for (final DirectedGraphNode child : current) {
                if (firstLevelArcFlags.get(current, child)
                                      .get(TARGET_REGION) == false) {
                    continue;
                }
                
                final Set<DirectedGraphNode> targetRegion = 
                        firstLevelRegions.get(TARGET_REGION);
                
                if (targetRegion.contains(current) 
                        && targetRegion.contains(child)) {
                    if (secondLevelArcFlags
                            .get(TARGET_REGION).get(current, child)
                            .get(SUB_TARGET_REGION) == false) {
                        continue;
                    }
                }
                
                double tmpg = GSCORE.get(current) +
                              weightFunction.get(current, child);
                
                if (GSCORE.containsKey(child) == false) {
                    OPEN.add(child, tmpg);
                    GSCORE.put(child, tmpg);
                    PARENT.put(child, current);
                } else if (GSCORE.get(child) > tmpg) {
                    OPEN.decreasePriority(child, tmpg);
                    GSCORE.put(child, tmpg);
                    PARENT.put(child, current);
                }
            }
        }
        
        return null;
    }

    public void preprocess(final Graph<DirectedGraphNode> graph,
            final Partitioner partitioner,
            final int firstLevelNodesPerRegion,
            final int secondLevelNodesPerRegion) {
        checkNotNull(graph, "'graph' is null.");
        checkNotNull(partitioner, "'partitioner' is null.");

        if (graph.isConnected() == false) {
            throw new IllegalStateException(
                    "The input graph is not connected.");
        }

        checkNotBelow(firstLevelNodesPerRegion,
                MIN_PARTITION_SIZE,
                "First level region size is too small.");

        checkNotBelow(secondLevelNodesPerRegion,
                MIN_PARTITION_SIZE,
                "Second level region size is too small.");

        partitioner.setMaxNodesPerRegion(firstLevelNodesPerRegion);

        final List<Set<DirectedGraphNode>> firstLevelPartition
                = partitioner.partition(graph.view());
        
        partitioner.setMaxNodesPerRegion(secondLevelNodesPerRegion);

        final List<List<Set<DirectedGraphNode>>> secondLevelPartition
                = new ArrayList<>();

        
        for (Set<DirectedGraphNode> firstLevelRegion : firstLevelPartition) {
            final List<Set<DirectedGraphNode>> subPartition
                    = partitioner.partition(firstLevelRegion);
            secondLevelPartition.add(subPartition);
        }

        loadRegionMaps(firstLevelPartition, secondLevelPartition);

        this.firstLevelRegions = firstLevelPartition;
        this.secondLevelRegions = secondLevelPartition;
    }

    private void loadRegionMaps(final List<Set<DirectedGraphNode>> firstLevelPartition,
            final List<List<Set<DirectedGraphNode>>> secondLevelPartition) {
        firstLevelRegionMap.clear();
        secondLevelRegionMap.clear();

        int index = 0;

        for (final Set<DirectedGraphNode> region : firstLevelPartition) {
            for (final DirectedGraphNode node : region) {
                firstLevelRegionMap.put(node, index);
            }

            ++index;
        }

        index = 0;

        for (final List<Set<DirectedGraphNode>> list : secondLevelPartition) {
            int index2 = 0;
            
            for (final Set<DirectedGraphNode> miniRegion : list) {
                if (secondLevelRegionMap.containsKey(index) == false) {
                    secondLevelRegionMap.put(index, new RegionMap());
                }

                for (final DirectedGraphNode node : miniRegion) {
                    secondLevelRegionMap.get(index).put(node, index2);
                }
                
                ++index2;
            }

            ++index;
        }
    }
}
