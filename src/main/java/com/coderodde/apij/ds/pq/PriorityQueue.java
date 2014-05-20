package com.coderodde.apij.ds.pq;

public interface PriorityQueue<T, P extends Comparable<? super P>> {
    
    public void add(final T element, final P priority);
    
    public void decreasePriority(final T element, final P newPriority);
    
    public T min();
    
    public P getPriorityOf(final T element);
    
    public T extractMinimum();
    
    public void clear();
    
    public int size();
    
    public boolean isEmpty();
    
    public PriorityQueue<T, P> spawn();
}
