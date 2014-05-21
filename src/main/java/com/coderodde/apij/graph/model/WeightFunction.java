package com.coderodde.apij.graph.model;

public interface WeightFunction<T extends Node<T>> {
    
    public void put(final T from, final T to, final double weight);
    
    public double get(final T from, final T to);
}
