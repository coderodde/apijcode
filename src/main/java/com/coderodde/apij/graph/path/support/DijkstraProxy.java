package com.coderodde.apij.graph.path.support;

import com.coderodde.apij.graph.model.Node;
import com.coderodde.apij.graph.path.Path;
import static com.coderodde.apij.util.Utils.checkNotNull;

public class DijkstraProxy<T extends Node<T>> {
    
    private final DijkstraWeightFunctionSelector<T> weightFunctionSelector;
    
    DijkstraProxy(final DijkstraWeightFunctionSelector<T> 
                        weightFunctionSelector) {
        checkNotNull(weightFunctionSelector, 
                     "'weightFunctionSelector' is null.");
        this.weightFunctionSelector = weightFunctionSelector;
    }
    
    public Path<T> search() {
        final DijkstraWeightFunctionSelector<T> s = weightFunctionSelector;
        return s.getFinder().searchImpl(s.getSource(),
                                        s.getTarget(),
                                        s.getWeightFunction());
    }
}
