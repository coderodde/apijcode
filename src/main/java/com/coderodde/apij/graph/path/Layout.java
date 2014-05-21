package com.coderodde.apij.graph.path;

import com.coderodde.apij.graph.model.Node;
import static com.coderodde.apij.util.Utils.checkNotNull;
import java.util.HashMap;
import java.util.Map;

public class Layout<T extends Node<T>> {
    
    private final int dimensions;
    
    private Map<T, Point> map;
    
    public Layout(final int dimensions) {
        this.dimensions = dimensions;
        this.map = new HashMap<>();
    }
    
    public void put(final T node, final Point point) {
        checkNotNull(node, "'node' is null.");
        checkNotNull(point, "'point' is null.");
        checkDimensions(point);
        map.put(node, point);
    }
    
    public Point get(final T node) {
        return map.get(node);
    }
    
    private void checkDimensions(Point point) {
        if (point.size() != dimensions) {
            throw new IllegalArgumentException(
                    "'point' does not have dimension of " + dimensions + ".");
        }
    }
}
