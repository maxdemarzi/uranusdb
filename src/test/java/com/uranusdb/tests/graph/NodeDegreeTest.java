package com.uranusdb.tests.graph;

import com.uranusdb.graph.Direction;
import com.uranusdb.graph.FastUtilGraph;
import com.uranusdb.graph.Graph;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class NodeDegreeTest {
    private Graph db;

    @Before
    public void setup() throws IOException {
        db = new FastUtilGraph();
        db.addNode("Node", "empty");
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
    public void shouldGetNodeDegree() {
        db.addNode("Node", "four");
        db.addNode("Node", "five");
        db.addNode("Node", "six");
        db.addRelationship("FRIENDS", "Node", "four", "Node", "five");
        db.addRelationship("ENEMIES", "Node", "four", "Node", "six");
        Integer actual = db.getNodeDegree("Node", "four");
        Assert.assertEquals(Integer.valueOf(2), actual);
    }

    @Test
    public void shouldGetNodeIncomingDegree() {
        db.addNode("Node", "four");
        db.addNode("Node", "five");
        db.addNode("Node", "six");
        db.addRelationship("FRIENDS", "Node", "four", "Node", "five");
        db.addRelationship("ENEMIES", "Node", "six", "Node", "four");
        Integer actual = db.getNodeDegree("Node", "four", Direction.OUT);
        Assert.assertEquals(Integer.valueOf(1), actual);
    }

    @Test
    public void shouldGetNodeOutgoingDegree() {
        db.addNode("Node", "four");
        db.addNode("Node", "five");
        db.addNode("Node", "six");
        db.addRelationship("FRIENDS", "Node", "four", "Node", "five");
        db.addRelationship("ENEMIES", "Node", "six", "Node", "four");
        Integer actual = db.getNodeDegree("Node", "four", Direction.OUT);
        Assert.assertEquals(Integer.valueOf(1), actual);
    }

    @Test
    public void shouldGetNodeIncomingTypedDegree() {
        db.addNode("Node", "four");
        db.addNode("Node", "five");
        db.addNode("Node", "six");
        db.addRelationship("FRIENDS", "Node", "five", "Node", "four");
        db.addRelationship("ENEMIES", "Node", "six", "Node", "four");
        Integer actual = db.getNodeDegree("Node", "four", Direction.IN, new ArrayList<String>() {{
            add("ENEMIES");
        }});
        Assert.assertEquals(Integer.valueOf(1), actual);
    }

    @Test
    public void shouldGetNodeOutgoingTypedDegree() {
        db.addNode("Node", "four");
        db.addNode("Node", "five");
        db.addNode("Node", "six");
        db.addRelationship("FRIENDS", "Node", "four", "Node", "five");
        db.addRelationship("ENEMIES", "Node", "four", "Node", "six");
        Integer actual = db.getNodeDegree("Node", "four", Direction.OUT, new ArrayList<String>() {{
            add("ENEMIES");
        }});
        Assert.assertEquals(Integer.valueOf(1), actual);
    }

    @Test
    public void shouldGetNodeDegreeMultiple() {
        db.addNode("Node", "four");
        db.addNode("Node", "five");
        db.addNode("Node", "six");
        db.addRelationship("FRIENDS", "Node", "four", "Node", "five");
        db.addRelationship("ENEMIES", "Node", "four", "Node", "six");
        db.addRelationship("FRIENDS", "Node", "four", "Node", "five");
        Integer actual = db.getNodeDegree("Node", "four");
        Assert.assertEquals(Integer.valueOf(3), actual);
    }
}
