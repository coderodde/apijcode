package com.coderodde.apij.ds.pq.support;

import com.coderodde.apij.ds.pq.PriorityQueue;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * This class implements d-ary heap.
 * 
 * @author Rodion Efremov
 * @version  1.6
 * 
 * @param <T> the element type.
 * @param <P> the priority key type, must be <code>Comparable</code>.
 */
public class DaryHeap<T, P extends Comparable<? super P>> 
implements PriorityQueue<T, P> {

    /**
     * A structure holding the elements and their data.
     * 
     * @param <T> the element type.
     */
    private static class Node<T, P extends Comparable<? super P>> {
        /**
         * The element.
         */
        T element;
        
        /**
         * The index at which this <code>node</code> resides in the storage
         * array.
         */
        int index;
        
        /**
         * The priority of element.
         */
        P priority;
    }
    
    /**
     * The minimum capacity of this heap.
     */
    private static final int MINIMUM_CAPACITY = 128;
    
    /**
     * The default capacity of this heap.
     */
    private static final int DEFAULT_CAPACITY = 1024;
    
    /**
     * The default degree of this heap.
     */
    private static final int DEFAULT_DEGREE = 2;
    
    /**
     * The degree of this heap.
     */
    private final int d;
    
    /**
     * Storage array.
     */
    private Object[] storage;
    
    /**
     * Used as to keep the decrease operation O(log N).
     */
    private Map<T, Node<T, P>> map;
    
    /**
     * Used to avoid creating the arrays every time children indices are 
     * computed.
     */
    private int[] indices;
    
    /**
     * The amount of elements in this heap.
     */
    private int size;
    
    /**
     * Construct a new d-ary heap with given <code>d</code> and
     * <code>capacity</code>.
     * 
     * @param d the degree (branching factor) of this heap.
     * @param capacity the initial capacity.
     */
    public DaryHeap(final int d, final int capacity) {
        checkD(d);
        checkCapacity(capacity);
        this.d = d;
        this.storage = new Object[capacity];
        this.indices = new int[d];
        this.map = new HashMap<T, Node<T, P>>(capacity);
    }
    
    /**
     * Constructs a new d-ary heap with given degree and default capacity.
     * 
     * @param d the degree of newly constructed heap.
     */
    public DaryHeap(final int d) {
        this(d, DEFAULT_CAPACITY);
    }
    
    /**
     * Constructs a heap with default parameters.
     */
    public DaryHeap() {
        this(DEFAULT_DEGREE, DEFAULT_CAPACITY);
    }
    
    /**
     * Adds an element to this queue if it is not already there.
     * 
     * @param element the element to add.
     * @param priority the priority of the element.
     */
    @Override
    public void add(final T element, final P priority) {
        if (map.containsKey(element)) {
            return;
        }
        
        checkAndExpand();
        Node<T, P> node = new Node<T, P>();
        node.element = element;
        node.priority = priority;
        node.index = size;
        storage[size] = node;
        map.put(element, node);
        siftUp(size++);
    }

    /**
     * Returns but does not remove the root element.
     * 
     * @return the root element of this heap.
     * 
     * @throws NoSuchElementException if this heap is empty.
     */
    @Override
    public T min() {
        checkNotEmpty();
        return ((Node<T, P>) storage[0]).element;
    }

    /**
     * Returns and removes the root element.
     * 
     * @return the root element of this heap.
     * 
     * @throws NoSuchElementException if this heap is empty.
     */
    @Override
    public T extractMinimum() {
        checkNotEmpty();
        T ret = ((Node<T, P>) storage[0]).element;
        map.remove(ret);
        Node<T, P> node = (Node<T, P>) storage[--size];
        storage[size] = null; // For garbage collecting.
        
        if (size != 0) {
            storage[0] = node;
            node.index = 0;
            siftDown(0);
        }
        
        return ret;
    }

    /**
     * Updates the priority of an element.
     * 
     * @param element the element whose priority to decrease.
     * @param priority the new priority.
     */
    @Override
    public void decreasePriority(final T element, final P priority) {
        Node<T, P> node = map.get(element);
        
        if (node == null 
                || node.index == 0
                || node.priority.compareTo(priority) <= 0) {
            return;
        }
        
        node.priority = priority;
        siftUp(node.index);
    }

    /**
     * Returns the amount of elements in this heap.
     * 
     * @return the amount of elements in this heap.
     */
    @Override
    public int size() {
        return size;
    }
    
    /**
     * Removes all the elements from this heap.
     */
    @Override
    public void clear() {
        map.clear();
        
        for (int i = 0; i != size; ++i) {
            storage[i] = null;
        }
        
        size = 0;
    }
    
    /**
     * Returns the branching factor (degree) of this heap.
     * 
     * @return the degree of this heap.
     */
    public int getDegree() {
        return d;
    }
    
    /**
     * Spawns a new empty heap with the same implementation.
     * 
     * @return a new empty heap.
     */
    @Override
    public final DaryHeap<T, P> spawn() {
        return new DaryHeap<>(getDegree(), storage.length);
    }
    
    @Override
    public P getPriorityOf(T element) {
        if (map.containsKey(element) == false) {
            throw new NoSuchElementException(
                    "No element '" + element.toString() + "' in this heap.");
        }
        
        return map.get(element).priority;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * This method expands the storage array in case it is full.
     */
    private final void checkAndExpand() {
        if (size == storage.length) {
            Object[] arr = new Object[3 * size / 2];
            System.arraycopy(storage, 0, arr, 0, size);
            storage = arr;
        }
    }
    
    /**
     * Checks the sanity of degree <code>d</code>
     * 
     * @param d the degree to check.
     * 
     * @throws IllegalArgumentException if degree is invalid.
     */
    private final void checkD(final int d) {
        if (d < 2) {
            throw new IllegalArgumentException("Degree is less than 2.");
        }
    }
    
    /**
     * Checks the sanity of capacity.
     * 
     * @param capacity capacity to check.
     * 
     * @throws IllegalArgumentException if capacity if invalid.
     */
    private final void checkCapacity(final int capacity) {
        if (capacity < MINIMUM_CAPACITY) {
            throw new IllegalArgumentException(
                    "Capacity (" + capacity + ") is below the minimum (" +
                    MINIMUM_CAPACITY + ").");
        }
    }
    
    /**
     * Checks whether this heap is not empty.
     * 
     * @throws NoSuchElementException if this heap is empty.
     */
    private final void checkNotEmpty() {
        if (size == 0) {
            throw new NoSuchElementException("Reading from an empty heap.");
        }
    }
    
    /**
     * Computes the children indices of <code>index</code> and stores them in
     * <code>indices</code>.
     * 
     * @param index the index whose children indices to compute.
     */
    private void computeChildrenIndices(final int index) {
        for (int i = 0; i != d; ++i) {
            indices[i] = d * index + i + 1;
            
            if (indices[i] >= size) {
                indices[i] = -1;
                break;
            }
        }
    }
    
    /**
     * Computes and returns the index of the parent of the element at index
     * <code>index</code>.
     * 
     * @param index the index whose parent index to compute.
     * 
     * @return the parent index. 
     */
    private int getParentIndex(final int index) {
        return (index - 1) / d;
    }
    
    /**
     * Sifts a node up the heap until the minimum heap invariant satisfied.
     * 
     * @param index the index of a node to sift up. 
     */
    private void siftUp(int index) {
        if (index == 0) {
            return;
        }
        
        int parentIndex = getParentIndex(index);
        Node<T, P> target = (Node<T, P>) storage[index];
        
        for (;;) {
            Node<T, P> parent = (Node<T, P>) storage[parentIndex];
            if (parent.priority.compareTo(target.priority) > 0) {
                storage[index] = parent;
                parent.index = index;
                
                index = parentIndex;
                parentIndex = getParentIndex(index);
            } else {
                break;
            }
            
            if (index == 0) {
                break;
            }
        }
        
        storage[index] = target;
        target.index = index;
    }
    
    /**
     * Sifts a node down the heap until minimum heap invariant is satisfied.
     * 
     * @param index the index of the node to sift down.
     */
    private void siftDown(int index) {
        final Node<T, P> target = (Node<T, P>) storage[index];
        final P PRIORITY = target.priority;
        
        for (;;) {
            P minChildPriority = PRIORITY;
            int minChildIndex = -1;
            computeChildrenIndices(index);

            for (int i : indices) {
                if (i == -1) {
                    break;
                }

                P tentative = ((Node<T, P>) storage[i]).priority;

                if (minChildPriority.compareTo(tentative) > 0) {
                    minChildPriority = tentative;
                    minChildIndex = i;
                }
            }

            if (minChildIndex == -1) {
                storage[index] = target;
                target.index = index;
                return;
            }
            
            storage[index] = storage[minChildIndex];
            ((Node<T, P>) storage[index]).index = index;
            
            // Go for the next iteration.
            index = minChildIndex;
        }
    }
}
