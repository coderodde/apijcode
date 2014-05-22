package com.coderodde.apij.sort.support;

import com.coderodde.apij.sort.Sort;
import com.coderodde.apij.util.Utils;
import static com.coderodde.apij.util.Utils.isSorted;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class BottomUpMergesort implements Sort {

    @Override
    public void sort(Object[] array, Comparator cmp) {
        sort(array, cmp, 0, array.length - 1);
    }

    @Override
    public void sort(final Object[] array,
                     final Comparator cmp, 
                     final int from, 
                     final int to) {
        final int N = to - from + 1;
        
        if (N < 2) {
            return;
        }
        
        final Object[] buffer = new Object[array.length];
        final int PASSES = (int) Math.ceil((Math.log(N) / Math.log(2)));
        
        boolean originalArrayIsSource = (PASSES % 2 == 0);
        
        Object[] destination;
        Object[] source;
        
        if (originalArrayIsSource) {
            source = array;
            destination = buffer;
        } else {
            System.arraycopy(array, from, buffer, 0, N);
            source = buffer;
            destination = array;
        }
        
        for (int width = 1; width < N; width <<= 1) {
            int chunkIndex = 0;
            
            for (; chunkIndex < N / width; chunkIndex += 2) {
                if ((chunkIndex + 1) * width > N) {
                    // chunkIndex + 1 is the index of the rightmost 
                    // "orphan chunk".
                    break;
                }
                
                int left = width * chunkIndex + from;
                int right = left + width;
                int i = left;

                int leftUpperBound = right;
                int rightUpperBound = right + width;

                if (rightUpperBound > N) {
                    rightUpperBound = N;
                }

                while (left < leftUpperBound && right < rightUpperBound) {
                    destination[i++] =
                            cmp.compare(source[left], source[right]) <= 0 ?
                                        source[left++] :
                                        source[right++];
                }

                while (left < leftUpperBound) {
                    destination[i++] = source[left++];
                }                    

                while (right < rightUpperBound) {
                    destination[i++] = source[right++];
                }
            }
            
            int i = chunkIndex * width;
            
            // Handle the orphan chunk.
            for (; i < from + N; ++i) {
                destination[i] = source[i];
            }
            
            originalArrayIsSource = !originalArrayIsSource;
            Object[] tmp = source;
            source = destination;
            destination = tmp;
        }
    }
    
    public static void main(String... args) {
        Sort sort = new BottomUpMergesort();
        Integer[] array = Utils.getRandomIntegerArray(1000000, 0, 5000000, new Random());
        Integer[] array2 = array.clone();
        
        Comparator<Integer> cmp = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        };
        
        long ta2 = System.currentTimeMillis();
        
        Arrays.sort(array2, cmp);
        
        long tb2 = System.currentTimeMillis();
        
        System.out.println("Arrays.sort in " + (tb2 - ta2) + " ms. " +
                           "Sorted: " + isSorted(array2, cmp));
        
        long ta = System.currentTimeMillis();
        
        sort.sort(array, cmp);
        
        long tb = System.currentTimeMillis();
        
        System.out.println("BottomUpMergesort in " + (tb - ta) + " ms. " +
                           "Sorted: " + isSorted(array, cmp));
    }
}
