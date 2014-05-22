package com.coderodde.apij.util;

import com.coderodde.apij.graph.model.Graph;
import com.coderodde.apij.graph.model.Node;
import com.coderodde.apij.graph.model.WeightFunction;
import com.coderodde.apij.graph.model.support.DefaultWeightFunction;
import com.coderodde.apij.graph.model.support.DirectedGraphNode;
import com.coderodde.apij.graph.model.support.UndirectedGraphNode;
import com.coderodde.apij.graph.path.HeuristicFunction;
import com.coderodde.apij.graph.path.Layout;
import com.coderodde.apij.graph.path.Path;
import com.coderodde.apij.graph.path.Point;
import com.coderodde.apij.graph.path.support.EuclidianHeuristicFunction;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * This class contains utility classes and methods.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class Utils {
   
    /**
     * A sentinel value to indicate the fact of absence of an index satisfying
     * a property.
     */
    public static final int INDEX_NOT_FOUND = -1;
    
    private static final int BAR_LENGTH = 80;
    
    /**
     * This method creates a random integer array.
     * 
     * @param size the length of the array.
     * @param min  the minimum element in the array.
     * @param max  the maximum element in the array.
     * @param r    the random number generator.
     * 
     * @return the array of length <code>size</code> with elements in the range
     * <code>[min, max]</code>.
     */
    public static final Integer[] getRandomIntegerArray(final int size,
                                                        final int min,
                                                        final int max,
                                                        final Random r) {
        checkMinMax(min, max);
        final Integer[] arr = new Integer[size];
        
        for (int i = 0; i < size; ++i) {
            arr[i] = r.nextInt(max - min + 1) + min;
        }
        
        return arr;
    }
    
    /**
     * Returns the index of the first (leftmost) element equal to
     * <code>element</code>, or <code>INDEX_NOT_FOUND</code> if there is no 
     * such.
     * 
     * @param  <T>     the element type of <code>array</code>.
     * @param  array   the array to search in.
     * @param  element the element to search.
     * 
     * @return the index of an element <code>element</code> or 
     * <code>INDEX_NOT_FOUND</code> if there is no such.
     */
    public static final <T> int findIndexOf(final T[] array, final T element) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        
        for (int i = 0; i < array.length; ++i) {
            if (array[i].equals(element)) {
                return i;
            }
        }
        
        return INDEX_NOT_FOUND;
    }
    
    /**
     * Returns the index of a maximum element in the input array.
     * 
     * @param <T>   the element type.
     * @param array the input array.
     * 
     * @return      the index of the maximum element or 
     * <code>INDEX_NOT_FOUND</code> if there is no such.
     */
    public static final <T extends Comparable<? super T>> 
        int findMaximum(final T[] array) {
        if (array == null || array.length == 0) {
            return INDEX_NOT_FOUND;
        }
        
        T max = array[0];
        int index = INDEX_NOT_FOUND;
        
        for (int i = 1; i < array.length; ++i) {
            final T current = array[i];
            
            if (max.compareTo(current) < 0) {
                max = current;
                index = i;
            }
        }
        
        return index;
    }
        
    public static final <T extends Comparable<? super T>> 
        boolean isSorted(final T[] array, int from, int to) {
        for (int i = from; i < to; ++i) {
            if (array[i].compareTo(array[i + 1]) > 0) {
                return false;
            }
        }
        
        return true;
    }
        
    /**
     * Checks whether the array is sorted and returns <code>true</code> in case
     * <code>array</code> is sorted, and <code>false</code> otherwise.
     * 
     * @param <T>   the element type, must be <code>Comparable</code>.
     * @param array the array to check.
     * 
     * @return      <code>true</code> if the array is sorted, <code>false</code>
     * otherwise.
     */
    public static final <T extends Comparable<? super T>>
        boolean isSorted(final T[] array) {
        return isSorted(array, 0, array.length - 1);
    }
        
//    public static final <T extends Comparable<? super T>>
//        boolean isSorted(final Object[] array, Comparator cmp) {
//        for (int i = 0; i < array.length - 1; ++i) {
//            if (cmp.compare(array[i], array[i + 1]) > 0) {
//                return false;
//            }
//        }
//        
//        return true;
//    }
    
    public static final boolean arraysSameByRef(final Object[]... arrays) {
        for (int i = 0; i < arrays.length - 1; ++i) {
            if (arrays[i].length != arrays[i + 1].length) {
                return false;
            }
        }
        
        for (int i = 0; i < arrays[0].length; ++i) {
            for (int j = 0; j < arrays.length - 1; ++j) {
                if (arrays[j][i] != arrays[j + 1][i]) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    public static final boolean arraysSame(final Object[]... arrays) {
        for (int i = 0; i < arrays.length - 1; ++i) {
            if (arrays[i].length != arrays[i + 1].length) {
                return false;
            }
        }
        
        for (int i = 0; i < arrays[0].length; ++i) {
            for (int j = 0; j < arrays.length - 1; ++j) {
                if (arrays[j][i].equals(arrays[j + 1][i]) == false) {
                    return false;
                }
            }
        }
        
        return true;
    }
        
    /**
     * The first title.
     * 
     * @param text the text to display.
     */
    public static final void title(final String text) {
        titleImpl(text, '*');
    }
    
    /**
     * The second title.
     * 
     * @param text the text to display. 
     */
    public static final void title2(final String text) {
        titleImpl(text, '-');
    }
    
    public static final void checkNotNull(final Object reference,
                                          final String message) {
        if (reference == null) {
            throw new NullPointerException(message);
        }
    }
        
    public static final <T extends Node<T>> void checkBelongsToGraph
        (final Node<T> node) {
        if (node.getOwnerGraph() == null) {
            throw new IllegalStateException(
                    "The input node does not belong to any graph.");
        }
    }
    
    public static final <T extends Node<T>> void checkBelongsToGraph
            (final T node, final Graph<T> graph) {
        if (graph.containsNode(node) == false) {
            throw new IllegalStateException(
                    "The input node does not belong to the input graph.");
        }
    }
        
    public static final <T extends Node<T>> void checkSameGraphs
        (final Graph<T> g1, final Graph<T> g2) {
        if (g1.getName().equals(g2.getName()) == false) {
            throw new IllegalStateException("The two graphs are not same.");
        } 
    }
     
    public static final <T extends Node<T>> void checkSameGraphs
            (final T n1, final T n2) {
        checkNotNull(n1, "'n1' is null.");        
        checkNotNull(n2, "'n2' is null."); 
        checkBelongsToGraph(n1);
        checkBelongsToGraph(n2);
        checkSameGraphs(n1.getOwnerGraph(), n2.getOwnerGraph());
    }
        
    public static final class Pair<F, S> {
        public F first;
        public S second;
        
        public Pair() {
            
        }
        
        public Pair(final F first, final S second) {
            this.first = first;
            this.second = second;
        }
    }
        
    public static final class Triple<F, S, T> {
        public F first;
        public S second;
        public T third;
        
        public Triple() {
            
        }
        
        public Triple(final F first, final S second, final T third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }
    }
    
    public static final Triple<Graph<UndirectedGraphNode>,
                      WeightFunction<UndirectedGraphNode>,
                              Layout<UndirectedGraphNode>>
                    getRandomUndirectedGraph(final String name,
                                             final int size,
                                             final float edgeLoadFactor,
                                             final float lengthFactor,
                                             final double regionWidth,
                                             final double regionHeight,
                                             final double maxDistance,
                                             final Random r) {
        final Graph<UndirectedGraphNode> g = new Graph<>(name);
        final Layout<UndirectedGraphNode> layout = new Layout<>();
        final HeuristicFunction<UndirectedGraphNode> hf =
                new EuclidianHeuristicFunction<>(layout);
        
        for (int i = 0; i < size; ++i) {
            final UndirectedGraphNode u = new UndirectedGraphNode("" + i);
            g.add(u);
            final double x = r.nextDouble() * regionWidth;
            final double y = r.nextDouble() * regionHeight;
            layout.put(u, new Point2D.Double(x, y));
        }
        
        int edges = (int)(size * size * edgeLoadFactor / 2);
        
        final List<UndirectedGraphNode> list = new ArrayList<>();
        final WeightFunction<UndirectedGraphNode> wf = 
                new DefaultWeightFunction<>();
        
        for (final UndirectedGraphNode u : g) {
            list.add(u);
        }
        
        while (edges > 0) {
            final int key1 = r.nextInt(g.size());
            final int key2 = r.nextInt(g.size());
            
            if (key1 == key2) {
                continue;
            }
            
            final UndirectedGraphNode node1 = list.get(key1);
            final UndirectedGraphNode node2 = list.get(key2);
            
            final double dist = hf.estimate(node1, node2);
            
            if (dist < maxDistance) {
                node1.connectTo(node2);
                wf.put(node1, node2, lengthFactor * dist);
                --edges;
            }
        }
        
        return new Triple<>(g, wf, layout);
    }
    
    public static final Triple<Graph<DirectedGraphNode>,
                      WeightFunction<DirectedGraphNode>,
                              Layout<DirectedGraphNode>>
                    getRandomDirectedGraph(final String name,
                                           final int size,
                                           final float edgeLoadFactor,
                                           final float lengthFactor,
                                           final double regionWidth,
                                           final double regionHeight,
                                           final double maxDistance,
                                           final Random r) {
        final Graph<DirectedGraphNode> g = new Graph<>(name);
        final Layout<DirectedGraphNode> layout = new Layout<>();
        final HeuristicFunction<DirectedGraphNode> hf =
                new EuclidianHeuristicFunction<>(layout);
        
        for (int i = 0; i < size; ++i) {
            final DirectedGraphNode u = new DirectedGraphNode("" + i);
            g.add(u);
            final double x = r.nextDouble() * regionWidth;
            final double y = r.nextDouble() * regionHeight;
            layout.put(u, new Point2D.Double(x, y));
        }
        
        int edges = (int)(size * size * edgeLoadFactor);
        
        final List<DirectedGraphNode> list = new ArrayList<>();
        final WeightFunction<DirectedGraphNode> wf = 
                new DefaultWeightFunction<>();
        
        for (final DirectedGraphNode u : g) {
            list.add(u);
        }
        
        while (edges > 0) {
            final int key1 = r.nextInt(g.size());
            final int key2 = r.nextInt(g.size());
            
            if (key1 == key2) {
                continue;
            }
            
            final DirectedGraphNode node1 = list.get(key1);
            final DirectedGraphNode node2 = list.get(key2);
            
            final double dist = hf.estimate(node1, node2);
            
            if (dist < maxDistance) {
                node1.connectTo(node2);
                wf.put(node1, node2, lengthFactor * dist);
                --edges;
            }
        }
        
        return new Triple<>(g, wf, layout);
    }
    
    public static final <T extends Node<T>> 
        void checkNotNullGraph(final Collection<T> nodes) {
        for (final T node : nodes) {
            checkNotNull(node, "'node' is null.");
            
            if (node.getOwnerGraph() == null) {
                throw new IllegalStateException(
                "Node " + node.getName() + " has no owner graph.");
            }
        }
    }
                    
    public static final <T extends Node<T>> void
        nodeSetBelongsToGraph(final Collection<T> set) {
        checkNotNullGraph(set);
        
        Graph<T> graph = null;
        
        for (final T node : set) {
            if (graph == null) {
                graph = node.getOwnerGraph();
            } else if (node.getOwnerGraph() != graph) {
                throw new IllegalStateException(
                "Node " + node.getName() + " has no owner graph.");
            }
        }
    }
                    
    public static final <T extends Node<T>> boolean 
        pathsAreSame(final Path<T>... paths) {
        for (int i = 0; i < paths.length - 1; ++i) {
            if (paths[i].size() != paths[i + 1].size()) {
                return false;
            }
        }
        
        for (int i = 0; i < paths[0].size(); ++i) {
            for (int j = 0; j < paths.length - 1; ++j) {
                if (paths[j].get(i).equals(paths[j + 1].get(i)) == false) {
                    return false;
                }
            }
        }
        
        return true;
    }
        
    public static final void checkPositive(final int i, final String msg) {
        checkNotBelow(i, 1, msg);
    }    
    
    public static final void checkNotBelow(final int i, 
                                           final int lowerBound,
                                           final String msg) {
        if (i < lowerBound) {
            throw new IllegalArgumentException(msg);
        }
    }
                    
    /**
     * Implements the title printing functionality.
     * 
     * @param text the text to display in the title.
     * @param barChar the character used to decorate the title bar.
     */
    private static final void titleImpl(final String text, final char barChar) {
        // The idiom ">> 1" means divide by two.
        final int before = (BAR_LENGTH - 2 - text.length()) >> 1;
        final int after = BAR_LENGTH - 2 - before;
        final StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < before; ++i) {
            sb.append(barChar);
        }
        
        sb.append(' ').append(text).append(' ');
        
        for (int i = 0; i < after; ++i) {
            sb.append(barChar);
        }
        
        System.out.println(sb.toString());
    }
        
    /**
     * Checks whether the <code>min</code> is not above <code>max</code>.
     * 
     * @param <T> the type of elements.
     * @param min the minimum value.
     * @param max the maximum value.
     * 
     * @throws IllegalArgumentException if <code>min</code> is larger than
     * <code>max</code>.
     */
    private static final <T extends Comparable<? super T>> void 
    checkMinMax(T min, T max) {
        if (min.compareTo(max) > 0) {
            // Here we have "min > max".
            throw new IllegalArgumentException("'min' is larger than 'max'.");
        }
    }
}
