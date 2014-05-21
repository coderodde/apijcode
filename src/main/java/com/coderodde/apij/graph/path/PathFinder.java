package com.coderodde.apij.graph.path;

import com.coderodde.apij.graph.model.Node;
import java.util.Map;

public abstract class PathFinder<T extends Node<T>> {
  
    public static final SearchData from(final Object source) {
        return new SearchData(SearchDataType.SOURCE, source);
    }
    
    public static final SearchData to(final Object target) {
        return new SearchData(SearchDataType.TARGET, target);
    }
    
    public static final SearchData 
        withWeightFunction(final Object weightFunction) {
        return new SearchData(SearchDataType.WEIGHT_FUNCTION, weightFunction);
    }
    
    public static final SearchData 
        withHeuristicFunction(final Object heuristicFunction) {
        return new SearchData(SearchDataType.HEURISTIC_FUNCTION,
                              heuristicFunction);
    }
        
    public static final SearchData
        withBackwardHeuristicFunction(final Object backwardHeuristicFunction) {
        return new SearchData(SearchDataType.HEURISTIC_FUNCTION,
                              backwardHeuristicFunction);
    }
    
    public abstract Path<T> search(final SearchData... data);
    
    protected static <T extends Node<T>> Path<T> constructPath
        (final T target, final Map<T, T> parentMap) {
        Path<T> path = new Path<>();
        T current = target;
        
        while (current != null) {
            path.prependNode(current);
            current = parentMap.get(current);
        }
        
        return path;
    }
     
    protected static <T extends Node<T>> Path<T> 
        constructPathBidirectional(final T touch, 
                                   final Map<T, T> parentMapForward,
                                   final Map<T, T> parentMapBackwards) {
        return null;    
    }
}
