package com.coderodde.apij.graph.path.support;

import com.coderodde.apij.graph.model.Node;
import com.coderodde.apij.graph.model.WeightFunction;
import com.coderodde.apij.graph.path.HeuristicFunction;
import com.coderodde.apij.graph.path.Path;
import static com.coderodde.apij.util.Utils.checkNotNull;

class AStarHeuristicFunctionSelector<T extends Node<T>,
                                     W extends Comparable<? super W>>{
    
    private AStarWeightFunctionSelector<T, W> weightFunctionSelector;
    private HeuristicFunction<T, W> heuristicFunction;
    
    AStarHeuristicFunctionSelector
        (final AStarWeightFunctionSelector<T, W> weightFunctionSelector) {
        checkNotNull(weightFunctionSelector, 
                     "'weightFunctionSelector' is null.");
        this.weightFunctionSelector = weightFunctionSelector;
    }
        
    AStarProxy<T, W> withHeuristicFunction
        (final HeuristicFunction<T, W> heuristicFunction) {
        checkNotNull(heuristicFunction, "'heuristicFunction' is null.");
        this.heuristicFunction = heuristicFunction;
        return new AStarProxy<T, W>(this);
    }
        
    AStarFinder<T, W> getFinder() {
        return weightFunctionSelector.getFinder();
    }
    
    T getSource() {
        return weightFunctionSelector.getSource();
    }
    
    T getTarget() {
        return weightFunctionSelector.getTarget();
    }
    
    WeightFunction<T, W> getWeightFunction() {
        return weightFunctionSelector.getWeightFunction();
    }
    
    HeuristicFunction<T, W> getHeuristicFunction() {
        return heuristicFunction;
    }
}
