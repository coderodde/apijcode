package com.coderodde.apij.sort;

import java.util.Comparator;

/**
 * This interface defines the API for sorting algorithms.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public interface Sort {
   
    public <T extends Comparable<? super T>> void sort(T[] array);
    
    public <T> void sort(T[] array, Comparator<T> cmp);
}
