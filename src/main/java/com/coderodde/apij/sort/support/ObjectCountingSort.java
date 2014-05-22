package com.coderodde.apij.sort.support;

import com.coderodde.apij.sort.Sort;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * This sorting algorithm runs in <tt>O(n + k log k)</tt> time whenever the 
 * input consists of <tt>k</tt> <strong>distinct</strong> objects and the array
 * has size <tt>n</tt>.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class ObjectCountingSort implements Sort {

    @Override
    public void sort(Object[] array, Comparator cmp) {
        sort(array, cmp, 0, array.length - 1);
    }

    @Override
    public void sort(Object[] array, Comparator cmp, int from, int to) {
        final int N = to - from + 1;
        
        if (N < 2) {
            return;
        }
        
        final Set<Object> set = new HashSet<>(N);
        final Map<Object, List<Object>> map = new HashMap<>(N);
        
        for (int i = from; i <= to; ++i) {
            final Object element = array[i];
            set.add(element);
            
            if (map.containsKey(element) == false) {
                List<Object> list = new ArrayList<>();
                list.add(element);
                map.put(element, list);
            } else {
                map.get(element).add(element);
            }
        }
        
        final Object[] condensator = set.toArray();
        Arrays.sort(condensator);
        
        int index = 0;
        
        for (final Object key : condensator) {
            for (final Object element : map.get(key)) {
                array[index++] = element;
            }
        }
    }
    
    static class Person implements Comparable<Person> {
        String name;
        int age;

        @Override
        public int compareTo(Person o) {
            return this.age - o.age;
        }
        
        public int hashCode() {
            return age;
        }
        
        public boolean equals(Object o) {
            return ((Person) o).age == this.age;
        }
    }
    
    static Person[] array(int size) {
        Random r = new Random();
        Person[] arr = new Person[size];
        for (int i = 0; i < size; ++i) {
            Person tmp = new Person();
            tmp.age = r.nextInt(70);
            arr[i] = tmp;
        }
        
        return arr;
    }
}
