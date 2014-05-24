package com.coderodde.apij.graph.path.afs;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class ArcFlagVectorTest {

    private static final int N = 10;
    
    private ArcFlagVector v;
    
    @Before
    public void init() {
        v = new ArcFlagVector(N);
    }
    
    @Test
    public void test() {
        for (int i = 0; i < N; ++i) {
            assertFalse(v.get(i));
        }
        
        v.set(0);
        
        assertTrue(v.get(0));
        
        v.set(4);
        
        assertTrue(v.get(4));
    }
}
