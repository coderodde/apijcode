package com.coderodde.apij.graph.path;

import com.coderodde.apij.graph.model.Node;
import java.util.Map;

public abstract class PathFinder<T extends Node<T>, P extends PathFinder<T, P>> {
  
    public abstract P findPath();
    
    protected static <T extends Node<T>> Path<T> constructPath
        (final T target, final Map<T, T> parentMap) {
        Path<T> path = new Path<>();
        T current = target;
        
        while (current != null) {
            path.prependNode(current);
            current = parentMap.get(current);
        }
        
        return path;
    }
     
    protected static <T extends Node<T>> Path<T> 
        constructPathBidirectional(final T touch, 
                                   final Map<T, T> parentMapForward,
                                   final Map<T, T> parentMapBackwards) {
        return null;    
    }
}
