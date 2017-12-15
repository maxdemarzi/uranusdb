package com.uranusdb.tests.graph;

import com.uranusdb.graph.FastUtilGraph;
import com.uranusdb.graph.Graph;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class RelationshipTypesTest {
    private Graph db;

    @Before
    public void setup() throws IOException {
        db = new FastUtilGraph();
        db.addNode("Node",  "one");
        db.addNode("Node",  "two");
    }

    @After
    public void tearDown() {
        db = null;
    }

    @Test
    public void shouldGetRelationshipTypes() {
        db.addRelationship("FOLLOWS", "Node", "one", "Node", "two");
        Set<String> types = db.getRelationshipTypes();
        Assert.assertTrue(types.contains("FOLLOWS"));
    }

    @Test
    public void shouldGetRelationshipTypesCount() {
        db.addRelationship("FOLLOWS", "Node", "one", "Node", "two");
        Map<String, Integer> counts = db.getRelationshipTypesCount();
        Assert.assertTrue(counts.containsKey("FOLLOWS"));
        Assert.assertTrue(counts.get("FOLLOWS").equals(1));
    }

    @Test
    public void shouldGetRelationshipTypeCount() {
        db.addRelationship("FOLLOWS", "Node", "one", "Node", "two");
        Integer counts = db.getRelationshipTypeCount("FOLLOWS");
        Assert.assertTrue(counts.equals(1));
    }

}
