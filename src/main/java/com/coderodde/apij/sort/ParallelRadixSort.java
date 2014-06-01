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
    private static final int MERGESORT_THRESHOLD = 256;
    private static final int INSERTIONSORT_THRESHOLD = 8;
    
    public static final void sort(final Entry[] array) {
        final Entry[] clone = array.clone();
        sortImpl(array, 
                 clone, 
                 MOST_SIGNIFICANT_BYTE_INDEX, 
                 0, 
                 array.length - 1);
    }
    
    private static final void sortImpl(final Entry[] source,
                                       final Entry[] target,
                                       final int byteIndex,
                                       final int from,
                                       final int to) {
        if (to - from < MERGESORT_THRESHOLD) {
            boolean evenPasses = mergesort(source, target, from, to);
            
            if (evenPasses) {
                // Here the sorted data is in 'source'.
                if ((byteIndex & 1) == 0) {
                    System.arraycopy(source, from, target, from, to - from + 1);
                }
            } else {
                // Here the sorted data is in 'target'
                if ((byteIndex & 1) == 1) {
                    System.arraycopy(source, from, target, from, to - from + 1);
                }
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
    
    /**
     * Sorts the range <tt>[from, to]</tt> using merge sort.
     * 
     * @param source the source array.
     * @param target the target array.
     * @param from the least index of the range to be sorted.
     * @param to the greatest index of the range to be sorted.
     * @return <code>true</code> if there was an even number of passes (i.e.,
     * the sorted data ended up in <code>source</code>), and <code>false</code>
     * otherwise (the sorted data ended up in <code>target</code>).
     */
    private static final boolean mergesort(Entry[] source,
                                           Entry[] target,
                                           final int from,
                                           final int to) {
        final int RANGE_SIZE = to - from + 1;
        final int BLOCKS = (int) Math.ceil(RANGE_SIZE / 
                                           INSERTIONSORT_THRESHOLD);
        final int PASS_AMOUNT = (int)(Math.ceil(Math.log(BLOCKS) / 
                                                Math.log(2)));
        
        // Sort to blocks.
        for (int blockId = 0; blockId < BLOCKS; ++blockId) {
            final int i = from + blockId * INSERTIONSORT_THRESHOLD;
            final int iBound = Math.min(to + 1, i + INSERTIONSORT_THRESHOLD);
            
            // Do the insertion sort.
            for (int j = i + 1; j < iBound; ++j) {
                for (int k = j - 1; 
                         k >= i && source[k].key > source[k + 1].key; 
                         k--) {
                    Entry tmp = source[k];
                    source[k] = source[k + 1];
                    source[k + 1] = tmp;
                }
            }
        }
        
        // Every iteration of the following loop performs a "merge pass" over
        // the arrays. "width <<= 1" essentially means "width *= 2".
        for (int width = INSERTIONSORT_THRESHOLD; 
                 width < RANGE_SIZE; 
                 width <<= 1) {
            int blockIndex = 0;
            
            for (; blockIndex < RANGE_SIZE / width; blockIndex += 2) {
                if ((blockIndex + 1) * width > RANGE_SIZE) {
                    // blockIndex + 1 is the index of the rightmost 
                    // "orphan block" 
                    break;
                }
                
                int l = width * blockIndex + from;
                int r = l + width;
                int i = l; 
                
                final int leftUpperBound = r;
                final int rightUpperBound = Math.min(r + width, to + 1);
                
                while (l < leftUpperBound && r < rightUpperBound) {
                    target[i++] = 
                            source[r].key < source[l].key ?
                            source[r++] :
                            source[l++];
                }
                
                while (l < leftUpperBound) {
                    target[i++] = source[l++];
                }
                
                while (r < leftUpperBound) {
                    target[i++] = source[r++];
                }
            }
            
            int i = blockIndex * width;
            
            // Handle the orphan block.
            while (i <= to) {
                target[i++] = source[i++];
            }
            
            Entry[] tmp = source;
            source = target;
            target = tmp;
        }
        
        return (PASS_AMOUNT & 1) == 0;
    }
    
    public static final void main(final String... args) {
        final long SEED = System.currentTimeMillis();
        final Random r = new Random(SEED);
        final Entry[] array1 = getRandomArray(5000000, r);
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
        
        System.out.println("Arrays identical: " + strongEquals(array1, array2));
    }
    
    private static final Entry[] getRandomArray(final int size, 
                                                final Random r) {
        final Entry[] array = new Entry[size];
        
        for (int i = 0; i != size; ++i) {
            Entry e = new Entry(Math.abs(r.nextLong() % 100L), new Object());
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
    
    private static final boolean strongEquals(final Entry[] array1,
                                              final Entry[] array2) {
        final int size = Math.max(array1.length, array2.length);
        
        for (int i = 0; i < size; ++i) {
            if (array1[i] != array2[i]) {
                return false;
            }
        }
        
        return true;
    }
}
