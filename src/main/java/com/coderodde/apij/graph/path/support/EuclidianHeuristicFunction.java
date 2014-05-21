package com.coderodde.apij.graph.path.support;

import com.coderodde.apij.graph.model.Node;
import com.coderodde.apij.graph.path.HeuristicFunction;
import com.coderodde.apij.graph.path.Layout;
import java.awt.geom.Point2D;

public class EuclidianHeuristicFunction<T extends Node<T>> 
extends HeuristicFunction<T> {
    
    public EuclidianHeuristicFunction(final Layout<T> layout) {
        super(layout);
    }
    
    @Override
    public double estimateFrom(final T from) {
        final Point2D.Double p = layout.get(from);
        return p.distance(targetPoint);
    }

    @Override
    public double estimate(final T from, final T to) {
        final Point2D.Double p1 = layout.get(from);
        final Point2D.Double p2 = layout.get(to);
        return p1.distance(p2);
    }
}