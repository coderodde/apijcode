package com.coderodde.apij.graph.model;

import com.coderodde.apij.graph.model.support.UndirectedGraphNode;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * This class tests <code>Graph</code>.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class GraphTest {
    
    private Graph<UndirectedGraphNode> g;
    private Graph<UndirectedGraphNode> gg;
    private UndirectedGraphNode a;
    private UndirectedGraphNode b;
    private UndirectedGraphNode c;
    
    @Before
    public void init() {
        g = new Graph<>("A");
        gg = new Graph<>("B");
        a = new UndirectedGraphNode("1");
        b = new UndirectedGraphNode("2");
        c = new UndirectedGraphNode("3");
    }
    
    @Test
    public void testAdd() {
        g.add(a);
        gg.add(b);
        
        assertTrue(g.containsNode(a));
        assertTrue(gg.containsNode(b));
        
        assertFalse(g.containsNode(b));
        assertFalse(gg.containsNode(a));
    }

    @Test
    public void testAddEdge() {
        g.add(a);
        g.add(c);
        
        assertFalse(a.isConnectedTo(c));
        assertFalse(c.isConnectedTo(a));
        
        g.addEdge(a, c);
        
        assertTrue(a.isConnectedTo(c));
        assertTrue(c.isConnectedTo(a));
    }

    @Test
    public void testRemoveEdge() {
        gg.add(b);
        gg.add(a);
        
        assertFalse(a.isConnectedTo(b));
        assertFalse(b.isConnectedTo(a));
        
        a.connectTo(b);
        
        assertTrue(a.isConnectedTo(b));
        assertTrue(b.isConnectedTo(a));
        
        gg.removeEdge(a, b);
        
        assertFalse(a.isConnectedTo(b));
        assertFalse(b.isConnectedTo(a));
    }

    @Test
    public void testGetNode() {
        g.add(b);
        
        assertEquals(g.getNode("2"), b);
    }

    @Test
    public void testContainsNode() {
        g.add(a);
        
        assertTrue(g.containsNode(a));
        assertFalse(g.containsNode(b));
    }

    @Test
    public void testSize() {
        assertEquals(0, g.size());
        
        g.add(b);
        
        assertEquals(1, g.size());
        
        g.add(c);
        
        assertEquals(2, g.size());
        
        g.removeNode(b);
        
        assertEquals(1, g.size());
        
        g.removeNode(c);
        
        assertEquals(0, g.size());
    }

    @Test
    public void testEdges() {
        g.add(a);
        g.add(b);
        g.add(c);
        
        a.connectTo(b);
        b.connectTo(c);
        
        assertEquals(2, g.edges());
        
        a.connectTo(c);
        
        assertEquals(3, g.edges());
    }

    @Test
    public void testGetName() {
        assertEquals("A", g.getName());
        assertEquals("B", gg.getName());
    }

    @Test
    public void testIterator() {
        g.add(a);
        g.add(c);
        
        Iterator<UndirectedGraphNode> iterator = g.iterator();
        Set<UndirectedGraphNode> set = new HashSet<>(2);
        
        while (iterator.hasNext()) {
            set.add(iterator.next());
        }
        
        assertEquals(2, set.size());
        assertTrue(set.contains(a));
        assertTrue(set.contains(c));
        assertFalse(set.contains(b));
    }
}
