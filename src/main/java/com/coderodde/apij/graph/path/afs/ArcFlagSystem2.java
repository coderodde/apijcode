package com.coderodde.apij.graph.path.afs;

import com.coderodde.apij.ds.pq.PriorityQueue;
import com.coderodde.apij.ds.pq.support.DaryHeap;
import com.coderodde.apij.graph.model.Graph;
import com.coderodde.apij.graph.model.WeightFunction;
import com.coderodde.apij.graph.model.support.DirectedGraphNode;
import com.coderodde.apij.graph.path.Path;
import com.coderodde.apij.graph.path.PathFinder;
import static com.coderodde.apij.util.Utils.checkNotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ArcFlagSystem2 {
    
    private Graph graph;
    private Partitioner partitioner;
    private WeightFunction<DirectedGraphNode> w;
    
    private final ArcFlags firstLevelArcFlags;
    private final Map<Integer, ArcFlags> secondLevelArcFlags;
    
    private final RegionMap firstLevelRegionMap;
    private final Map<Integer, RegionMap> secondLevelRegionMap;
    
    private final List<Set<DirectedGraphNode>> firstLevelRegions;
    private final List<List<Set<DirectedGraphNode>>> secondLevelRegions;
    
    private final PriorityQueue<DirectedGraphNode, Double> OPEN;
    private final Set<DirectedGraphNode> CLOSED;
    private final Map<DirectedGraphNode, Double> GSCORE;
    private final Map<DirectedGraphNode, DirectedGraphNode> PARENT;
    
    public ArcFlagSystem2
        (final Partitioner partitioner,
         final PriorityQueue<DirectedGraphNode, Double> queue) {
        checkNotNull(queue, "'queue' is null.");
        setPartitioner(partitioner);
        
        this.firstLevelArcFlags = new ArcFlags();
        this.secondLevelArcFlags = new HashMap<>();
        
        this.firstLevelRegionMap = new RegionMap();
        this.secondLevelRegionMap = new HashMap<>();
        
        this.firstLevelRegions = new ArrayList<>();
        this.secondLevelRegions = new ArrayList<>();
        
        this.OPEN = queue;
        this.CLOSED = new HashSet<>();
        this.GSCORE = new HashMap<>();
        this.PARENT = new HashMap<>();
    }
        
    public ArcFlagSystem2(final Partitioner partitioner) {
        this(partitioner, new DaryHeap<DirectedGraphNode, Double>());
    }
        
    public final void setPartitioner(final Partitioner partitioner) {
        checkNotNull(partitioner, "'partitioner' is null.");
        this.partitioner = partitioner;
    }
        
    public Path<DirectedGraphNode> 
        search(final DirectedGraphNode source,
               final DirectedGraphNode target,
               final WeightFunction<DirectedGraphNode> w) {
        OPEN.clear();
        GSCORE.clear();
        PARENT.clear();
        
        OPEN.add(source, 0.0);
        GSCORE.put(source, 0.0);
        PARENT.put(source, null);
        
        final int TARGET_REGION_NUMBER = firstLevelRegionMap.get(target);
        final int SUB_TARGET_REGION_NUMBER = secondLevelRegionMap
                                             .get(TARGET_REGION_NUMBER)
                                             .get(target);
                
        while (OPEN.isEmpty() == false) {
            final DirectedGraphNode current = OPEN.extractMinimum();
            
            if (current.equals(target)) {
                return PathFinder.
                       <DirectedGraphNode>constructPath(target, PARENT);
            }

            final int FIRST_LEVEL_REGION_NUMBER = 
                    firstLevelRegionMap.get(current);
            
            final Set<DirectedGraphNode> currentRegion = 
                    firstLevelRegions.get(FIRST_LEVEL_REGION_NUMBER);
            
            for (final DirectedGraphNode child : current) {
                if (!firstLevelArcFlags.get(current, child)
                                       .get(TARGET_REGION_NUMBER)) {
                    continue;
                }
                
                if (currentRegion.contains(child)) {
                    if (!secondLevelArcFlags.get(FIRST_LEVEL_REGION_NUMBER)
                                            .get(current, child)
                                            .get(SUB_TARGET_REGION_NUMBER)) {
                        continue;
                    }
                }
                
                double tmpg = GSCORE.get(current) + w.get(current, child);
                
                if (GSCORE.containsKey(child) == false) {
                    GSCORE.put(child, tmpg);
                    PARENT.put(child, current);
                    OPEN.add(child, tmpg);
                } else if (GSCORE.get(child) > tmpg) {
                    GSCORE.put(child, tmpg);
                    PARENT.put(child, current);
                    OPEN.decreasePriority(child, tmpg);
                }
            }
        }
        
        return Path.NO_PATH;
    }
        
    public long preprocess(final Graph<DirectedGraphNode> graph,
                           final WeightFunction<DirectedGraphNode> w) {
        checkNotNull(graph, "'graph' is null.");
        checkNotNull(w, "'w' is null.");
        
        final long ta = System.currentTimeMillis();
        
        this.graph = graph;
        this.w = w;
        
        ///// First level preprocessing ////
        
        loadFirstLevelData();
        loadSecondLevelData();
        
        
        int index = 0;
        
        for (final Set<DirectedGraphNode> region : firstLevelRegions) {
            for (final DirectedGraphNode node : region) {
                firstLevelRegionMap.put(node, index++);
            }
            
            setInnerFlags(region, index++);
        }
        
        final List<List<DirectedGraphNode>> boundaryPointListList =
                findBoundaryPoints();
        
        for (final List<DirectedGraphNode> region : boundaryPointListList) {
            GSCORE.clear();
            
            for (final DirectedGraphNode boundaryPoint : region) {
                computeArcFlags(boundaryPoint);
            }
        }
        
        //// Second level preprocessing ////
        
        this.secondLevelRegions.clear();
        this.secondLevelRegionMap.clear();
        this.secondLevelArcFlags.clear();
        
        return System.currentTimeMillis() - ta;
    }
    
    private void loadFirstLevelData() {
        firstLevelRegions.clear();
        firstLevelArcFlags.clear();
        firstLevelRegionMap.clear();
        
        firstLevelRegions.addAll(partitioner.partition(graph.view()));
        
        int index = 0;
        
        for (final Set<DirectedGraphNode> region : firstLevelRegions) {
            for (final DirectedGraphNode node : region) {
                firstLevelRegionMap.put(node, index);
            }
            
            ++index;
        }
        
        final List<Set<DirectedGraphNode>> boundaryNodeList =
                findFirstLevelBoundaryNodes();
        
        for (final Set<DirectedGraphNode> regionBoundary : boundaryNodeList) {
            for (final DirectedGraphNode boundaryNode : regionBoundary) {
                computeArcFlags(boundaryNode);
            }
        }
    }
    
    private final List<Set<DirectedGraphNode>> findFirstLevelBoundaryNodes() {
        final List<Set<DirectedGraphNode>> ret =
                new ArrayList<>(firstLevelRegions.size());
        
        for (final Set<DirectedGraphNode> region : firstLevelRegions) {
            final Set<DirectedGraphNode> boundaryNodes = new HashSet<>();
            
            label:
            for (final DirectedGraphNode node : region) {
                for (final DirectedGraphNode parent : node.parents()) {
                    if (region.contains(parent) == false) {
                        boundaryNodes.add(node);
                        continue label;
                    }
                }
            }
            
            ret.add(boundaryNodes);
        }
        
        return ret;
    }
    
    private void loadSecondLevelData() {
        
    }
    
    private void computeArcFlags(final DirectedGraphNode source) {
        OPEN.clear();
        CLOSED.clear();

        OPEN.add(source, 0.0);
        GSCORE.put(source, 0.0);

        final int TARGET_REGION_NUMBER = firstLevelRegionMap.get(source);

        while (OPEN.isEmpty() == false) {
            final DirectedGraphNode current = OPEN.extractMinimum();

            CLOSED.add(current);

            for (final DirectedGraphNode parent : current.parents()) {
                ArcFlagVector afv = firstLevelArcFlags.get(parent, current);

                if (afv == null) {
                    afv = new ArcFlagVector(firstLevelRegions.size());
                    firstLevelArcFlags.put(parent, current, afv);
                }

                afv.set(TARGET_REGION_NUMBER);

                double tmpg = GSCORE.get(current) + w.get(parent, current);

                if (GSCORE.containsKey(parent) == false) {
                    GSCORE.put(parent, tmpg);
                    OPEN.add(parent, tmpg);
                } else if (GSCORE.get(parent) > tmpg) {
                    GSCORE.put(parent, tmpg);
                    OPEN.decreasePriority(parent, tmpg);
                }
            }
        }
    }
        
    private List<List<DirectedGraphNode>> findBoundaryPoints() {
        final List<List<DirectedGraphNode>> ret = new ArrayList<>();
        
        for (final Set<DirectedGraphNode> region : firstLevelRegions) {
            ret.add(getBoundaryPointsOfRegion(region));
        }
        
        return ret;
    }
    
    private List<DirectedGraphNode> 
        getBoundaryPointsOfRegion(final Set<DirectedGraphNode> region) {
        final List<DirectedGraphNode> ret = new ArrayList<>();
        
        outer: 
        for (final DirectedGraphNode node : region) {
            for (final DirectedGraphNode parent : node.parents()) {
                if (region.contains(parent) == false) {
                    ret.add(node);
                    continue outer;
                }
            }
        }
        
        return ret;
    }
    
    private void setInnerFlags(final Set<DirectedGraphNode> region, 
                               final int regionNumber) {
        for (final DirectedGraphNode node : region) {
            for (final DirectedGraphNode child : node) {
                if (region.contains(child)) {
                    ArcFlagVector afv = firstLevelArcFlags.get(node, child);
                    
                    if (afv == null) {
                        afv = new ArcFlagVector(firstLevelRegions.size());
                        firstLevelArcFlags.put(node, child, afv);
                    }
                    
                    afv.set(regionNumber);
                }
            }
        }
    }
}