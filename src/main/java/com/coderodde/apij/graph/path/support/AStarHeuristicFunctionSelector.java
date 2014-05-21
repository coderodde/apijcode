package com.coderodde.apij.graph.path.support;

import com.coderodde.apij.graph.model.Node;
import com.coderodde.apij.graph.model.WeightFunction;
import com.coderodde.apij.graph.path.HeuristicFunction;
import static com.coderodde.apij.util.Utils.checkNotNull;

public class AStarHeuristicFunctionSelector<T extends Node<T>>{
    
    private AStarWeightFunctionSelector<T> weightFunctionSelector;
    private HeuristicFunction<T> heuristicFunction;
    
    AStarHeuristicFunctionSelector
        (final AStarWeightFunctionSelector<T> weightFunctionSelector) {
        checkNotNull(weightFunctionSelector, 
                     "'weightFunctionSelector' is null.");
        this.weightFunctionSelector = weightFunctionSelector;
    }
        
    public AStarProxy<T> withHeuristicFunction
        (final HeuristicFunction<T> heuristicFunction) {
        checkNotNull(heuristicFunction, "'heuristicFunction' is null.");
        this.heuristicFunction = heuristicFunction;
        return new AStarProxy<T>(this);
    }
        
    AStarFinder<T> getFinder() {
        return weightFunctionSelector.getFinder();
    }
    
    T getSource() {
        return weightFunctionSelector.getSource();
    }
    
    T getTarget() {
        return weightFunctionSelector.getTarget();
    }
    
    WeightFunction<T> getWeightFunction() {
        return weightFunctionSelector.getWeightFunction();
    }
    
    HeuristicFunction<T> getHeuristicFunction() {
        return heuristicFunction;
    }
}
