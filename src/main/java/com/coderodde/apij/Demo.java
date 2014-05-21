package com.coderodde.apij;

import com.coderodde.apij.graph.model.Graph;
import com.coderodde.apij.graph.model.WeightFunction;
import com.coderodde.apij.graph.model.support.DefaultWeightFunction;
import com.coderodde.apij.graph.model.support.UndirectedGraphNode;
import com.coderodde.apij.graph.path.HeuristicFunction;
import com.coderodde.apij.graph.path.Path;
import com.coderodde.apij.graph.path.support.AStarFinder;
import static com.coderodde.apij.util.Utils.INDEX_NOT_FOUND;
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
        Graph<UndirectedGraphNode> g = new Graph<>("G");
        
        g.add(new UndirectedGraphNode("a"));
        g.add(new UndirectedGraphNode("b"));
        
        g.addEdge(g.getNode("a"), g.getNode("b"));
        
        WeightFunction<UndirectedGraphNode> wf =
                new DefaultWeightFunction<>();
        
        HeuristicFunction<UndirectedGraphNode> hf = null;
        
        wf.put(g.getNode("a"), g.getNode("b"), 2);
        
        Path<UndirectedGraphNode> path = 
                new AStarFinder<UndirectedGraphNode>()
                .from(g.getNode("a"))
                .to(g.getNode("b"))
                .withWeightFunction(wf)
                .withHeuristicFunction(hf)
                .search();
        profileBasicAlgorithms();
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
