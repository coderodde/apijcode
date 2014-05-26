package com.coderodde.apij.graph.path.afs.support;

import com.coderodde.apij.graph.model.Graph;
import com.coderodde.apij.graph.model.WeightFunction;
import com.coderodde.apij.graph.model.support.DirectedGraphNode;
import com.coderodde.apij.graph.path.Layout;
import com.coderodde.apij.util.Utils.Triple;
import static com.coderodde.apij.util.Utils.getRandomDirectedGraph;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * This class tests <code>kdTreePartitioner</code>.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class kdTreeTest {
   
    private static final int GRAPH_SIZE = 1000;
    
    private final Layout<DirectedGraphNode> layout;
    private final Graph<DirectedGraphNode> graph;
    
    private kdTreePartitioner tree;

    public kdTreeTest() {
        Triple<Graph<DirectedGraphNode>,
               WeightFunction<DirectedGraphNode>,
               Layout<DirectedGraphNode>> data =
               getRandomDirectedGraph("Graph",
                                      GRAPH_SIZE,
                                      0.5f,
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
        tree = new kdTreePartitioner(MAX_NODES_PER_PARTITION, layout);
        List<List<DirectedGraphNode>> listList = tree.partition(graph.view());
        int total = 0;
        
        for (final List<DirectedGraphNode> set : listList) {
            total += set.size();
            assertTrue(set.size() <= MAX_NODES_PER_PARTITION);
        }
        
        assertEquals(GRAPH_SIZE, total);
        assertTrue(isValidSeparation(listList));
    }
    
    private boolean isValidSeparation
        (final List<List<DirectedGraphNode>> listList) {
        List<Rectangle2D.Double> rectangles = new ArrayList<>(listList.size());
        
        for (final List<DirectedGraphNode> list : listList) {
            rectangles.add(setToRectangle(list));
        }
        
        for (int i = 0; i < rectangles.size() - 1; ++i) {
            for (int j = i + 1; j < rectangles.size(); ++j) {
                if (rectanglesIntersect(rectangles.get(i),
                                        rectangles.get(j))) {
                    return false;
                }
            }
        }
        
        return true;
    }
        
    private Rectangle2D.Double setToRectangle
        (final List<DirectedGraphNode> list) {
        Point2D.Double p = layout.get(list.get(0));
        
        double top = p.y;
        double bottom = p.y;
        
        double left = p.x;
        double right = p.x;
        
        for (final DirectedGraphNode node : list) {
            p = layout.get(node);
            
            if (top > p.y) {
                top = p.y;
            } else if (bottom < p.y) {
                bottom = p.y;
            }
            
            if (left > p.x) {
                left = p.x;
            } else if (right < p.x) {
                right = p.x;
            }
        }
        
        return new Rectangle2D.Double(left, 
                                      top, 
                                      right - left, 
                                      bottom - top);
    }
        
    private boolean rectanglesIntersect(final Rectangle2D.Double r1,
                                        final Rectangle2D.Double r2) {
        return r1.intersects(r2);
    }
}
