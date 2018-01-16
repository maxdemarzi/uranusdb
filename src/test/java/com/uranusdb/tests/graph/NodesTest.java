package com.uranusdb.tests.graph;

import com.uranusdb.graph.FastUtilGraph;
import com.uranusdb.graph.Graph;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NodesTest {
    private Graph db;

    @Before
    public void setup() throws IOException {
        db = new FastUtilGraph();
        db.addNode("Node",  "empty");
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("name", "max");
        properties.put("email", "maxdemarzi@hotmail.com");
        db.addNode("Node", "existing", properties);
    }

    @After
    public void tearDown() {
        db = null;
    }

    @Test
    public void shouldAddNode() {
        int created = db.addNode("Node", "key");
        Assert.assertTrue(created > -1);
        Assert.assertEquals(new HashMap<String, Object>() {{
            put("~id", 2);
            put("~label", "Node");
            put("~key", "key");
        }}, db.getNode("Node", "key"));
    }

    @Test
    public void shouldAddNodeWithProperties() {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("name", "max");
        properties.put("email", "maxdemarzi@hotmail.com");
        int created = db.addNode("Node", "max", properties);
        Assert.assertTrue(created > -1);
        Assert.assertEquals(properties, db.getNode("Node", "max"));
    }

    @Test
    public void shouldRemoveNode() {
        int result = db.addNode("Node", "simple");
        Assert.assertTrue(result > -1);
        boolean remove = db.removeNode("Node","simple");
        Assert.assertTrue(remove);
        Assert.assertTrue(db.getNode("Node", "simple") == null);
    }

    @Test
    public void shouldRemoveNodeInMiddle() {
        HashMap<String, Object> node1props = new HashMap<>();
        node1props.put("id", "node1");
        HashMap<String, Object> node2props = new HashMap<>();
        node2props.put("id", "node2");
        HashMap<String, Object> node3props = new HashMap<>();
        node3props.put("id", "node3");
        HashMap<String, Object> node4props = new HashMap<>();
        node3props.put("id", "node4");

        int result = db.addNode("Node", "node1", node1props);
        db.addNode("Node", "node2", node2props);
        db.addNode("Node", "node3", node3props);
        Assert.assertTrue(result > -1);
        int node2Id = db.getNodeId("Node", "node2");
        boolean remove = db.removeNode("Node", "node2");
        Assert.assertTrue(remove);
        Assert.assertTrue(db.getNode("Node", "node2") == null);
        Assert.assertEquals(node1props, db.getNode("Node", "node1"));
        Assert.assertEquals(node3props, db.getNode("Node", "node3"));
        db.addNode("Node", "node4", node4props);
        Assert.assertEquals(node4props, db.getNode("Node", "node4"));
        Assert.assertEquals(node2Id, db.getNodeId("Node", "node4"));
    }

    @Test
    public void shouldRemoveNodeRelationships() {
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        db.addNode("Node", "three");
        db.addRelationship("FRIENDS", "Node", "one", "Node", "two");
        db.addRelationship("FRIENDS", "Node", "three", "Node", "one");

        boolean result = db.removeNode("Node", "one");
        Assert.assertTrue(result);
        Integer expected = 0;
        Assert.assertEquals(expected, db.getRelationshipTypeCount("FRIENDS"));

        Assert.assertEquals(null, db.getRelationship("FRIENDS", "Node", "one", "Node", "two"));
        Assert.assertEquals(null, db.getRelationship("FRIENDS", "Node", "three", "Node", "one"));
    }

    @Test
    public void shouldAddNodeWithObjectProperties() {
        HashMap<String, Object> address = new HashMap<>();
        address.put("Country", "USA");
        address.put("Zip", "60601");
        address.put("State", "TX");
        address.put("City", "Chicago");
        address.put("Line1 ", "175 N. Harbor Dr.");
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("name", "max");
        properties.put("email", "maxdemarzi@hotmail.com");
        properties.put("address", address);
        int created = db.addNode("Node", "complex", properties);
        Assert.assertTrue(created > -1);
        Assert.assertEquals(properties, db.getNode("Node", "complex"));
    }

    @Test
    public void shouldGetEmptyNode() {
        Assert.assertEquals(new HashMap<String, Object>() {{
            put("~id", 0);
            put("~label", "Node");
            put("~key", "empty");  }}, db.getNode("Node", "empty"));
    }

    @Test
    public void shouldGetNodeWithProperties() {
        HashMap<String, Object> properties = new HashMap<String, Object>() {{
            put("~id", 1);
            put("~label", "Node");
            put("~key", "existing");  }};
        properties.put("name", "max");
        properties.put("email", "maxdemarzi@hotmail.com");
        Assert.assertEquals(properties, db.getNode("Node", "existing"));
    }

    @Test
    public void shouldGetNodeId() {
        int actual = db.getNodeId("Node", "empty");
        Assert.assertEquals(0, actual);
        actual = db.getNodeId("Node", "existing");
        Assert.assertEquals(1, actual);
    }

    @Test
    public void shouldNotAddNodeAlreadyThere() {
        int created = db.addNode("Node", "key");
        Assert.assertTrue(created > -1);
        Assert.assertEquals(new HashMap<String, Object>() {{
            put("~id", 2);
            put("~label", "Node");
            put("~key", "key");  }},
                db.getNode("Node", "key"));
        created = db.addNode("Node", "key");
        Assert.assertFalse(created > -1);
    }

    @Test
    public void shouldNotGetNodeNotThere() {
        Assert.assertEquals(null, db.getNode("Node", "notThere"));
    }

    @Test
    public void shouldNotRemoveNodeNotThere() {
        boolean result = db.removeNode("Node", "not_there");
        Assert.assertFalse(result);
    }

    @Test
    public void shouldNotGetNodeIdOfNodeNotThere() {
        int actual = db.getNodeId("Node", "not-empty");
        Assert.assertEquals(-1, actual);
    }

    @Test
    public void shouldGetAllNodes() {
        int count = 0;

        Iterator<Map<String, Object>> iterator = db.getAllNodes();
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        Assert.assertEquals(2, count);
    }

    @Test
    public void shouldGetAllLabeledNodes() {
        int count = 0;

        Iterator<Map<String, Object>> iterator = db.getNodes("Node");
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        Assert.assertEquals(2, count);
    }

    @Test
    public void shouldClearGraph() {
        db.clear();
        int count = 0;
        Iterator<Map<String, Object>> iterator = db.getAllNodes();
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        Assert.assertEquals(0, count);
    }
}
