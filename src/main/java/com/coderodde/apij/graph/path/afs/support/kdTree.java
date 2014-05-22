package com.coderodde.apij.graph.path.afs.support;

import com.coderodde.apij.graph.model.Node;
import com.coderodde.apij.graph.path.Layout;
import com.coderodde.apij.graph.path.afs.Partitioner;
import static com.coderodde.apij.util.Utils.checkNotNull;
import static com.coderodde.apij.util.Utils.checkPositive;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class kdTree<T extends Node<T>> implements Partitioner<T> {
    
    private static final boolean X = false;
    private static final boolean Y = true;
    
//    private static Comparator<Point2D.Double> xcmp = new XComparator();
//    
//    private static Comparator<Point2D.Double> ycmp = new YComparator();
    
    private boolean axis = X;
    
    private int maximumNodesPerPartition;
    
    private Layout<T> layout;
    
    private Object[] nodes;
    
    private List<Integer> separatorList;
    
    public kdTree(final int maximumNodesPerPartition,
                  final Layout<T> layout) {
        checkPositive(maximumNodesPerPartition, 
                      "'maximumNodesPerPartition' is less than 1.");
        checkNotNull(layout, "'layout' is null.");
        this.maximumNodesPerPartition = maximumNodesPerPartition;
        this.separatorList = new ArrayList<>();
    }

    @Override
    public List<Set<T>> partition(Set<T> nodes) {
        this.separatorList.clear();
        this.axis = X;
        this.nodes = new Object[nodes.size()];
        List<Set<T>> partition = new ArrayList<>();
        
        int i = 0;
        
        for (final T node : nodes) {
            this.nodes[i++] = node;
        }
        
        
        return partition;
    }
    
    
//    private class XComparator implements Comparator<> {
//        
//        private Layout<T> layout;
//        
////        @Override
////        public int compare(Point2D.Double o1, Point2D.Double o2) {
////            return (o1.x < o2.x ? -1 : (o1.x > o2.x ? 1 : 0));
////        }
//    }
    
    private static class YComparator implements Comparator<Point2D.Double> {
        @Override
        public int compare(Point2D.Double o1, Point2D.Double o2) {
            return (o1.y < o2.y ? - 1 : (o1.y > o2.y ? 1 : 0));
        }
    }
}
