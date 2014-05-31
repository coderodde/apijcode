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
    private final int levelTwoMaxNodes;
    
    private final ArcFlags firstLevelArcFlags;
    private final List<ArcFlags> secondLevelArcFlags;
    
    private final RegionMap firstLevelRegionMap;
    private final List<RegionMap> secondLevelRegionMap;
    
    private final List<Set<DirectedGraphNode>> firstLevelRegions;
    private final List<List<Set<DirectedGraphNode>>> secondLevelRegions;
    
    private final Set<DirectedGraphNode> CLOSED;
    private final Map<DirectedGraphNode, Double> GSCORE;
    private final PriorityQueue<DirectedGraphNode, Double> OPEN;
    private final Map<DirectedGraphNode, DirectedGraphNode> PARENT;
    
    public ArcFlagSystem2
        (final Partitioner partitioner, 
         final int levelOneMaxNodes,
         final int levelTwoMaxNodes,
         final PriorityQueue<DirectedGraphNode, Double> queue) {
        checkNotNull(queue, "'queue' is null.");
        setPartitioner(partitioner);
        partitioner.setMaxNodesPerRegion(levelOneMaxNodes);
        this.levelTwoMaxNodes = levelTwoMaxNodes;
        
        this.firstLevelArcFlags = new ArcFlags();
        this.secondLevelArcFlags = new ArrayList<>();
        
        this.firstLevelRegionMap = new RegionMap();
        this.secondLevelRegionMap = new ArrayList<>();
        
        this.firstLevelRegions = new ArrayList<>();
        this.secondLevelRegions = new ArrayList<>();
        
        this.OPEN = queue;
        this.GSCORE = new HashMap<>();
        this.PARENT = new HashMap<>();
        this.CLOSED = new HashSet<>();
    }
        
    public ArcFlagSystem2(final Partitioner partitioner, 
                          final int levelOneMaxNodes,
                          final int levelTwoMaxNodes) {
        this(partitioner, 
             levelOneMaxNodes,
             levelTwoMaxNodes,
             new DaryHeap<DirectedGraphNode, Double>());
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
        CLOSED.clear();
        
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
            
            CLOSED.add(current);

            final int FIRST_LEVEL_REGION_NUMBER = 
                    firstLevelRegionMap.get(current);
            
            final Set<DirectedGraphNode> currentRegion = 
                    firstLevelRegions.get(FIRST_LEVEL_REGION_NUMBER);
            
            for (final DirectedGraphNode child : current) {
                if (CLOSED.contains(child)) {
                    continue;
                }
                
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
        
        loadFirstLevelData();
        loadSecondLevelData();
        
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
        
        for (final Set<DirectedGraphNode> region : firstLevelRegions) {
            GSCORE.clear();
            setInnerFlags(region, firstLevelArcFlags);
        }
    }
    
    private void loadSecondLevelData() {
        secondLevelRegions.clear();
        secondLevelArcFlags.clear();
        secondLevelRegionMap.clear();
        partitioner.setMaxNodesPerRegion(levelTwoMaxNodes);
        
        for (Set<DirectedGraphNode> firstLevelRegion : firstLevelRegions) {
            final List<Set<DirectedGraphNode>> miniRegions = 
                    partitioner.partition(firstLevelRegion);
            secondLevelRegions.add(miniRegions);
            
            final RegionMap rm = new RegionMap();
            secondLevelRegionMap.add(rm);
            secondLevelArcFlags.add(new ArcFlags());
            
            int index = 0;
            
            for (final Set<DirectedGraphNode> miniRegion : miniRegions) {
                for (final DirectedGraphNode node : miniRegion) {
                    rm.put(node, index);
                }
                
                ++index;
            }
        }
        
        int index = 0;
        
        for (final List<Set<DirectedGraphNode>> region : secondLevelRegions) {
            for (final Set<DirectedGraphNode> miniRegion : region) {
                setInnerFlags(miniRegion, secondLevelArcFlags.get(index));
            }
            
            for (final Set<DirectedGraphNode> miniRegion : region) {
                Set<DirectedGraphNode> b = getBoundaryNodesOfRegion(miniRegion);
                GSCORE.clear();
                
                for (final DirectedGraphNode node : b) {
                    computeArcFlagsSecondLevel(node, miniRegion);
                }
            }
            
            ++index;
        }
    }
    
    private void setInnerFlags(final Set<DirectedGraphNode> region, 
                               final ArcFlags arcFlags) {
        for (final DirectedGraphNode node : region) {
            setInnerFlagsImpl(node, region, arcFlags);
        }
    }
    
    private void setInnerFlagsImpl(final DirectedGraphNode source,
                                   final Set<DirectedGraphNode> region,
                                   final ArcFlags arcFlags) {
        OPEN.clear();
        OPEN.add(source, 0.0);
        GSCORE.clear();
        GSCORE.put(source, 0.0);
        
        while (OPEN.isEmpty() == false) {
            final DirectedGraphNode current = OPEN.extractMinimum();
            
            for (final DirectedGraphNode parent : current.parents()) {
                if (region.contains(parent) == false) {
                    continue;
                }
                
                ArcFlagVector afv = arcFlags.get(parent, current);
                
                if (afv == null) {
                    afv = new ArcFlagVector(firstLevelRegions.size());
                    arcFlags.put(parent, current, afv);
                }
                
                afv.set(firstLevelRegionMap.get(source));
                
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
    
    private List<Set<DirectedGraphNode>> findFirstLevelBoundaryNodes() {
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
    
    private void computeArcFlags(final DirectedGraphNode source) {
        OPEN.clear();
        OPEN.add(source, 0.0);
//        GSCORE.clear();
        GSCORE.put(source, 0.0);

        final int TARGET_REGION_NUMBER = firstLevelRegionMap.get(source);

        while (OPEN.isEmpty() == false) {
            final DirectedGraphNode current = OPEN.extractMinimum();
            
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
    
    private Set<DirectedGraphNode> 
        getBoundaryNodesOfRegion(final Set<DirectedGraphNode> region) {
        final Set<DirectedGraphNode> ret = new HashSet<>(region.size());
        
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

    private void computeArcFlagsSecondLevel
        (final DirectedGraphNode source, final Set<DirectedGraphNode> bound) {
        OPEN.clear();
        OPEN.add(source, 0.0);
//        GSCORE.clear();
        GSCORE.put(source, 0.0);

        final int TARGET_REGION_NUMBER = firstLevelRegionMap.get(source);
        final int SUB_TARGET_REGION_NUMBER = 
                secondLevelRegionMap.get(TARGET_REGION_NUMBER).get(source);
        final ArcFlags af = secondLevelArcFlags.get(TARGET_REGION_NUMBER);

        while (OPEN.isEmpty() == false) {
            final DirectedGraphNode current = OPEN.extractMinimum();
            
            for (final DirectedGraphNode parent : current.parents()) {
                if (bound.contains(parent) == false) {
                    continue;
                }
                
                ArcFlagVector afv = af.get(parent, current);

                if (afv == null) {
                    afv = new ArcFlagVector(firstLevelRegions.size());
                    af.put(parent, current, afv);
                }

                afv.set(SUB_TARGET_REGION_NUMBER);

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
}