package com.coderodde.apij.graph.model.support.weights;

import com.coderodde.apij.graph.model.Weight;

/**
 * This class implements the integer weight.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class IntegerWeight implements Weight<Integer> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer identity() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer apply(Integer t1, Integer t2) {
        return t1 + t2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer inverse(Integer t) {
        return -t;
    }
}