package com.coderodde.apij;

import com.coderodde.apij.graph.model.Graph;
import com.coderodde.apij.graph.model.WeightFunction;
import com.coderodde.apij.graph.model.support.UndirectedGraphNode;
import com.coderodde.apij.graph.path.HeuristicFunction;
import com.coderodde.apij.graph.path.Layout;
import com.coderodde.apij.graph.path.Path;
import com.coderodde.apij.graph.path.PathFinder;
import static com.coderodde.apij.graph.path.PathFinder.from;
import static com.coderodde.apij.graph.path.PathFinder.to;
import static com.coderodde.apij.graph.path.PathFinder.withBackwardHeuristicFunction;
import static com.coderodde.apij.graph.path.PathFinder.withHeuristicFunction;
import static com.coderodde.apij.graph.path.PathFinder.withWeightFunction;
import com.coderodde.apij.graph.path.support.AStarFinder;
import com.coderodde.apij.graph.path.support.BidirectionalAStarFinder;
import com.coderodde.apij.graph.path.support.BidirectionalDijkstraFinder;
import com.coderodde.apij.graph.path.support.DijkstraFinder;
import com.coderodde.apij.graph.path.support.EuclidianHeuristicFunction;
import com.coderodde.apij.util.Utils;
import static com.coderodde.apij.util.Utils.INDEX_NOT_FOUND;
import com.coderodde.apij.util.Utils.Triple;
import static com.coderodde.apij.util.Utils.findIndexOf;
import static com.coderodde.apij.util.Utils.findMaximum;
import static com.coderodde.apij.util.Utils.getRandomIntegerArray;
import static com.coderodde.apij.util.Utils.title;
import static com.coderodde.apij.util.Utils.title2;
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
        
        Triple<Graph<UndirectedGraphNode>,
      WeightFunction<UndirectedGraphNode>,
              Layout<UndirectedGraphNode>> data =
                Utils.getRandomUndirectedGraph("Graph",
                                               40000,
                                               0.0001f,
                                               1.3f,
                                               100.0,
                                               50.0,
                                               15.0,
                                               new Random());
        
        HeuristicFunction<UndirectedGraphNode> hf = 
                new EuclidianHeuristicFunction<>(data.third);
        
        HeuristicFunction<UndirectedGraphNode> hfb = 
                new EuclidianHeuristicFunction<>(data.third);
        
        final UndirectedGraphNode source = data.first.getNode("1");
        final UndirectedGraphNode target = data.first.getNode("4004");
        
        long ta = System.currentTimeMillis();
        PathFinder<UndirectedGraphNode> finder = new AStarFinder<>();
        
        Path<UndirectedGraphNode> path = 
                finder.search(from(source),
                              to(target),
                              withWeightFunction(data.second),
                              withHeuristicFunction(hf));
        
        long tb = System.currentTimeMillis();
        
        System.out.println("A* time: " + (tb - ta) + " ms. Path length: " + 
                           path.getLength(data.second));
        
        
        ta = System.currentTimeMillis();
        
        Path<UndirectedGraphNode> path2 =
               new DijkstraFinder<UndirectedGraphNode>()
                       .search(from(source),
                               to(target),
                               withWeightFunction(data.second));
        
        tb = System.currentTimeMillis();
        System.out.println("Dijkstra time: " + (tb - ta) + " ms. Path length: " +
                path2.getLength(data.second));
        
        ta = System.currentTimeMillis();
        
        Path<UndirectedGraphNode> path3 =
               new BidirectionalDijkstraFinder<UndirectedGraphNode>()
                       .search(from(source),
                               to(target),
                               withWeightFunction(data.second));
        
        tb = System.currentTimeMillis();
        System.out.println("Bidirectional Dijkstra time: " + (tb - ta) + " ms. Path length: " +
                path3.getLength(data.second));
        
        ta = System.currentTimeMillis();
        
        Path<UndirectedGraphNode> path4 =
               new BidirectionalAStarFinder<UndirectedGraphNode>()
                       .search(from(source),
                               to(target),
                               withWeightFunction(data.second),
                               withHeuristicFunction(hf),
                               withBackwardHeuristicFunction(hfb));
        
        tb = System.currentTimeMillis();
        System.out.println("Bidirectional A* time: " + (tb - ta) + " ms. Path length: " +
                path4.getLength(data.second));
//        profileBasicAlgorithms();
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
