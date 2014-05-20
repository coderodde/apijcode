package com.coderodde.apij.graph.path.support;

import com.coderodde.apij.graph.model.Node;
import com.coderodde.apij.graph.path.Path;
import static com.coderodde.apij.util.Utils.checkNotNull;

public class AStarProxy<T extends Node<T>, 
                        W extends Comparable<? super W>> {
    
    private final AStarHeuristicFunctionSelector<T, W> heuristicFunctionSelector;
    
    AStarProxy(final AStarHeuristicFunctionSelector heuristicFunctionSelector) {
        checkNotNull(heuristicFunctionSelector, 
                     "'heuristicFunctionSelector' is null.");
        this.heuristicFunctionSelector = heuristicFunctionSelector;
    }
    
    Path<T> search() {
        AStarHeuristicFunctionSelector<T, W> s = heuristicFunctionSelector;
        return s.getFinder().searchImpl(s.getSource(),
                                        s.getTarget(),
                                        s.getWeightFunction(),
                                        s.getHeuristicFunction());
    }
}
