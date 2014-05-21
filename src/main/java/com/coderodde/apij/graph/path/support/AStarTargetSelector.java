package com.coderodde.apij.graph.path.support;

import com.coderodde.apij.graph.model.Node;
import com.coderodde.apij.graph.path.TargetSelector;
import static com.coderodde.apij.util.Utils.checkBelongsToGraph;
import static com.coderodde.apij.util.Utils.checkNotNull;
import static com.coderodde.apij.util.Utils.checkSameGraphs;

public class AStarTargetSelector<T extends Node<T>> {
    
    private final AStarFinder<T> finder;
    
    private T target;
    
    AStarTargetSelector(final AStarFinder<T> finder) {
        checkNotNull(finder, "'finder' is 'null'.");
        this.finder = finder;
    }
    
    
    public AStarWeightFunctionSelector<T> to(final T target) {
        checkNotNull(target, "'target' is 'null'.");
        checkBelongsToGraph(target);
        checkSameGraphs(target.getOwnerGraph(), getSource().getOwnerGraph());
        this.target = target;
        return new AStarWeightFunctionSelector<T>(this);
    }
    
    AStarFinder<T> getFinder() {
        return finder;
    }
    
    T getSource() {
        return finder.getSource();
    }
    
    T getTarget() {
        return target;
    }
}
