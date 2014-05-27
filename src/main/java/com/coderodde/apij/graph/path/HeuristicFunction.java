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
    
    public static final double estimate(final Point2D.Double p1, 
                                        final Point2D.Double p2) {
        final double dx = p1.x - p2.x;
        final double dy = p1.y - p2.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
