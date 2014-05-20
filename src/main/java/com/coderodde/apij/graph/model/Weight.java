package com.coderodde.apij.graph.model;

/**
 * This interface defines the API for the objects representing edge weights.
 * 
 * @author Rodion Efremov
 * 
 * @version 1.6
 * 
 * @param <T> 
 */
public interface Weight<T extends Comparable<? super T>> {
    
    /**
     * Returns the identity element.
     * 
     * @return the identity element.
     */
    public T identity();
    
    /**
     * Applies <code>t2</code> to <code>t1</code> and returns the result.
     * 
     * @param t1 the first element.
     * @param t2 the second element.
     * 
     * @return result <tt>t1 (op) t2</tt>.
     */
    public T apply(T t1, T t2);
    
    /**
     * Returns the inverse of <code>t</code>.
     * 
     * @param t the element whose inverse to return.
     * 
     * @return the inverse of <code>t</code>.
     */
    public T inverse(T t);
}
