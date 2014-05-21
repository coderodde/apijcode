package com.coderodde.apij.graph.path;

import com.coderodde.apij.graph.model.Node;

public interface TargetSelector<T, N extends Node<N>> {
    
    public T from(final N source);
}
