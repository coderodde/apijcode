package com.coderodde.apij.graph.path;

import com.coderodde.apij.graph.model.Node;
import static com.coderodde.apij.util.Utils.checkNotNull;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

public class Layout<T extends Node<T>> {
    
    private Map<T, Point2D.Double> map;
    
    public Layout() {
        this.map = new HashMap<>();
    }
    
    public void put(final T node, final Point2D.Double point) {
        checkNotNull(node, "'node' is null.");
        checkNotNull(point, "'point' is null.");
        map.put(node, point);
    }
    
    public Point2D.Double get(final T node) {
        return map.get(node);
    }
}
