package com.coderodde.apij.graph.model.support;

import com.coderodde.apij.graph.model.Graph;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * This class tests <code>UndirectedGraphNode</code>.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class UndirectedGraphNodeTest {

    private UndirectedGraphNode a;
    private UndirectedGraphNode b;
    private UndirectedGraphNode c;
    private UndirectedGraphNode d;
    private UndirectedGraphNode e;
    private Graph<UndirectedGraphNode> g;
    
    @Before
    public void init() {
        a = new UndirectedGraphNode("A");
        b = new UndirectedGraphNode("B");
        c = new UndirectedGraphNode("C");
        d = new UndirectedGraphNode("D");
        e = new UndirectedGraphNode("E");
        
        g = new Graph("Graph");
        
        g.add(a);
        g.add(b);
        g.add(c);
        g.add(d);
        g.add(e);
    }
    
    @Test
    public void testGetName() {
        assertEquals("A", a.getName());
        assertEquals("B", b.getName());
    }

    @Test
    public void testParentIterable() {
        a.connectTo(b);
        b.connectTo(c);
        
        assertEquals(2, g.edges());
        
        c.connectTo(a);
        
        assertEquals(3, g.edges());
        
        c.connectTo(b);
        
        assertEquals(3, g.edges());
    }

    @Test
    public void testIterator() {
        Set<UndirectedGraphNode> set = new HashSet<>();
        
        for (final UndirectedGraphNode u : g) {
            set.add(u);
        }
        
        assertEquals(5, set.size());
        
        for (final UndirectedGraphNode u : g) {
            assertTrue(g.containsNode(u));
        }
    }

    @Test
    public void testConnectTo() {
        a.connectTo(b);
        
        assertTrue(a.isConnectedTo(b));
        assertTrue(b.isConnectedTo(a));
        
        assertFalse(a.isConnectedTo(c));
        assertFalse(c.isConnectedTo(a));
    }

    @Test
    public void testDisconnect() {
        a.connectTo(d);
        assertTrue(a.isConnectedTo(d));
        assertTrue(d.isConnectedTo(a));
        assertEquals(1, g.edges());
        assertEquals(5, g.size());
        
        a.disconnect(d);
        assertFalse(a.isConnectedTo(d));
        assertFalse(d.isConnectedTo(a));
        assertEquals(0, g.edges());
        assertEquals(5, g.size());
    }

    @Test
    public void testIsConnectedTo() {
        assertFalse(c.isConnectedTo(d));
        assertFalse(d.isConnectedTo(c));
        c.connectTo(d);
        assertTrue(c.isConnectedTo(d));
        assertTrue(d.isConnectedTo(c));
        assertFalse(c.isConnectedTo(a));
        assertFalse(a.isConnectedTo(c));
    }

    @Test
    public void testGetOwnerGraph() {
        assertEquals(a.getOwnerGraph(), g);
        assertEquals(b.getOwnerGraph(), g);
        assertNull(new UndirectedGraphNode("X").getOwnerGraph());
    }

    @Test
    public void testSetOwnerGraph() {
        assertEquals(d.getOwnerGraph(), g);
        d.setOwnerGraph(null);
        assertNull(d.getOwnerGraph());
        Graph gg = new Graph("?");
        d.setOwnerGraph(gg);
        assertEquals(d.getOwnerGraph(), gg);
    }

    @Test
    public void testClear() {
        a.connectTo(b);
        a.connectTo(c);
        a.connectTo(d);
        c.connectTo(d);
        assertEquals(4, g.edges());
        
        a.clear();
        
        assertEquals(1, g.edges());
        
        c.clear();
        
        assertEquals(0, g.edges());
    }
    
}
