package com.coderodde.apij.graph.path;

import com.coderodde.apij.graph.model.Node;
import static com.coderodde.apij.util.Utils.checkNotNull;

public abstract class HeuristicFunction<T extends Node<T>> {
    
    protected T target;
    protected Layout<T> layout;
    
    protected HeuristicFunction() {
        
    }
    
    protected HeuristicFunction(final T target) {
        setTarget(target);
    }
    
    public final void setLayout(final Layout<T> layout) {
        checkNotNull(layout, "'layout' is null.");
        this.layout = layout;
    }
    
    public final void setTarget(final T target) {
        checkNotNull(target, "'target' is null.");
        this.target = target;
    }
    
    public abstract double estimateFrom(final T from);
}
