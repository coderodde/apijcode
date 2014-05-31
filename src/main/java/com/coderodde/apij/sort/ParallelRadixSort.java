package com.coderodde.apij.sort;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class ParallelRadixSort {

    public static final class Entry {
        public final long key;
        public final Object o;
        
        public Entry(final long key, final Object o) {
            this.key = key;
            this.o = o;
        }
    }
    
    private static final int MOST_SIGNIFICANT_BYTE_INDEX = 7;
    private static final int RIGHT_SHIFT_AMOUNT = 56;
    private static final int LONG_BYTES = 8;
    private static final int BUCKETS = 256;
    private static final int QUICKSORT_THRESHOLD = 256;
    private static final int INSERTIONSORT_THRESHOLD = 8;
    
    public static final void sort(final Entry[] array) {
        final Entry[] clone = array.clone();
        sortImpl(clone, 
                 array, 
                 MOST_SIGNIFICANT_BYTE_INDEX, 
                 0, 
                 array.length - 1);
    }
    
    private static final void quicksort(final Entry[] array,
                                        final int from,
                                        final int to) {
        if (to - from < INSERTIONSORT_THRESHOLD) {
            for (int i = from + 1; i <= to; ++i) {
                int j = i - 1;
                
                while (j >= from && array[j].key > array[j + 1].key) {
                    Entry e = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = e;
                    --j;
                }
            }
            
            return;
        }
        
        final int index = (from + to) >>> 1;
        final long pivot = median(array[from].key,
                                  array[index].key,
                                  array[to].key);
        
        int i = from;
        int j = to;
        
        while (i <= j) {
            while (array[i].key < pivot) ++i;
            while (array[j].key > pivot) --j;
            
            if (i <= j) {
                Entry e = array[i];
                array[i] = array[j];
                array[j] = e;
                ++i;
                --j;
            } else {
                break;
            }
        }
        
        quicksort(array, from, i - 1);
        quicksort(array, i, to);
    }
    
    private static long median( long a, long b, long c ){
        if( a <= b )
        {
            if( c <= a )
                return a;
            else if( c <= b )
                return c;
            else
                return b;
        }
        else
        {
            if( c <= b )
                return b;
            else if( a <= c )
                return a;
            else
                return c;
        }
    }
    
    private static final void sortImpl(final Entry[] source,
                                       final Entry[] target,
                                       final int byteIndex,
                                       final int from,
                                       final int to) {
        if (to - from < QUICKSORT_THRESHOLD) {
            quicksort(source, from, to);
            
            // By a target array we mean the array in which the sorted
            // data must rely.
            
            if ((byteIndex & 1) == 0) {
                // 'source' points to actual target array.
                // 'target' points to a clone array.
                
            } else {
                // 'target' points to actual target array.
                // 'source' points to a clone array.
                System.arraycopy(source, from, target, from, to - from + 1);
            }
           
            return;
        }
        
        final int[] bucketSizeMap = new int[BUCKETS];
        final int LEFT_SHIFT_AMOUNT =
                LONG_BYTES * (MOST_SIGNIFICANT_BYTE_INDEX - byteIndex);
        
        for (int i = from; i <= to; ++i) {
            bucketSizeMap[(int)((source[i].key << LEFT_SHIFT_AMOUNT) 
                                              >>> RIGHT_SHIFT_AMOUNT)]++;
        }
        
        final int[] startIndexMap = new int[BUCKETS];
        final int[] processedMap =  new int[BUCKETS];
        
        startIndexMap[0] = from;
        
        for (int i = 1; i != BUCKETS; ++i) {
            startIndexMap[i] = startIndexMap[i - 1] + bucketSizeMap[i - 1];
        }
        
        for (int i = from; i <= to; ++i) {
            final Entry current = source[i];
            final long key = current.key;
            final int index = (int)((key << LEFT_SHIFT_AMOUNT) 
                                       >>> RIGHT_SHIFT_AMOUNT);
            target[startIndexMap[index] + processedMap[index]++] = current;
        }
        
        if (byteIndex != 0) {
            for (int i = 0; i != BUCKETS - 1; ++i) {
                if (bucketSizeMap[i] != 0) {
                    sortImpl(target, 
                             source, 
                             byteIndex - 1, 
                             startIndexMap[i],
                             startIndexMap[i] + bucketSizeMap[i] - 1);
                }
            }
            
            if (bucketSizeMap[BUCKETS - 1] != 0) {
                sortImpl(target,
                         source,
                         byteIndex - 1,
                         startIndexMap[BUCKETS - 1],
                         to);
            }
        }
    }
    
    public static final void main(final String... args) {
        final long SEED = 1401530452981L;//System.currentTimeMillis();
        final Random r = new Random(SEED);
        final Entry[] array1 = getRandomArray(1000000, r);
        final Entry[] array2 = array1.clone();
        
        System.out.println("Seed: " + SEED);
        
        long ta = System.currentTimeMillis();

        Arrays.sort(array1, new EntryComparator());
        
        long tb = System.currentTimeMillis();
        
        System.out.print("Arrays.sort() in " + (tb - ta) + " ms. ");
        System.out.println("Sorted: " + isSorted(array1));
        
        ta = System.currentTimeMillis();

        sort(array2);
        
        tb = System.currentTimeMillis();
        
        System.out.print("Parallel radix sort in " + (tb - ta) + " ms. ");
        System.out.println("Sorted: " + isSorted(array2));
    }
    
    private static final Entry[] getRandomArray(final int size, 
                                                final Random r) {
        final Entry[] array = new Entry[size];
        
        for (int i = 0; i != size; ++i) {
            Entry e = new Entry(Math.abs(r.nextLong()), new Object());
            array[i] = e;
        }
        
        return array;
    }
    
    private static final class EntryComparator implements Comparator<Entry> {

        @Override
        public int compare(Entry o1, Entry o2) {
            final long l = o1.key - o2.key;
            return (l < 0 ? -1 : (l > 0 ? 1 : 0));
        }
    }
    
    private static final boolean isSorted(final Entry[] array) {
        for (int i = 0; i < array.length - 1; ++i) {
            if (array[i].key > array[i + 1].key) {
                return false;
            }
        }
        
        return true;
    }
}
