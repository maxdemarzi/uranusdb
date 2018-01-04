package com.uranusdb.tests.graph;

import com.uranusdb.graph.FastUtilGraph;
import com.uranusdb.graph.Graph;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TraversingTest {
    private Graph db;

    @Before
    public void setup() throws IOException {
        db = new FastUtilGraph();
    }

    @After
    public void tearDown() {
        db = null;
    }

    @Test
    public void shouldGetNodeOutgoingRelationshipsFromKey() {
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        db.addNode("Node", "three");
        db.addRelationship("FRIENDS", "Node", "one", "Node", "two");
        db.addRelationship("ENEMIES", "Node", "one", "Node", "three");
        List<Map<String, Object>> actual = db.getOutgoingRelationships("Node", "one");
        ArrayList<Map<String, Object>> expected = new ArrayList<>();
        expected.add(new HashMap<String, Object>(){{
            put("_type", "ENEMIES");
            put("_id", 1);
            put("_incoming_node_id", 0);
            put("_outgoing_node_id", 2);  }});
        expected.add(new HashMap<String, Object>(){{
            put("_type", "FRIENDS");
            put("_id", 0);
            put("_incoming_node_id", 0);
            put("_outgoing_node_id", 1);  }});        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldGetNodeOutgoingRelationshipsFromId() {
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        db.addNode("Node", "three");
        db.addRelationship("FRIENDS", "Node", "one", "Node", "two");
        db.addRelationship("ENEMIES", "Node", "one", "Node", "three");
        List<Map<String, Object>> actual = db.getOutgoingRelationships(0);
        ArrayList<Map<String, Object>> expected = new ArrayList<>();
        expected.add(new HashMap<String, Object>(){{
            put("_type", "ENEMIES");
            put("_id", 1);
            put("_incoming_node_id", 0);
            put("_outgoing_node_id", 2);  }});
        expected.add(new HashMap<String, Object>(){{
            put("_type", "FRIENDS");
            put("_id", 0);
            put("_incoming_node_id", 0);
            put("_outgoing_node_id", 1);  }});
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldGetNodeOutgoingRelationshipsOfTypeFromKey() {
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        db.addNode("Node", "three");
        db.addRelationship("FRIENDS", "Node", "one", "Node", "two");
        db.addRelationship("ENEMIES", "Node", "one", "Node", "three");
        List<Map<String, Object>> actual = db.getOutgoingRelationships("FRIENDS", "Node", "one");
        ArrayList<Map<String, Object>> expected = new ArrayList<>();
        expected.add(new HashMap<String, Object>(){{
            put("_type", "FRIENDS");
            put("_id", 0);
            put("_incoming_node_id", 0);
            put("_outgoing_node_id", 1);  }});

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldGetNodeOutgoingRelationshipsOfTypeFromId() {
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        db.addNode("Node", "three");
        db.addRelationship("FRIENDS", "Node", "one", "Node", "two");
        db.addRelationship("ENEMIES", "Node", "one", "Node", "three");
        List<Map<String, Object>> actual = db.getOutgoingRelationships("FRIENDS",0);
        ArrayList<Map<String, Object>> expected = new ArrayList<>();
        expected.add(new HashMap<String, Object>(){{
            put("_type", "FRIENDS");
            put("_id", 0);
            put("_incoming_node_id", 0);
            put("_outgoing_node_id", 1);  }});

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldGetNodeIncomingRelationshipsFromKey() {
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        db.addNode("Node", "three");
        db.addRelationship("FRIENDS", "Node", "one", "Node", "two");
        db.addRelationship("ENEMIES", "Node", "three", "Node", "two");
        List<Map<String, Object>> actual = db.getIncomingRelationships("Node", "two");
        ArrayList<Map<String, Object>> expected = new ArrayList<>();
        expected.add(new HashMap<String, Object>(){{
            put("_type", "ENEMIES");
            put("_id", 1);
            put("_incoming_node_id", 2);
            put("_outgoing_node_id", 1);  }});
        expected.add(new HashMap<String, Object>(){{
            put("_type", "FRIENDS");
            put("_id", 0);
            put("_incoming_node_id", 0);
            put("_outgoing_node_id", 1);  }});
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldGetNodeIncomingRelationshipsFromId() {
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        db.addNode("Node", "three");
        db.addRelationship("FRIENDS", "Node", "one", "Node", "two");
        db.addRelationship("ENEMIES", "Node", "three", "Node", "two");
        List<Map<String, Object>> actual = db.getIncomingRelationships(1);
        ArrayList<Map<String, Object>> expected = new ArrayList<>();
        expected.add(new HashMap<String, Object>(){{
            put("_type", "ENEMIES");
            put("_id", 1);
            put("_incoming_node_id", 2);
            put("_outgoing_node_id", 1);  }});
        expected.add(new HashMap<String, Object>(){{
            put("_type", "FRIENDS");
            put("_id", 0);
            put("_incoming_node_id", 0);
            put("_outgoing_node_id", 1);  }});
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldGetNodeIncomingRelationshipsOfTypeFromKey() {
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        db.addNode("Node", "three");
        db.addRelationship("FRIENDS", "Node", "one", "Node", "two");
        db.addRelationship("ENEMIES", "Node", "three", "Node", "two");
        List<Map<String, Object>> actual = db.getIncomingRelationships("FRIENDS", "Node", "two");
        ArrayList<Map<String, Object>> expected = new ArrayList<>();
        expected.add(new HashMap<String, Object>(){{
            put("_type", "FRIENDS");
            put("_id", 0);
            put("_incoming_node_id", 0);
            put("_outgoing_node_id", 1);  }});
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldGetNodeIncomingRelationshipsOfTypeFromId() {
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        db.addNode("Node", "three");
        db.addRelationship("FRIENDS", "Node", "one", "Node", "two");
        db.addRelationship("ENEMIES", "Node", "three", "Node", "two");
        List<Map<String, Object>> actual = db.getIncomingRelationships("FRIENDS",1);
        ArrayList<Map<String, Object>> expected = new ArrayList<>();
        expected.add(new HashMap<String, Object>(){{
            put("_type", "FRIENDS");
            put("_id", 0);
            put("_incoming_node_id", 0);
            put("_outgoing_node_id", 1);  }});

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldGetNodeOutgoingRelationshipNodeIds() {
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        db.addNode("Node", "three");
        db.addRelationship("FRIENDS", "Node", "one", "Node", "two");
        db.addRelationship("FRIENDS","Node",  "one", "Node", "three");
        List<Integer> actual = db.getOutgoingRelationshipNodeIds("FRIENDS", "Node", "one");
        ArrayList<Integer> expected = new ArrayList<>();
        expected.add(db.getNodeId("Node", "two"));
        expected.add(db.getNodeId("Node", "three"));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldGetNodeIncomingRelationshipNodeIds() {
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        db.addNode("Node", "three");
        db.addRelationship("FRIENDS", "Node", "two", "Node", "one");
        db.addRelationship("FRIENDS", "Node", "three", "Node", "one");
        List<Integer> actual = db.getIncomingRelationshipNodeIds("FRIENDS", "Node", "one");
        ArrayList<Integer> expected = new ArrayList<>();
        expected.add(db.getNodeId("Node", "two"));
        expected.add(db.getNodeId("Node", "three"));

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldGetNodeOutgoingRelationshipNodes() {
        HashMap<String, Object> node2props = new HashMap<String, Object> (){{ put("two", "node two"); }};
        HashMap<String, Object> node3props = new HashMap<String, Object> (){{ put("property1", 3); }};

        db.addNode("Node", "one");
        db.addNode("Node", "two", node2props);
        db.addNode("Node", "three", node3props);

        db.addRelationship("FRIENDS", "Node", "one", "Node", "two");
        db.addRelationship("FRIENDS", "Node", "one", "Node", "three");
        Object[] actual = db.getOutgoingRelationshipNodes("FRIENDS", "Node", "one");
        Object[] expected = new Object[2];
        expected[0] = node2props;
        expected[1] = node3props;

        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void shouldGetNodeIncomingRelationshipNodes() {
        HashMap<String, Object> node1props = new HashMap<String, Object> (){{ put("one", 1); }};
        HashMap<String, Object> node2props = new HashMap<String, Object> (){{ put("two", "node two"); }};
        HashMap<String, Object> node3props = new HashMap<String, Object> (){{ put("property1", 3); }};

        db.addNode("Node", "one", node1props);
        db.addNode("Node", "two", node2props);
        db.addNode("Node", "three", node3props);

        db.addRelationship("FRIENDS", "Node", "two","Node",  "one");
        db.addRelationship("FRIENDS", "Node", "three","Node",  "one");
        Object[] actual = db.getIncomingRelationshipNodes("FRIENDS", "Node", "one");
        Object[] expected = new Object[2];
        expected[0] = node2props;
        expected[1] = node3props;

        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void shouldGetNodeOutgoingRelationshipNodesByID() {
        HashMap<String, Object> node2props = new HashMap<String, Object> (){{ put("two", "node two"); }};
        HashMap<String, Object> node3props = new HashMap<String, Object> (){{ put("property1", 3); }};

        db.addNode("Node", "one");
        db.addNode("Node", "two", node2props);
        db.addNode("Node", "three", node3props);

        db.addRelationship("FRIENDS", "Node", "one", "Node", "two");
        db.addRelationship("FRIENDS", "Node", "one","Node",  "three");
        int one = db.getNodeId("Node", "one");
        Object[] actual = db.getOutgoingRelationshipNodes("FRIENDS", one);
        Object[] expected = new Object[2];
        expected[0] = node2props;
        expected[1] = node3props;

        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void shouldGetNodeIncomingRelationshipNodesByID() {
        HashMap<String, Object> node1props = new HashMap<String, Object> (){{ put("one", 1); }};
        HashMap<String, Object> node2props = new HashMap<String, Object> (){{ put("two", "node two"); }};
        HashMap<String, Object> node3props = new HashMap<String, Object> (){{ put("property1", 3); }};

        db.addNode("Node", "one", node1props);
        db.addNode("Node", "two", node2props);
        db.addNode("Node", "three", node3props);

        db.addRelationship("FRIENDS", "Node", "two", "Node", "one");
        db.addRelationship("FRIENDS", "Node", "three", "Node", "one");
        int one = db.getNodeId("Node", "one");
        Object[] actual = db.getIncomingRelationshipNodes("FRIENDS", one);
        Object[] expected = new Object[2];
        expected[0] = node2props;
        expected[1] = node3props;

        Assert.assertArrayEquals(expected, actual);
    }
}
