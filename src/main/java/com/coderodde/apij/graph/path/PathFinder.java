package com.coderodde.apij.graph.path;

import com.coderodde.apij.graph.model.Node;
import java.util.Map;

public abstract class PathFinder<T extends Node<T>> {
  
    protected static <T extends Node<T>> Path<T> constructPath
        (final T target, final Map<T, T> parentMap) {
        return null;
    }
      
}
