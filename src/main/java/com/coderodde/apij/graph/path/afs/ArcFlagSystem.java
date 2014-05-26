package com.coderodde.apij.graph.path.afs;

import com.coderodde.apij.ds.pq.PriorityQueue;
import com.coderodde.apij.ds.pq.support.DaryHeap;
import com.coderodde.apij.graph.model.Graph;
import com.coderodde.apij.graph.model.WeightFunction;
import com.coderodde.apij.graph.model.support.DirectedGraphNode;
import com.coderodde.apij.graph.path.Path;
import static com.coderodde.apij.graph.path.PathFinder.constructPath;
import com.coderodde.apij.util.Utils.Pair;
import static com.coderodde.apij.util.Utils.checkNotBelow;
import static com.coderodde.apij.util.Utils.checkNotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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

    private Graph<DirectedGraphNode> graph;
    
    /**
     * The "open set".
     */
    private PriorityQueue<DirectedGraphNode, Double> OPEN;
    
    private Set<DirectedGraphNode> CLOSED;
    
    /**
     * The map mapping each discovered node to its shortest path length 
     * estimate.
     */
    private final Map<DirectedGraphNode, Double> GSCORE;

    private final Map<DirectedGraphNode, DirectedGraphNode> PARENT;
    
    private List<Set<DirectedGraphNode>> firstLevelRegions;
    
    private List<List<Set<DirectedGraphNode>>> secondLevelRegions;
    
    private WeightFunction<DirectedGraphNode> weightFunction;
    
    public ArcFlagSystem(final PriorityQueue<DirectedGraphNode, Double> queue,
                         final WeightFunction<DirectedGraphNode> weightFunction) 
    {
        checkNotNull(queue, "'queue' is null.");
        checkNotNull(weightFunction, "'weightFunction' is null.");
        
        this.weightFunction = weightFunction;
        
        this.firstLevelRegionMap = new RegionMap();
        this.secondLevelRegionMap = new HashMap<>();
        
        this.firstLevelArcFlags = new ArcFlags();
        this.secondLevelArcFlags = new HashMap<>();
        
        this.OPEN = queue;
        this.CLOSED = new HashSet<>();
        this.GSCORE = new HashMap<>();
        this.PARENT = new HashMap<>();
    }

    public ArcFlagSystem(final WeightFunction<DirectedGraphNode> weightFunction) 
    {
        this(new DaryHeap<DirectedGraphNode, Double>(), weightFunction);
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
            
            if (current.equals(target)) {
                return constructPath(target, PARENT);
            }
            
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
        
        return Path.NO_PATH;
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

        this.graph = graph;
        
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
        
        computeArcFlags();
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

    private void computeArcFlags() {
        final Pair<List<Collection<DirectedGraphNode>>,
                   List<Collection<DirectedGraphNode>>> data =
                getBoundaryNodes();
        
        for (final Collection<DirectedGraphNode> boundaryNodes : data.first) {
            for (final DirectedGraphNode boundaryNode : boundaryNodes) {
                setArcFlagsFromBoundarNode(boundaryNode);
            }
        }
        
        for (final Collection<DirectedGraphNode> boundaryNodes : data.second) {
            for (final DirectedGraphNode boundaryNode : boundaryNodes) {
                this.setArcFlagsFromBoundarNode(boundaryNode, 
                                                firstLevelRegionMap
                                                .get(boundaryNode));
            }
        }
    }
    
    private Pair<List<Collection<DirectedGraphNode>>,
                 List<Collection<DirectedGraphNode>>> 
            getBoundaryNodes() {
        final List<Collection<DirectedGraphNode>> firstLevelBoundarySetList;
        final List<Collection<DirectedGraphNode>> secondLevelBoundarySetList;
        
        firstLevelBoundarySetList = new ArrayList<>();
        secondLevelBoundarySetList = new ArrayList<>();
        
        for (final Set<DirectedGraphNode> set : firstLevelRegions) {
            firstLevelBoundarySetList.add(findFirstLevelBoundaryPoints(set));
        }
        
        for (List<Set<DirectedGraphNode>> miniRegions : secondLevelRegions) {
            for (final Set<DirectedGraphNode> miniRegion : miniRegions) {
                final Iterator<DirectedGraphNode> iterator = 
                        miniRegion.iterator();
                
                final DirectedGraphNode probe = iterator.next();
                
                secondLevelBoundarySetList.add(
                        findSecondLevelBoundaryPoints(
                                miniRegion,
                                firstLevelRegionMap.get(probe)));
            }
        }
        
        return new Pair<>(firstLevelBoundarySetList,
                          secondLevelBoundarySetList);
    }

    /**
     * Computes the boundary nodes within a set of input nodes.
     * 
     * @param set the set whose boundary nodes to compute.
     * 
     * @return a collection of boundary nodes of <code>set</code>.
     */
    private Collection<DirectedGraphNode> 
        findFirstLevelBoundaryPoints(Set<DirectedGraphNode> set) {
            List<DirectedGraphNode> result = new ArrayList<>();
            
            outer:
            for (final DirectedGraphNode node : set) {
                for (final DirectedGraphNode parent : node.parentIterable()) {
                    if (firstLevelRegionMap.get(node) 
                            != firstLevelRegionMap.get(parent)) {
                        result.add(node);
                        continue outer;
                    }
                }
            }
            
            return result;
    }

    /**
     * Computes the boundary nodes of a mini-region.
     * 
     * @param miniRegion the mini-region whose boundary nodes to compute.
     * @param containerRegionIndex the index of the first level region
     * containing <code>miniRegion</code>.
     * 
     * @return a collection of boundary nodes of <code>miniRegioin</code>.
     */
    private Collection<DirectedGraphNode> 
        findSecondLevelBoundaryPoints(final Set<DirectedGraphNode> miniRegion,
                                      final int containerRegionIndex) {
        final List<DirectedGraphNode> result = new ArrayList<>();
        final RegionMap rm = secondLevelRegionMap.get(containerRegionIndex);
        
        outer:
        for (final DirectedGraphNode node : miniRegion) {
            for (final DirectedGraphNode parent : node.parentIterable()) {
                if (rm.containsNode(parent) == false) {
                    result.add(node);
                    continue outer;
                }
            }
        }
        
        return result;
    }
        
    private void setArcFlagsFromBoundarNode(final DirectedGraphNode source) {
        final int REGION_NUMBER = firstLevelRegionMap.get(source);
        
        OPEN.clear();
        CLOSED.clear();
        GSCORE.clear();
        
        OPEN.add(source, 0.0);
        GSCORE.put(source, 0.0);
        
        while (OPEN.isEmpty() == false) {
            final DirectedGraphNode current = OPEN.extractMinimum();
            
            CLOSED.add(current);
            
            for (final DirectedGraphNode parent : current.parentIterable()) {
                if (CLOSED.contains(parent)) {
                    continue;
                }
                
                final int PARENT_REGION_NUMBER = 
                        firstLevelRegionMap.get(parent);
                
                if (PARENT_REGION_NUMBER == REGION_NUMBER) {
                    
                    continue;
                }
                
                ArcFlagVector vector = firstLevelArcFlags.get(parent, current);
                
                if (vector == null) {
                    firstLevelArcFlags.put(
                            parent, 
                            current,
                            (vector = new ArcFlagVector(firstLevelRegions
                                                        .size())));
                }
                
                vector.set(REGION_NUMBER);
                
                double tmpg = GSCORE.get(current) + 
                              weightFunction.get(parent, current);
                
                if (GSCORE.containsKey(parent) == false) {
                    OPEN.add(parent, tmpg);
                    GSCORE.put(parent, tmpg);
                } else if (tmpg < GSCORE.get(parent)) {
                    OPEN.decreasePriority(parent, tmpg);
                    GSCORE.put(parent, tmpg);
                }
            }
        }
    }
        
    private void setArcFlagsFromBoundarNode(final DirectedGraphNode source, 
                                            final int containerRegionIndex) {
        
        final int REGION_NUMBER = 
                secondLevelRegionMap.get(containerRegionIndex).get(source);
        
        final Set<DirectedGraphNode> REGION = 
                firstLevelRegions.get(containerRegionIndex);
        
        OPEN.clear();
        CLOSED.clear();
        GSCORE.clear();
        
        OPEN.add(source, 0.0);
        GSCORE.put(source, 0.0);
        
        while (OPEN.isEmpty() == false) {
            final DirectedGraphNode current = OPEN.extractMinimum();
            
            CLOSED.add(current);
            
            for (final DirectedGraphNode parent : current.parentIterable()) {
                if (CLOSED.contains(parent)) {
                    continue;
                }
                
                if (REGION.contains(parent) == false) {
                    continue;
                }
                
                final int PARENT_REGION_NUMBER = secondLevelRegionMap
                                                 .get(containerRegionIndex)
                                                 .get(parent);
                
                if (PARENT_REGION_NUMBER == REGION_NUMBER) {
                    ArcFlags arcFlags = secondLevelArcFlags
                                        .get(containerRegionIndex);
                    
                    if (arcFlags == null) {
                        secondLevelArcFlags.put(containerRegionIndex,
                                                (arcFlags = new ArcFlags()));
                    }
                    
                    ArcFlagVector vector = arcFlags.get(parent, current);
                    
                    if (vector == null) {
                        vector = new ArcFlagVector(secondLevelRegions
                                                   .get(containerRegionIndex)
                                                   .size());
                        arcFlags.put(parent, current, vector);
                    }
                    
                    vector.set(REGION_NUMBER);
                    continue;
                }
                
                final ArcFlags arcFlags = 
                        secondLevelArcFlags.get(containerRegionIndex);
                
                ArcFlagVector vector = arcFlags.get(parent, current);
                
                if (vector == null) {
                    final int miniRegionCount = 
                              secondLevelRegions
                              .get(containerRegionIndex)
                              .size();
                    
                    arcFlags.put(parent, 
                                 current,
                                 (vector = new ArcFlagVector(miniRegionCount)));
                }
                
                vector.set(REGION_NUMBER);
                
                double tmpg = GSCORE.get(current) + 
                              weightFunction.get(parent, current);
                
                if (GSCORE.containsKey(parent) == false) {
                    OPEN.add(parent, tmpg);
                    GSCORE.put(parent, tmpg);
                } else if (tmpg < GSCORE.get(parent)) {
                    OPEN.decreasePriority(parent, tmpg);
                    GSCORE.put(parent, tmpg);
                }
            }
        }
    }
}
