package com.coderodde.apij.util;

import com.coderodde.apij.graph.model.Graph;
import com.coderodde.apij.graph.model.Node;
import java.util.Random;

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
        for (int i = 0; i < array.length - 1; ++i) {
            if (array[i].compareTo(array[i + 1]) > 0) {
                return false;
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
