package com.coderodde.apij.graph.path.support;

import com.coderodde.apij.graph.model.Node;
import com.coderodde.apij.graph.model.WeightFunction;
import static com.coderodde.apij.util.Utils.checkNotNull;

public class DijkstraWeightFunctionSelector<T extends Node<T>> {
    
    private final DijkstraTargetSelector<T> targetSelector;
    
    private WeightFunction<T> weightFunction;
    
    DijkstraWeightFunctionSelector
        (final DijkstraTargetSelector<T> targetSelector) {
        checkNotNull(targetSelector, "'targetSelector' is null.");
        this.targetSelector = targetSelector;
    }
    
    public DijkstraProxy<T> withWeightFunction
            (final WeightFunction<T> weightFunction) {
        checkNotNull(weightFunction, "'weightFunction' is null.");
        this.weightFunction = weightFunction;
        return new DijkstraProxy<>(this);
    }
            
    DijkstraFinder<T> getFinder() {
        return targetSelector.getFinder();
    }
    
    T getSource() {
        return targetSelector.getSource();
    }
    
    T getTarget() {
        return targetSelector.getTarget();
    }
    
    WeightFunction<T> getWeightFunction() {
        return weightFunction;
    }
}
