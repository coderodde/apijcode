package com.coderodde.apij.sort.support;

import static com.coderodde.apij.util.Utils.arraysSameByRef;
import static com.coderodde.apij.util.Utils.getRandomIntegerArray;
import static com.coderodde.apij.util.Utils.isSorted;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * This class tests BottomUpMergesort.
 */
public class BottomUpMergesortTest {
    
    private Integer[] array;
    private Integer[] array2;
    private Random r = new Random();
    private BottomUpMergesort sort = new BottomUpMergesort();
    private Comparator<Integer> cmp = new Comparator<Integer>(){
        @Override
        public int compare(final Integer i1, final Integer i2) {
            return i1 - i2;
        }
    };
    
    @Before
    public void init() {
        array = getRandomIntegerArray(10000,
                                      -10000,
                                      10000,
                                      r);
        array2 = array.clone();
    }
    
    @Test
    public void testSort1() {
        sort.sort(array, cmp);
        Arrays.sort(array2, cmp);
        
        assertTrue(isSorted(array));
        assertTrue(isSorted(array2));
        assertTrue(arraysSameByRef(array, array2));
    }
    
    public void testSort2() {
        sort.sort(array, cmp, 10, 100);
        Arrays.sort(array, 10, 100, cmp);
        assertFalse(isSorted(array));
        assertFalse(isSorted(array2));
        
        assertTrue(isSorted(array, 10, 100));
        assertTrue(isSorted(array2, 10, 100));
        
        assertTrue(arraysSameByRef(array, array2));
    }
}
