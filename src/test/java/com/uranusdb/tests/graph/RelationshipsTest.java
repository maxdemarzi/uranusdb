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

import static java.lang.Math.toIntExact;

public class RelationshipsTest {
    private Graph db;

    @Before
    public void setup() throws IOException {
        db = new FastUtilGraph();
        db.addNode("Node", "empty");
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("name", "max");
        properties.put("email", "maxdemarzi@hotmail.com");
        db.addNode("Node", "existing", properties);
        HashMap<String, Object> relProperties = new HashMap<>();
        relProperties.put("weight", 5);
        db.addRelationship("RELATED", "Node", "empty", "Node", "existing", relProperties);
    }

    @After
    public void tearDown() {
        db = null;
    }


    @Test
    public void shouldResolveIds() {
        int node1 = 7;
        int rel = 5;
        long combo;

        combo = node1;
        combo = combo << 32;
        combo += rel;
        int nodeId = toIntExact(combo >> 32);
        int relId = toIntExact((combo << 32) >> 32);
        Assert.assertEquals(nodeId, node1);
        Assert.assertEquals(rel, relId);

    }


    @Test
    public void shouldAddRelationship() {
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        boolean created = db.addRelationship("FRIENDS", "Node", "one", "Node", "two");
        Assert.assertTrue(created);
    }

    @Test
    public void shouldAddRelationshipWithProperties() {
        HashMap<String, Object> properties = new HashMap<String, Object>() {{ put("stars", 5); }};
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        db.addRelationship("RATED", "Node", "one", "Node", "two", properties);
        Object actual = db.getRelationship("RATED", "Node", "one", "Node", "two");
        Assert.assertEquals(properties, actual);
        Integer expected = 1;
        Assert.assertEquals(expected, db.getRelationshipTypeCount("RATED"));
    }

    @Test
    public void shouldAddMultipleRelationshipsSameType() {
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        db.addRelationship("MULTIPLE", "Node", "one", "Node", "two");
        boolean created = db.addRelationship("MULTIPLE", "Node", "one", "Node", "two");
        Assert.assertTrue(created);
        Integer expected = 2;
        Assert.assertEquals(expected, db.getRelationshipTypeCount("MULTIPLE"));
        Assert.assertEquals(new HashMap<String, Object>(){{
            put("_incoming_node_id", 2);
            put("_outgoing_node_id", 3);
        }}, db.getRelationship("MULTIPLE", "Node", "one", "Node", "two"));
        Assert.assertEquals(new HashMap<String, Object>(){{
            put("_incoming_node_id", 2);
            put("_outgoing_node_id", 3);
        }}, db.getRelationship("MULTIPLE", "Node", "one", "Node", "two", 2));
    }

    @Test
    public void shouldAddMultipleRelationshipsSameTypeWithProperties() {
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        Map rel1Properties = new HashMap<String, Object>() {{ put("key", "rel1");}};
        Map rel2Properties = new HashMap<String, Object>() {{ put("key", "rel2");}};
        db.addRelationship("MULTIPLE", "Node", "one", "Node", "two", rel1Properties);
        boolean created = db.addRelationship("MULTIPLE", "Node", "one", "Node", "two", rel2Properties);
        Assert.assertTrue(created);
        Integer expected = 2;
        Assert.assertEquals(expected, db.getRelationshipTypeCount("MULTIPLE"));
        Assert.assertEquals(rel1Properties, db.getRelationship("MULTIPLE", "Node", "one", "Node", "two"));
        Assert.assertEquals(rel2Properties, db.getRelationship("MULTIPLE", "Node", "one", "Node", "two", 2));
    }

    @Test
    public void shouldRemoveRelationship() {
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        db.addRelationship("LIKES", "Node", "one", "Node", "two");
        Integer expected = 1;
        Assert.assertEquals(expected, db.getRelationshipTypeCount("LIKES"));
        db.removeRelationship("LIKES", "Node", "one", "Node", "two");
        expected = 0;
        Assert.assertEquals(expected, db.getRelationshipTypeCount("LIKES"));
    }

    @Test
    public void shouldRemoveMultipleRelationshipsSameType() {
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        Map<String, Object> rel1Properties = new HashMap<String, Object>() {{ put("key", "rel1");}};
        Map<String, Object> rel2Properties = new HashMap<String, Object>() {{ put("key", "rel2");}};
        Map<String, Object> rel3Properties = new HashMap<String, Object>() {{ put("key", "rel3");}};

        db.addRelationship("LOVES", "Node", "one", "Node", "two", rel1Properties);
        db.addRelationship("LOVES", "Node", "one", "Node", "two", rel2Properties);
        db.addRelationship("LOVES", "Node", "one", "Node", "two", rel3Properties);
        Integer expected = 3;
        Assert.assertEquals(expected, db.getRelationshipTypeCount("LOVES"));
        db.removeRelationship("LOVES", "Node", "one", "Node", "two");
        expected = 2;
        Assert.assertEquals(expected, db.getRelationshipTypeCount("LOVES"));
        Map<String, Object> rel = db.getRelationship("LOVES", "Node", "one", "Node", "two");
        Assert.assertEquals(rel1Properties, rel);
    }

    @Test
    public void shouldRemoveMultipleRelationshipsSameTypeLast() {
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        Map<String, Object> rel1Properties = new HashMap<String, Object>() {{ put("key", "rel1");}};
        Map<String, Object> rel2Properties = new HashMap<String, Object>() {{ put("key", "rel2");}};
        Map<String, Object> rel3Properties = new HashMap<String, Object>() {{ put("key", "rel3");}};

        db.addRelationship("LOVES", "Node", "one", "Node", "two", rel1Properties);
        db.addRelationship("LOVES", "Node", "one", "Node", "two", rel2Properties);
        db.addRelationship("LOVES", "Node", "one", "Node", "two", rel3Properties);
        Integer expected = 3;
        Assert.assertEquals(expected, db.getRelationshipTypeCount("LOVES"));
        db.removeRelationship("LOVES", "Node", "one", "Node", "two", 3);
        expected = 2;
        Assert.assertEquals(expected, db.getRelationshipTypeCount("LOVES"));
        Map<String, Object> rel = db.getRelationship("LOVES", "Node", "one", "Node", "two");
        Assert.assertEquals(rel1Properties, rel);
        rel = db.getRelationship("LOVES", "Node", "one", "Node", "two", 2);
        Assert.assertEquals(rel2Properties, rel);
    }

    @Test
    public void shouldRemoveMultipleRelationshipsSameTypeMiddle() {
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        Map<String, Object> rel1Properties = new HashMap<String, Object>() {{ put("key", "rel1");}};
        Map<String, Object> rel2Properties = new HashMap<String, Object>() {{ put("key", "rel2");}};
        Map<String, Object> rel3Properties = new HashMap<String, Object>() {{ put("key", "rel3");}};

        db.addRelationship("LOVES", "Node", "one", "Node", "two", rel1Properties);
        db.addRelationship("LOVES", "Node", "one", "Node", "two", rel2Properties);
        db.addRelationship("LOVES", "Node", "one", "Node", "two", rel3Properties);
        Integer expected = 3;
        Assert.assertEquals(expected, db.getRelationshipTypeCount("LOVES"));
        db.removeRelationship("LOVES", "Node", "one", "Node", "two", 2);
        expected = 2;
        Assert.assertEquals(expected, db.getRelationshipTypeCount("LOVES"));
        Map<String, Object> rel = db.getRelationship("LOVES", "Node", "one", "Node", "two");
        Assert.assertEquals(rel1Properties, rel);
        rel = db.getRelationship("LOVES", "Node", "one", "Node", "two", 2);
        Assert.assertEquals(rel3Properties, rel);
    }

    @Test
    public void shouldGetRelationshipWithoutProperties() {
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        db.addRelationship("RATED", "Node", "one", "Node", "two");
        Object actual = db.getRelationship("RATED", "Node", "one", "Node", "two");
        Assert.assertEquals(new HashMap<String, Object>(){{
            put("_incoming_node_id", 2);
            put("_outgoing_node_id", 3);
        }}, actual);
        Integer expected = 1;
        Assert.assertEquals(expected, db.getRelationshipTypeCount("RATED"));
    }

    @Test
    public void shouldGetRelationshipWithProperties() {
        HashMap<String, Object> properties = new HashMap<String, Object>() {{ put("stars", 5); }};
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        db.addRelationship("RATED", "Node", "one", "Node", "two", properties);
        Object actual = db.getRelationship("RATED", "Node", "one", "Node", "two");
        Assert.assertEquals(properties, actual);
        Integer expected = 1;
        Assert.assertEquals(expected, db.getRelationshipTypeCount("RATED"));
    }

    @Test
    public void shouldGetMultipleRelationshipsSameType() {
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        Map<String, Object> rel1Properties = new HashMap<String, Object>() {{ put("key", "rel1");}};
        Map<String, Object> rel2Properties = new HashMap<String, Object>() {{ put("key", "rel2");}};

        db.addRelationship("MULTIPLE", "Node", "one", "Node", "two", rel1Properties);
        boolean created = db.addRelationship("MULTIPLE", "Node", "one", "Node", "two", rel2Properties);
        Assert.assertTrue(created);
        Integer expected = 2;
        Assert.assertEquals(expected, db.getRelationshipTypeCount("MULTIPLE"));
        Assert.assertEquals(rel1Properties, db.getRelationship("MULTIPLE", "Node", "one", "Node", "two"));
        Assert.assertEquals(rel2Properties, db.getRelationship("MULTIPLE", "Node", "one", "Node", "two", 2));
    }

    @Test
    public void shouldGetAllRelationships() {
        int count = 0;

        Iterator<Map<String, Object>> iterator = db.getAllRelationships();
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        Assert.assertEquals(1, count);
    }

    @Test
    public void shouldGetAllTypedRelationships() {
        int count = 0;

        Iterator<Map<String, Object>> iterator = db.getRelationships("RELATED");
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        Assert.assertEquals(1, count);
    }
}
