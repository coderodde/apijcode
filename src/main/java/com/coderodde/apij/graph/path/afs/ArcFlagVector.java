package com.coderodde.apij.graph.path.afs;

import java.util.BitSet;

/**
 * This class implements the arc-flag vector.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
class ArcFlagVector {
    
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
        this.flagVector = new BitSet(regionAmount);
    }
    
    void set(final int index) {
        flagVector.set(index);
    }
    
    boolean get(final int index) {
        return flagVector.get(index);
    }
}
