package com.coderodde.apij.graph.path.afs.support;

import com.coderodde.apij.graph.model.Node;
import com.coderodde.apij.graph.path.Layout;
import com.coderodde.apij.graph.path.afs.Partitioner;
import static com.coderodde.apij.util.Utils.checkNotBelow;
import static com.coderodde.apij.util.Utils.checkNotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class kdTree<T extends Node<T>> implements Partitioner<T> {
    
    private static final boolean X = false;
    private static final boolean Y = true;
    private static final int MIN_PARTITION_SIZE = 10;
    
    private boolean axis = X;
    
    private int maximumNodesPerPartition;
    
    private Layout<T> layout;
    
    private Object[] nodes;
    
    private List<Integer> separatorList;
    
    private Comparator<T> xcmp;
    
    private Comparator<T> ycmp;
    
    public kdTree(final int maximumNodesPerPartition,
                  final Layout<T> layout) {
        checkNotBelow(maximumNodesPerPartition,
                      MIN_PARTITION_SIZE,
                      "'maximumNodesPerPartition' is less than 1.");
        checkNotNull(layout, "'layout' is null.");
        this.maximumNodesPerPartition = maximumNodesPerPartition;
        this.separatorList = new ArrayList<>();
        this.xcmp = new XComparator<>(layout);
        this.ycmp = new YComparator<T>(layout);
    }

    @Override
    public List<Set<T>> partition(Set<T> nodeSet) {
        if (nodeSet.isEmpty()) {
            return Collections.<Set<T>>emptyList();
        }
        
        this.separatorList.clear();
        this.axis = X;
        this.nodes = new Node[nodeSet.size()];
        List<Set<T>> partition = new ArrayList<>();
        
        int i = 0;
        
        for (final T node : nodeSet) {
            this.nodes[i++] = node;
        }
        
        Deque<Range> stack = new LinkedList<Range>();
        
        stack.add(new Range(0, nodes.length - 1, X));
        
        List<Range> resultRanges = new ArrayList<>();
        
        while (stack.isEmpty() == false) {
            Range r = stack.removeFirst();
            
            if (r.length() > maximumNodesPerPartition) {
//                Arrays.sort(nodes, (r.axis == X ? xcmp : ycmp));
                Range[] children = r.split();
                stack.addLast(children[0]);
                stack.addLast(children[1]);
            } else {
                resultRanges.add(r);
            }
        }
        
        return partition;
    }
    
    private void sort(Range r, boolean axis) {
        
    }
    
    private class XComparator<T extends Node<T>> implements Comparator<T> {
        
        private Layout<T> layout;
        
        XComparator(final Layout<T> layout) {
            this.layout = layout;
        }

        @Override
        public int compare(final T o1, final T o2) {
            final Double x1 = layout.get(o1).x;
            final Double x2 = layout.get(o2).x;
            
            return (x1 < x2 ? -1 : (x1 > x2 ? 1 : 0));
        }
    }
    
    private class YComparator<T extends Node<T>> implements Comparator<T> {
        
        private Layout<T> layout;
        
        YComparator(final Layout<T> layout) {
            this.layout = layout;
        }

        @Override
        public int compare(final T o1, final T o2) {
            final Double y1 = layout.get(o1).y;
            final Double y2 = layout.get(o2).y;
            return (y1 < y2 ? -1 : (y1 > y2 ? 1 : 0));
        }
    }
       
    static class Range {
        int from;
        int to;
        boolean axis;

        Range(final int from, final int to, final boolean axis) {
            this.from = from;
            this.to = to;
            this.axis = axis;
        }

        int length() {
            return to - from + 1;
        }

        Range[] split() {
            final boolean newAxis = !axis;
            final int s = (to + from) >>> 1;
            return new Range[]{ new Range(from, s, newAxis),
                                new Range(s + 1, to, newAxis)};
        }
    }
}
