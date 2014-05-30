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

public class ArcFlagSystem {
    
    private Partitioner partitioner;
    private List<Set<DirectedGraphNode>> regionList;
    private WeightFunction<DirectedGraphNode> w;
    private final ArcFlags arcFlags;
    private final RegionMap regionMap;
    private final PriorityQueue<DirectedGraphNode, Double> OPEN;
    private final Set<DirectedGraphNode> CLOSED;
    private final Map<DirectedGraphNode, Double> GSCORE;
    private final Map<DirectedGraphNode, DirectedGraphNode> PARENT;
    
    public ArcFlagSystem
        (final Partitioner partitioner,
         final PriorityQueue<DirectedGraphNode, Double> queue) {
        checkNotNull(queue, "'queue' is null.");
        setPartitioner(partitioner);
        this.arcFlags = new ArcFlags();
        this.regionMap = new RegionMap();
        this.OPEN = queue;
        this.CLOSED = new HashSet<>();
        this.GSCORE = new HashMap<>();
        this.PARENT = new HashMap<>();
    }
        
    public ArcFlagSystem(final Partitioner partitioner) {
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
        CLOSED.clear();
        
        final int TARGET_REGION_NUMBER = regionMap.get(target);
        
        while (OPEN.isEmpty() == false) {
            final DirectedGraphNode current = OPEN.extractMinimum();
            
            if (current.equals(target)) {
                return PathFinder.
                       <DirectedGraphNode>constructPath(target, PARENT);
            }
            
            CLOSED.add(current);
            
            for (final DirectedGraphNode child : current) {
                if (CLOSED.contains(child)) {
                    continue;
                }
                
                if (!arcFlags.get(current, child).get(TARGET_REGION_NUMBER)) {
                    continue;
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
        final long ta = System.currentTimeMillis();
        this.w = w;
        this.regionList = partitioner.partition(graph.view());
        this.regionMap.clear();
        
        int index = 0;
        
        for (final Set<DirectedGraphNode> region : regionList) {
            for (final DirectedGraphNode node : region) {
                regionMap.put(node, index);
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
        
        return System.currentTimeMillis() - ta;
    }
    
    private void computeArcFlags(final DirectedGraphNode source) {
        OPEN.clear();

        OPEN.add(source, 0.0);
        GSCORE.put(source, 0.0);

        final int TARGET_REGION_NUMBER = regionMap.get(source);

        while (OPEN.isEmpty() == false) {
            final DirectedGraphNode current = OPEN.extractMinimum();

            for (final DirectedGraphNode parent : current.parents()) {
                ArcFlagVector afv = arcFlags.get(parent, current);

                if (afv == null) {
                    afv = new ArcFlagVector(regionMap.size());
                    arcFlags.put(parent, current, afv);
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
        
        for (final Set<DirectedGraphNode> region : regionList) {
            ret.add(getBoundaryPointsOfRegion(region));
        }
        
        return ret;
    }
    
    private List<DirectedGraphNode> 
        getBoundaryPointsOfRegion(final Set<DirectedGraphNode> region) {
        final Set<DirectedGraphNode> setView = new HashSet<>(region);
        final List<DirectedGraphNode> ret = new ArrayList<>();
        
        outer: 
        for (final DirectedGraphNode node : region) {
            for (final DirectedGraphNode parent : node.parents()) {
                if (setView.contains(parent) == false) {
                    ret.add(node);
                    continue outer;
                }
            }
        }
        
        return ret;
    }
    
    private void setInnerFlags(final Set<DirectedGraphNode> region, 
                               final int regionNumber) {
        final Set<DirectedGraphNode> set = new HashSet<>(region);
        
        for (final DirectedGraphNode node : region) {
            for (final DirectedGraphNode child : node) {
                if (set.contains(child)) {
                    ArcFlagVector afv = arcFlags.get(node, child);
                    
                    if (afv == null) {
                        afv = new ArcFlagVector(regionList.size());
                        arcFlags.put(node, child, afv);
                    }
                    
                    afv.set(regionNumber);
                }
            }
        }
    }
}