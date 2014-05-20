package com.coderodde.apij.graph.model;

/**
 *
 * @author rodionefremov
 */
public interface WeightFunction<T extends Node<T>, W> {
    
    public void put(final T from, final T to, final W weight);
    
    public W get(final T from, final T to);
}
