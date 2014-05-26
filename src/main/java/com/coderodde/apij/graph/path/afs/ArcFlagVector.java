package com.coderodde.apij.graph.path.afs;

import java.util.BitSet;

/**
 * This class implements the arc-flag vector.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
class ArcFlagVector {
    
    private int regionAmount;
    
    /**
     * The actual storage.
     */
    private BitSet flagVector;
    
    /**
     * Constructs a new arc-flag vector.
     * 
     * @param regionAmount the amount of regions in total.
     */
    ArcFlagVector(final int regionAmount) {
        this.regionAmount = regionAmount;
        this.flagVector = new BitSet(regionAmount);
    }
    
    void set(final int index) {
        checkIndex(index);
        flagVector.set(index);
    }
    
    boolean get(final int index) {
        checkIndex(index);
        return flagVector.get(index);
    }
    
    private void checkIndex(final int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException(
                    "'index' is negative: " + index);
        }
        
        if (index >= regionAmount) {
            throw new IndexOutOfBoundsException(
                    "'index' is too large: " + index + ", region amount: " +
                    regionAmount);
        }
    }
}
