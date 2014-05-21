package com.coderodde.apij.graph.path.afs.support;

import com.coderodde.apij.graph.model.Node;
import com.coderodde.apij.graph.path.Layout;
import static com.coderodde.apij.util.Utils.checkNotNull;
import static com.coderodde.apij.util.Utils.checkPositive;

public class kdTree<T extends Node<T>> {
    
    private int maximumNodesPerPartition;
    
    private Layout<T> layout;
    
    public kdTree(final int maximumNodesPerPartition,
                  final Layout<T> layout) {
        checkPositive(maximumNodesPerPartition, 
                      "'maximumNodesPerPartition' is less than 1.");
        checkNotNull(layout, "'layout' is null.");
        this.maximumNodesPerPartition = maximumNodesPerPartition;
    }
}
