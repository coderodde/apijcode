package com.coderodde.apij;

import com.coderodde.apij.graph.model.Graph;
import com.coderodde.apij.graph.model.WeightFunction;
import com.coderodde.apij.graph.model.support.UndirectedGraphNode;
import com.coderodde.apij.graph.path.HeuristicFunction;
import com.coderodde.apij.graph.path.Layout;
import com.coderodde.apij.graph.path.Path;
import com.coderodde.apij.graph.path.PathFinder;
import com.coderodde.apij.graph.path.support.AStarFinder;
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
                                               10000,
                                               0.005f,
                                               1.3f,
                                               100.0,
                                               50.0,
                                               15.0,
                                               new Random());
        
        HeuristicFunction<UndirectedGraphNode> hf = 
                new EuclidianHeuristicFunction<>(data.third);
        
        final UndirectedGraphNode source = data.first.getNode("1");
        final UndirectedGraphNode target = data.first.getNode("6");
        
        long ta = System.currentTimeMillis();
        PathFinder<UndirectedGraphNode, 
                   AStarFinder<UndirectedGraphNode>> finder = 
                new AStarFinder<>();
        
        Path<UndirectedGraphNode> path = 
                finder
                .findPath()
                .from(source)
                .to(target)
                .withWeightFunction(data.second)
                .withHeuristicFunction(hf)
                .search();
        
        long tb = System.currentTimeMillis();
        
        System.out.println("A* time: " + (tb - ta) + " ms. Path length: " + 
                           path.getLength(data.second));
        
        PathFinder<UndirectedGraphNode, 
                   DijkstraFinder<UndirectedGraphNode>> finder2 =
                new DijkstraFinder<>();
        
        ta = System.currentTimeMillis();
        
        Path<UndirectedGraphNode> path2 =
               finder2.findPath()
                      .from(source)
                      .to(target)
                      .withWeightFunction(data.second)
                      .search();
        
        tb = System.currentTimeMillis();
        System.out.println("Dijkstra time: " + (tb - ta) + " ms. Path length: " +
                path2.getLength(data.second));
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
