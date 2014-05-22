package com.coderodde.apij.sort;

import java.util.Comparator;

/**
 * This interface defines the API for sorting algorithms.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public interface Sort {
    
    public void sort(Object[] array, Comparator cmp);
    
    public void sort(Object[] array, Comparator cmp, int from, int to);
}
