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
    
    @Test(expected = NullPointerException.class) 
    public void nullNameNotAllowedInConstructor() {
        new Graph(null);
    }
    
    @Test
    public void testAdd() {
        g.add(a);
        gg.add(b);
        
        assertTrue(g.containsNode(a));
        assertTrue(gg.containsNode(b));
        
        assertFalse(g.containsNode(b));
        assertFalse(gg.containsNode(a));
        
        g.add(a);
        assertTrue(g.containsNode(a));
        assertEquals(1, g.size());
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
        
        assertEquals(1, g.edges());
        
        g.addEdge(c, a);
        
        assertEquals(1, g.edges());
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
        assertNull(g.getNode("HABABABÄWABA"));
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
    
    @Test
    public void testRemoveNode() {
        g.add(a);
        g.add(b);
        
        assertTrue(g.containsNode(a));
        assertTrue(g.containsNode(b));
        
        assertEquals(0, g.edges());
        assertFalse(g.containsNode(c));
        
        g.addEdge(a, b);
        
        assertEquals(1, g.edges());
        
        g.removeNode(b);
        
        assertEquals(0, g.edges());
        assertFalse(g.containsNode(b));
        assertTrue(g.containsNode(a));
    }
    
    public void testClear() {
        g.add(a);
        g.add(b);
        g.add(c);
        
        g.addEdge(a, b);
        b.connectTo(c);
        
        assertEquals(2, g.edges());
        assertEquals(3, g.size());
        
        g.clear();
        
        assertEquals(0, g.edges());
        assertEquals(0, g.size());
    }
    
    @Test(expected = NullPointerException.class)
    public void testAddEdgeFirstArgNotNull() {
        g.addEdge(null, a);
    }
    
    @Test(expected = NullPointerException.class)
    public void testAddEdgeSecondArgNotNull() {
        g.addEdge(a, null);
    }
    
    @Test(expected = IllegalStateException.class)
    public void firstArgNotInGraph() {
        g.add(c);
        g.addEdge(a, c);
    }
    
    @Test(expected = IllegalStateException.class)
    public void secondArgNotInGraph() {
        g.add(a);
        g.addEdge(a, c);
    }
    
    @Test(expected = IllegalStateException.class)
    public void differentGraphs() {
        g.add(a);
        gg.add(c);
        
        g.addEdge(a, c);
    }
    
    @Test(expected = NullPointerException.class)
    public void testRemoveEdgeFirstArgMayNotBeNull() {
        g.add(a);
        g.removeEdge(null, a);
    }
    
    @Test(expected = NullPointerException.class)
    public void testRemoveEdgeSecondArgMayNotBeNull() {
        g.add(b);
        g.removeEdge(b, null);
    }
    
    @Test(expected = IllegalStateException.class)
    public void testRemoveEdgeFirstArgMustBelongToGraph() {
        g.add(b);
        g.removeEdge(a, b);
    }
    
    @Test(expected = IllegalStateException.class)
    public void testRemoveEdgeSecondArgMustBelongToGraph() {
        g.add(a);
        g.removeEdge(a, b);
    }
    
    @Test(expected = IllegalStateException.class)
    public void testRemoveEdgeNoOutsiderGraphs() {
        g.add(a);
        gg.add(b);
        
        g.addEdge(a, b);
    }
}
