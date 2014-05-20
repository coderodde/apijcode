package com.coderodde.apij.graph.path.support;

import com.coderodde.apij.graph.model.Node;
import static com.coderodde.apij.util.Utils.checkNotNull;

class AStarTargetSelector<T extends Node<T>, W extends Comparable<? super W>> {
    
    private final AStarFinder<T, W> finder;
    
    private T target;
    
    AStarTargetSelector(final AStarFinder<T, W> finder) {
        checkNotNull(finder, "'finder' is 'null'.");
        this.finder = finder;
    }
    
    
    AStarWeightFunctionSelector<T, W> to(final T target) {
        checkNotNull(target, "'target' is 'null'.");
        this.target = target;
        return new AStarWeightFunctionSelector<T, W>(this);
    }
    
    AStarFinder<T, W> getFinder() {
        return finder;
    }
    
    T getSource() {
        return finder.getSource();
    }
    
    T getTarget() {
        return target;
    }
}
