package com.uranusdb.tests.graph;

import com.uranusdb.graph.ReversibleMultiMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class ReversibleMultiMapTest {

    private ReversibleMultiMap reversibleMultiMap;

    @Before
    public void setup() throws IOException {
        reversibleMultiMap = new ReversibleMultiMap();
        reversibleMultiMap.put(1,1, 1);
        reversibleMultiMap.put(1,2, 2);
        reversibleMultiMap.put(3,4, 3);
    }

    @Test
    public void shouldCheckRMMSize() {
        Assert.assertEquals(3, reversibleMultiMap.size());
    }

    @Test
    public void shouldCheckRMMEmptyness() {
        Assert.assertEquals(false, reversibleMultiMap.isEmpty());
    }

    @Test
    public void shouldCheckRMMContainsKey() {
        Assert.assertEquals(true, reversibleMultiMap.containsNode(1));
        Assert.assertEquals(false, reversibleMultiMap.containsNode(9));
    }

    @Test
    public void shouldCheckRMMContainsOtherNode() {
        Assert.assertEquals(true, reversibleMultiMap.containsOtherNode(4));
        Assert.assertEquals(false, reversibleMultiMap.containsOtherNode(9));
    }

    @Test
    public void shouldCheckRMMContainsEntry() {
        Assert.assertEquals(true, reversibleMultiMap.containsEntry(1,2, 2));
        Assert.assertEquals(false, reversibleMultiMap.containsEntry(9,9, 2));
    }

    @Test
    public void shouldCheckRMMClearAll() {
        ReversibleMultiMap reversibleMultiMap2 = new ReversibleMultiMap();
        reversibleMultiMap2.put(8,8, 12);
        Assert.assertEquals(true, reversibleMultiMap2.containsEntry(8,8,12));
        reversibleMultiMap2.clear();
        Assert.assertEquals(false, reversibleMultiMap2.containsEntry(8,8,12));
    }


    @Test
    public void shouldCheckRMMEntries() {
        Assert.assertEquals(3 , reversibleMultiMap.size());
    }

}
