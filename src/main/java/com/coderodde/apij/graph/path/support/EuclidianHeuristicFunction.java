package com.coderodde.apij.graph.path.support;

import com.coderodde.apij.graph.model.Node;
import com.coderodde.apij.graph.path.HeuristicFunction;
import com.coderodde.apij.graph.path.Layout;
import com.coderodde.apij.graph.path.Point;

public class EuclidianHeuristicFunction<T extends Node<T>> 
extends HeuristicFunction<T> {

    public EuclidianHeuristicFunction() {
        
    }
    
    public EuclidianHeuristicFunction(final T target) {
        setTarget(target);
    }
    
    public EuclidianHeuristicFunction(final Layout<T> layout) {
        setLayout(layout);
    }
    
    public EuclidianHeuristicFunction(final T target, final Layout<T> layout) {
        setTarget(target);
        setLayout(layout);
    }
    
    @Override
    public double estimateFrom(final T from) {
        final Double[] s = layout.get(from).getRef();
        final Double[] t = layout.get(target).getRef();
        double h = 0.0;
        
        for (int i = 0; i < s.length; ++i) {
            final double delta = s[i] - t[i];
            h += delta * delta;
        }
        
        return Math.sqrt(h);
    }

    @Override
    public double estimate(final T from, final T to) {
        final Double[] s = layout.get(from).getRef();
        final Double[] t = layout.get(to).getRef();
        double h = 0.0;
        
        for (int i = 0; i < s.length; ++i) {
            final double delta = s[i] - t[i];
            h += delta * delta;
        }
        
        return Math.sqrt(h);
    }
}