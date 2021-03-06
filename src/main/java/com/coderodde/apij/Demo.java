package com.coderodde.apij;

import com.coderodde.apij.graph.model.Graph;
import com.coderodde.apij.graph.model.WeightFunction;
import com.coderodde.apij.graph.model.support.DirectedGraphNode;
import com.coderodde.apij.graph.path.Layout;
import com.coderodde.apij.graph.path.Path;
import com.coderodde.apij.graph.path.PathFinder;
import static com.coderodde.apij.graph.path.PathFinder.from;
import static com.coderodde.apij.graph.path.PathFinder.to;
import static com.coderodde.apij.graph.path.PathFinder.withWeightFunction;
import com.coderodde.apij.graph.path.afs.ArcFlagSystem;
import com.coderodde.apij.graph.path.afs.ArcFlagSystem2;
import com.coderodde.apij.graph.path.afs.support.kdTreePartitioner;
import com.coderodde.apij.graph.path.support.BidirectionalDijkstraFinder;
import com.coderodde.apij.graph.path.support.DijkstraFinder;
import com.coderodde.apij.util.Utils;
import static com.coderodde.apij.util.Utils.INDEX_NOT_FOUND;
import com.coderodde.apij.util.Utils.Triple;
import static com.coderodde.apij.util.Utils.findIndexOf;
import static com.coderodde.apij.util.Utils.findMaximum;
import static com.coderodde.apij.util.Utils.getClosestNodeTo;
import static com.coderodde.apij.util.Utils.getRandomIntegerArray;
import static com.coderodde.apij.util.Utils.pathsAreSame;
import static com.coderodde.apij.util.Utils.title;
import static com.coderodde.apij.util.Utils.title2;
import java.awt.geom.Point2D;
import java.util.Random;


/**
 * This class contains a performance demo for algorithms in "Algorithmic 
 * programming in Java".
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class Demo {
   
    public static final void main(final String... args) {
        profileDirectedGraphShortestPathAlgorithms();
    }

    private static void profileDirectedGraphShortestPathAlgorithms() {
        final int GRAPH_SIZE = 20000;
        //1401184740657L
        final long SEED = 313L;//1401454179647L;//= System.currentTimeMillis();
        final Random r = new Random(SEED);
        final double HEIGHT = 100.0;
        final double WIDTH = 100.0;
        
        System.out.println("Seed: " + SEED);
        
        long ta = System.currentTimeMillis();
        
        final Triple<Graph<DirectedGraphNode>,
            WeightFunction<DirectedGraphNode>,
                    Layout<DirectedGraphNode>> data =
                Utils.getRandomDirectedGraph("Graph1",
                                             GRAPH_SIZE, 
                                             0.00045f,
                                             100.0f,
                                             100.0f,
                                             3.0f,
                                             r);
        
        long tb = System.currentTimeMillis();
        
        System.out.println("Created a random directed graph in " + (tb - ta) +
                           " ms.");
         
        final DirectedGraphNode source = getClosestNodeTo(
                                            new Point2D.Double(0, 0), 
                                            data.first,
                                            data.third);
        
        final DirectedGraphNode target = getClosestNodeTo(
                                            new Point2D.Double(WIDTH, HEIGHT),
                                            data.first,
                                            data.third);
        
        ta = System.currentTimeMillis();
        PathFinder<DirectedGraphNode> finder = new DijkstraFinder<>();
        
        Path<DirectedGraphNode> path = 
                finder.search(from(source),
                              to(target),
                              withWeightFunction(data.second));
        
        tb = System.currentTimeMillis();        
        
        System.out.println(
                finder.getClass().getSimpleName() + ": " + (tb - ta) + " ms.");
        
        ta = System.currentTimeMillis();
        PathFinder<DirectedGraphNode> finder2 = 
                new BidirectionalDijkstraFinder<>();
        
        Path<DirectedGraphNode> path2 =
                finder2.search(from(source),
                               to(target),
                               withWeightFunction(data.second));
        
        tb = System.currentTimeMillis();
        
        System.out.println(
                finder2.getClass().getSimpleName() + ": " + (tb - ta) + " ms.");
        
        ArcFlagSystem afs = 
                new ArcFlagSystem(new kdTreePartitioner(1000, data.third));
        
        ArcFlagSystem2 afs2 = 
                new ArcFlagSystem2(new kdTreePartitioner(1000, data.third),
                                                         1000,
                                                         50);
        
        long duration = afs.preprocess(data.first,
                                       data.second);
        
        System.out.println("Arc-flag Dijkstra system preprocessed in " +
                           duration + " ms.");
        
        ta = System.currentTimeMillis();
        
        Path<DirectedGraphNode> path3 = afs.search(source,
                                                   target,
                                                   data.second);
        
        tb = System.currentTimeMillis();
        
        System.out.println("Dijkstra's algorithm with arc-flags: " + (tb - ta) +
                           " ms.");
        
        ////
        
        duration = afs2.preprocess(data.first, data.second);
                
        
        System.out.println("Arc-flag Dijkstra system with two-level preprocessed in " +
                           duration + " ms.");
        
        ta = System.currentTimeMillis();
        
        Path<DirectedGraphNode> path4 = afs2.search(source,
                                                    target,
                                                    data.second);
        
        tb = System.currentTimeMillis();
        
        System.out.println("Dijkstra's algorithm with two-level arc-flags: " + (tb - ta) +
                           " ms.");
        
        System.out.println(
                "Paths are identical: " + pathsAreSame(path, path2, path3, path4));
        
        System.out.println(path.getLength(data.second));
        System.out.println(path2.getLength(data.second));
        System.out.println(path3.getLength(data.second));
        System.out.println(path4.getLength(data.second));
    }
    
    private static final void profileBasicAlgorithms() {
        title("Basic algorithms");
        
        final long SEED = System.currentTimeMillis();
        final Random r = new Random();
        
        for (int n = 100000; n < 25600001; n *= 2) {
            Integer[] arr = getRandomIntegerArray(n, 0, n, r);
            title2(arr.length + " elements");
            
            // Measure the time spent executing 'findIndexOf' in milliseconds.
            long ta = System.currentTimeMillis();
            
            int index = findIndexOf(arr, 88);
            
            long tb = System.currentTimeMillis();
            
            System.out.println("findIndexOf() took " + (tb - ta) + " ms.");
            
            if (index != INDEX_NOT_FOUND) {
                System.out.println("Correct: " + (arr[index] == 88));
            }
            
            // Measure the time spent executing 'findMaximum'.
            ta = System.currentTimeMillis();
            
            findMaximum(arr);
            
            tb = System.currentTimeMillis();
            
            System.out.println("findMaximum() took " + (tb - ta) + " ms.");
        }
    }
}
