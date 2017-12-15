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

public class RelatedTest {
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

        db.addNode("Node", "disconnected", properties);

    }

    @After
    public void tearDown() {
        db = null;
    }


    @Test
    public void shouldFindRelated() {
        Assert.assertTrue(db.related("Node", "empty", "Node", "existing"));
        Assert.assertTrue(db.related("Node", "empty", "Node", "existing", Direction.ALL, "RELATED"));
        Assert.assertTrue(db.related("Node", "empty", "Node", "existing", Direction.ALL, new ArrayList<String>(){{ add("RELATED");}}));
    }

    @Test
    public void shouldNotFindUnRelated() {
        Assert.assertFalse(db.related("Node", "empty", "Node", "disconnected"));
        Assert.assertFalse(db.related("Node", "empty", "Node", "disconnected", Direction.ALL, "RELATED"));
        Assert.assertFalse(db.related("Node", "empty", "Node", "disconnected", Direction.ALL, new ArrayList<String>(){{ add("RELATED");}}));
    }

    @Test
    public void shouldNotFindUnFound() {
        Assert.assertFalse(db.related("Node", "empty", "Node", "not_there"));
        Assert.assertFalse(db.related("Node", "empty", "Node", "not_there", Direction.ALL, "RELATED"));
        Assert.assertFalse(db.related("Node", "empty", "Node", "not_there", Direction.ALL, new ArrayList<String>(){{ add("RELATED");}}));
    }

}
