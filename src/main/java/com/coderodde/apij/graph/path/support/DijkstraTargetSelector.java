package com.coderodde.apij.graph.path.support;

import com.coderodde.apij.graph.model.Node;
import static com.coderodde.apij.util.Utils.checkBelongsToGraph;
import static com.coderodde.apij.util.Utils.checkNotNull;
import static com.coderodde.apij.util.Utils.checkSameGraphs;

public class DijkstraTargetSelector<T extends Node<T>> {
    
    private final DijkstraFinder<T> finder;
    
    private T target;
    
    DijkstraTargetSelector(final DijkstraFinder<T> finder) {
        checkNotNull(finder, "'finder' is 'null'.");
        this.finder = finder;
    }
    
    public DijkstraWeightFunctionSelector<T> to(final T target) {
        checkNotNull(target, "'target' is 'null'.");
        checkBelongsToGraph(target);
        checkSameGraphs(target.getOwnerGraph(), getSource().getOwnerGraph());
        this.target = target;
        return new DijkstraWeightFunctionSelector<T>(this);
    }
    
    DijkstraFinder<T> getFinder() {
        return finder;
    }
    
    T getSource() {
        return finder.getSource();
    }
    
    T getTarget() {
        return target;
    }
}
