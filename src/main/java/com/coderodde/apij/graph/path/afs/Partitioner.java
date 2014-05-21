package com.coderodde.apij.graph.path.afs;

import com.coderodde.apij.graph.model.Node;
import java.util.List;
import java.util.Set;

public interface Partitioner<T extends Node<T>> {
    
    public List<Set<T>> partition(Set<T> nodes);
    
}
