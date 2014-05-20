package com.coderodde.apij.graph.path.support;

import com.coderodde.apij.graph.model.Node;
import com.coderodde.apij.graph.model.WeightFunction;
import static com.coderodde.apij.util.Utils.checkNotNull;

class AStarWeightFunctionSelector<T extends Node<T>, 
                                         W extends Comparable<? super W>> {
    private final AStarTargetSelector<T, W> targetSelector;
    
    private WeightFunction<T, W> weightFunction;
    
    AStarWeightFunctionSelector
        (final AStarTargetSelector<T, W> targetSelector) {
        checkNotNull(targetSelector, "'targetSelector' is null.");
        this.targetSelector = targetSelector;
    }
    
    AStarHeuristicFunctionSelector<T, W> withWeightFunction
            (final WeightFunction<T, W> weightFunction) {
        checkNotNull(weightFunction, "'weightFunction' is null.");
        this.weightFunction = weightFunction;
        return new AStarHeuristicFunctionSelector<>(this);
    }
            
    AStarFinder<T, W> getFinder() {
        return targetSelector.getFinder();
    }
    
    T getSource() {
        return targetSelector.getSource();
    }
    
    T getTarget() {
        return targetSelector.getTarget();
    }
    
    WeightFunction<T, W> getWeightFunction() {
        return weightFunction;
    }
}
