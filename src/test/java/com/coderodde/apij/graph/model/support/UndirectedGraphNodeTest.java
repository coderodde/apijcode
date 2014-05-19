package com.coderodde.apij.graph.model.support;

import com.coderodde.apij.graph.model.Graph;
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
    private Graph g;
    
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
    }

    @Test
    public void testConnectTo() {
    }

    @Test
    public void testDisconnect() {
    }

    @Test
    public void testIsConnectedTo() {
    }

    @Test
    public void testGetOwnerGraph() {
    }

    @Test
    public void testSetOwnerGraph() {
    }

    @Test
    public void testClear() {
    }
    
}
