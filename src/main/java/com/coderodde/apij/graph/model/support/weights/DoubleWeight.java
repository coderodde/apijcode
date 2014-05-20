package com.coderodde.apij.graph.model.support.weights;

import com.coderodde.apij.graph.model.Weight;

/**
 * This class implements the integer weight.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class DoubleWeight implements Weight<Double> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Double identity() {
        return 0.0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double apply(Double t1, Double t2) {
        return t1 + t2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double inverse(Double t) {
        return -t;
    }
}