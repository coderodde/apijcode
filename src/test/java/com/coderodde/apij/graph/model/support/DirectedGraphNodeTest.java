package com.coderodde.apij.graph.model.support;

import com.coderodde.apij.graph.model.Graph;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * This class tests <code>DirectedGraphNode</code>.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class DirectedGraphNodeTest {

    private DirectedGraphNode a;
    private DirectedGraphNode b;
    private DirectedGraphNode c;
    private DirectedGraphNode d;
    private DirectedGraphNode e;
    private DirectedGraphNode x;
    private Graph<DirectedGraphNode> g;
    
    @Before
    public void init() {
        a = new DirectedGraphNode("A");
        b = new DirectedGraphNode("B");
        c = new DirectedGraphNode("C");
        d = new DirectedGraphNode("D");
        e = new DirectedGraphNode("E");
        x = new DirectedGraphNode("X");
        
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
        b.connectTo(a);
        c.connectTo(a);
        d.connectTo(a);
        
        assertEquals(3, g.edges());
        
        Set<DirectedGraphNode> set = new HashSet<>();
        
        for (final DirectedGraphNode u : a.parentIterable()) {
            set.add(u);
        }
        
        assertEquals(3, set.size());
        assertTrue(set.contains(b));
        assertTrue(set.contains(c));
        assertTrue(set.contains(d));
        assertFalse(set.contains(e));
    }

    @Test
    public void testIterator() {
        a.connectTo(b);
        a.connectTo(d);
        
        Set<DirectedGraphNode> set = new HashSet<>();
        
        for (final DirectedGraphNode u : a) {
            set.add(u);
        }
        
        assertEquals(2, set.size());
    }

    @Test
    public void testConnectTo() {
        a.connectTo(b);
        
        assertTrue(a.isConnectedTo(b));
        assertFalse(b.isConnectedTo(a));
        
        assertFalse(a.isConnectedTo(c));
        assertFalse(c.isConnectedTo(a));
        
        assertEquals(1, g.edges());
        
        a.connectTo(b);
        
        assertEquals(1, g.edges());
        
        b.connectTo(a);
        
        assertEquals(2, g.edges());
    }

    @Test
    public void testDisconnect() {
        a.connectTo(d);
        assertTrue(a.isConnectedTo(d));
        assertFalse(d.isConnectedTo(a));
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
        assertFalse(d.isConnectedTo(c));
        assertFalse(c.isConnectedTo(a));
        assertFalse(a.isConnectedTo(c));
    }

    @Test
    public void testGetOwnerGraph() {
        assertEquals(a.getOwnerGraph(), g);
        assertEquals(b.getOwnerGraph(), g);
        assertNull(new DirectedGraphNode("X").getOwnerGraph());
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
        
        assertTrue(a.isConnectedTo(b));
        assertTrue(a.isConnectedTo(c));
        assertTrue(a.isConnectedTo(d));
        
        assertFalse(b.isConnectedTo(a));
        assertFalse(c.isConnectedTo(a));
        assertFalse(d.isConnectedTo(a));
        
        assertEquals(4, g.edges());
        
        a.clear();
        
        assertFalse(a.isConnectedTo(b));
        assertFalse(a.isConnectedTo(c));
        assertFalse(a.isConnectedTo(d));
        
        assertFalse(b.isConnectedTo(a));
        assertFalse(c.isConnectedTo(a));
        assertFalse(d.isConnectedTo(a));
        
        assertEquals(1, g.edges());
        
        c.clear();
        
        assertEquals(0, g.edges());
        assertFalse(c.isConnectedTo(d));
        assertFalse(d.isConnectedTo(c));
    }
    
    @Test(expected = NullPointerException.class)
    public void constructorWorksOnlyWithName() {
        new DirectedGraphNode(null);
    }
    
    @Test(expected = NullPointerException.class)
    public void cannotConnectToNull() {
        a.connectTo(null);
    }
    
    @Test(expected = IllegalStateException.class)
    public void firstNodeMustBelongToGraph() {
        x.connectTo(a);
    }
    
    @Test(expected = IllegalStateException.class)
    public void secondNodeMustBelongToGraph() {
        a.connectTo(x);
    }
    
    @Test(expected = IllegalStateException.class)
    public void bothNodesMustBeInTheSameGraph() {
        new Graph("??").add(x);
        x.connectTo(a);
    }
    
    @Test(expected = NullPointerException.class)
    public void cannotDisconnectFromNull() {
        a.disconnect(null);
    }
    
    @Test(expected = IllegalStateException.class) 
    public void inputNodeMustBelongToAGraphOnDisconnect() {
        a.connectTo(x);
    }
    
    @Test(expected = IllegalStateException.class)
    public void nodesBelongToSameGraphOnDisconnect() {
        new Graph("?").add(x);
        a.disconnect(x);
    }
    
    public void testEquals() {
        DirectedGraphNode xx = new DirectedGraphNode("B");
        assertTrue(xx.equals(b));
    }
}
