package com.coderodde.apij.graph.path;

import com.coderodde.apij.graph.model.Node;
import static com.coderodde.apij.util.Utils.checkNotNull;
import java.awt.geom.Point2D;

public abstract class HeuristicFunction<T extends Node<T>> {
    
    protected Point2D.Double targetPoint;
    protected Layout<T> layout;
    
    protected HeuristicFunction(final Layout<T> layout) {
        checkNotNull(layout, "'layout' is null.");
        this.layout = layout;
    }
    
    public final void setTarget(final T target) {
        checkNotNull(target, "'target' is null.");
        targetPoint = layout.get(target);
    }
    
    public abstract double estimateFrom(final T from);
    
    public abstract double estimate(final T from, final T to);
}
