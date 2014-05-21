package com.coderodde.apij.graph.path;

/**
 *
 * @author rodionefremov
 */
public class Point {
    
    private final Double[] coordinates;
    
    public Point(final Double... coordinates) {
        this.coordinates = coordinates;
    }
    
    public Double get(int i) {
        return coordinates[i];
    }
    
    public int size() {
        return coordinates.length;
    }
    
    public Double[] getRef() {
        return coordinates;
    }
}
