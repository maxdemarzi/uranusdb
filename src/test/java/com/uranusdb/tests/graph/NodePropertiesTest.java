package com.uranusdb.tests.graph;

import com.uranusdb.graph.FastUtilGraph;
import com.uranusdb.graph.Graph;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NodePropertiesTest {
    private Graph db;

    @Before
    public void setup() throws IOException {
        db = new FastUtilGraph();
        db.addNode("Node","empty");
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("name", "max");
        properties.put("email", "maxdemarzi@hotmail.com");
        db.addNode("Node","existing", properties);
    }

    @After
    public void tearDown() {
        db = null;
    }

    @Test
    public void shouldGetNodeProperty() {
        Object property = db.getNodeProperty("Node","existing", "name");
        Assert.assertEquals("max", property);
    }

    @Test
    public void shouldNotGetNodePropertyNotThere() {
        Object property = db.getNodeProperty("Node","existing", "eman");
        Assert.assertEquals(null, property);
    }

    @Test
    public void shouldNotGetNodePropertyNodeNotThere() {
        Object property = db.getNodeProperty("Node","not-existing", "name");
        Assert.assertEquals(null, property);
    }

    @Test
    public void shouldUpdateNodeProperties() {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("property", "this");
        db.addNode("Node","this", properties);
        properties.put("property", "that");
        db.updateNodeProperties("Node","this", properties);
        Object property = db.getNodeProperty("Node","this", "property");
        Assert.assertEquals("that", property);
    }

    @Test
    public void shouldDeleteNodeProperties() {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("property", "this");
        db.addNode("Node","this", properties);
        db.deleteNodeProperties("Node","this");
        Assert.assertEquals(new HashMap<>(), db.getNode("Node","this"));
    }

    @Test
    public void shouldUpdateNodeProperty() {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("property", "this");
        db.addNode("Node","this", properties);
        db.updateNodeProperty("Node","this", "property", "that");
        Object property = db.getNodeProperty("Node","this", "property");
        Assert.assertEquals("that", property);
    }

    @Test
    public void shouldDeleteNodeProperty() {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("property", "this");
        properties.put("other", "that");
        db.addNode("Node","this", properties);
        db.deleteNodeProperty("Node","this", "other");
        Map<String, Object> node = db.getNode("Node","this");
        Assert.assertEquals("this", node.get("property"));
        Assert.assertTrue(node.get("other") == null);
    }

}
