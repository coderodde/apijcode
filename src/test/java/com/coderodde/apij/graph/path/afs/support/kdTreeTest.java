package com.coderodde.apij.graph.path.afs.support;

import com.coderodde.apij.graph.model.Graph;
import com.coderodde.apij.graph.model.WeightFunction;
import com.coderodde.apij.graph.model.support.DirectedGraphNode;
import com.coderodde.apij.graph.path.Layout;
import com.coderodde.apij.util.Utils;
import com.coderodde.apij.util.Utils.Triple;
import static com.coderodde.apij.util.Utils.getRandomDirectedGraph;
import java.util.List;
import java.util.Random;
import java.util.Set;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author rodionefremov
 */
public class kdTreeTest {
   
    private final Layout<DirectedGraphNode> layout;
    private final Graph<DirectedGraphNode> graph;
    
    private kdTree tree;

    public kdTreeTest() {
        Triple<Graph<DirectedGraphNode>,
               WeightFunction<DirectedGraphNode>,
               Layout<DirectedGraphNode>> data =
               getRandomDirectedGraph("Graph",
                                      100,
                                      0.5f,
                                      1.3f,
                                      20.0,
                                      20.0,
                                      10.0,
                                      new Random());
        
        this.graph = data.first;
        this.layout = data.third;
    }
    
    @Test
    public void testPartition() {
        final int MAX_NODES_PER_PARTITION = 10;
        tree = new kdTree(MAX_NODES_PER_PARTITION, layout);
        List<Set<DirectedGraphNode>> setList = tree.partition(graph.view());
        
        for (final Set<DirectedGraphNode> set : setList) {
            assertTrue(set.size() <= MAX_NODES_PER_PARTITION);
        }
    }
    
}
