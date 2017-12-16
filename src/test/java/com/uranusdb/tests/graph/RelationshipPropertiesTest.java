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

public class RelationshipPropertiesTest {

    private Graph db;

    @Before
    public void setup() throws IOException {
        db = new FastUtilGraph();
        db.addNode("Node","empty");
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("name", "max");
        properties.put("email", "maxdemarzi@hotmail.com");
        db.addNode("Node","existing", properties);
        HashMap<String, Object> relProperties = new HashMap<>();
        relProperties.put("weight", 5);
        db.addRelationship("RELATED", "Node","empty", "Node","existing", relProperties);
    }

    @After
    public void tearDown() {
        db = null;
    }

    @Test
    public void shouldGetRelationshipProperty() {
        Object property = db.getRelationshipProperty("RELATED", "Node","empty", "Node","existing", "weight");
        Assert.assertEquals(5, property);
    }

    @Test
    public void shouldNotGetRelationshipPropertyNotThere() {
        Object property = db.getRelationshipProperty("RELATED", "Node","empty", "Node","existing", "not_there");
        Assert.assertEquals(null, property);
    }

    @Test
    public void shouldNotGetRelationshipPropertyRelationshipNotTHere() {
        Object property = db.getRelationshipProperty("RELATED","Node", "empty", "Node", "not_existing", "weight");
        Assert.assertEquals(null, property);
    }

    @Test
    public void shouldGetMultipleRelationshipProperty() {
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        Map<String, Object> rel1Properties = new HashMap<String, Object>() {{ put("key", "rel1");}};
        Map<String, Object> rel2Properties = new HashMap<String, Object>() {{ put("key", "rel2");}};
        Map<String, Object> rel3Properties = new HashMap<String, Object>() {{ put("key", "rel3");}};

        db.addRelationship("LOVES", "Node", "one", "Node", "two", rel1Properties);
        db.addRelationship("LOVES", "Node", "one", "Node", "two", rel2Properties);
        db.addRelationship("LOVES", "Node", "one", "Node", "two", rel3Properties);

        Object property = db.getRelationshipProperty("LOVES", "Node", "one", "Node", "two", "key");
        Assert.assertEquals("rel1", property);
        property = db.getRelationshipProperty("LOVES", "Node", "one", "Node", "two", 2,"key");
        Assert.assertEquals("rel2", property);
        property = db.getRelationshipProperty("LOVES", "Node", "one", "Node", "two", 3,"key");
        Assert.assertEquals("rel3", property);
    }

    @Test
    public void shouldNotGetMultipleRelationshipPropertyNotThere() {
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        Map<String, Object> rel1Properties = new HashMap<String, Object>() {{ put("key", "rel1");}};
        Map<String, Object> rel2Properties = new HashMap<String, Object>() {{ put("key", "rel2");}};
        Map<String, Object> rel3Properties = new HashMap<String, Object>() {{ put("key", "rel3");}};

        db.addRelationship("LOVES", "Node", "one", "Node", "two", rel1Properties);
        db.addRelationship("LOVES", "Node", "one", "Node", "two", rel2Properties);
        db.addRelationship("LOVES", "Node", "one", "Node", "two", rel3Properties);

        Object property = db.getRelationshipProperty("LOVES", "Node", "one", "Node", "two", "key");
        Assert.assertEquals("rel1", property);
        property = db.getRelationshipProperty("LOVES", "Node", "one", "Node", "two", 2,"not_there");
        Assert.assertEquals(null, property);
    }

    @Test
    public void shouldNotGetMultipleRelationshipPropertyRelationshipNotThere() {
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        Map<String, Object> rel1Properties = new HashMap<String, Object>() {{ put("key", "rel1");}};
        Map<String, Object> rel2Properties = new HashMap<String, Object>() {{ put("key", "rel2");}};
        Map<String, Object> rel3Properties = new HashMap<String, Object>() {{ put("key", "rel3");}};

        db.addRelationship("LOVES", "Node", "one", "Node", "two", rel1Properties);
        db.addRelationship("LOVES", "Node", "one", "Node", "two", rel2Properties);
        db.addRelationship("LOVES", "Node", "one", "Node", "two", rel3Properties);

        Object property = db.getRelationshipProperty("LOVES", "Node", "one", "Node", "two", "key");
        Assert.assertEquals("rel1", property);
        property = db.getRelationshipProperty("LOVES", "Node", "one", "Node", "two", 4,"key");
        Assert.assertEquals(null, property);
    }

    @Test
    public void shouldUpdateRelationshipProperties() {
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        Map<String, Object> rel1Properties = new HashMap<String, Object>() {{ put("key", "rel1");}};
        Map<String, Object> rel2Properties = new HashMap<String, Object>() {{ put("key", "rel2");}};

        db.addRelationship("LOVES", "Node", "one", "Node", "two", rel1Properties);
        db.updateRelationshipProperties("LOVES", "Node", "one", "Node", "two", rel2Properties);

        Map<String, Object> actual = db.getRelationship("LOVES","Node", "one","Node", "two");
        Assert.assertEquals(rel2Properties, actual);
    }

    @Test
    public void shouldUpdateMultipleRelationshipProperties() {
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        Map<String, Object> rel1Properties = new HashMap<String, Object>() {{ put("key", "rel1");}};
        Map<String, Object> rel2Properties = new HashMap<String, Object>() {{ put("key", "rel2");}};
        Map<String, Object> rel3Properties = new HashMap<String, Object>() {{ put("key", "rel3");}};

        db.addRelationship("LOVES", "Node", "one", "Node", "two", rel1Properties);
        db.addRelationship("LOVES", "Node", "one", "Node", "two", rel2Properties);
        db.updateRelationshipProperties("LOVES", "Node", "one", "Node", "two", 2, rel3Properties);

        Map<String, Object> actual = db.getRelationship("LOVES", "Node", "one", "Node", "two", 2);
        Assert.assertEquals(rel3Properties, actual);
    }

    @Test
    public void shouldDeleteRelationshipProperties() {
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        Map<String, Object> rel1Properties = new HashMap<String, Object>() {{ put("key", "rel1");}};

        db.addRelationship("LOVES", "Node", "one", "Node", "two", rel1Properties);
        db.deleteRelationshipProperties("LOVES", "Node", "one", "Node", "two");

        Map<String, Object> actual = db.getRelationship("LOVES", "Node", "one", "Node", "two");
        Assert.assertEquals(new HashMap<String, Object>() {{
            put("_incoming_node_id", 2);
            put("_outgoing_node_id", 3);
        }}, actual);
    }

    @Test
    public void shouldDeleteMultipleRelationshipProperties() {
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        Map<String, Object> rel1Properties = new HashMap<String, Object>() {{ put("key", "rel1");}};
        Map<String, Object> rel2Properties = new HashMap<String, Object>() {{ put("key", "rel2");}};

        db.addRelationship("LOVES", "Node", "one", "Node", "two", rel1Properties);
        db.addRelationship("LOVES", "Node", "one", "Node", "two", rel2Properties);
        db.deleteRelationshipProperties("LOVES", "Node", "one", "Node", "two", 2);

        Map<String, Object> actual = db.getRelationship("LOVES", "Node", "one", "Node", "two", 2);
        Assert.assertEquals(  new HashMap<String, Object>() {{
            put("_incoming_node_id", 2);
            put("_outgoing_node_id", 3);
        }}, actual);
    }

    @Test
    public void shouldUpdateRelationshipProperty() {
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        Map<String, Object> rel1Properties = new HashMap<String, Object>() {{ put("key", "rel1");}};
        Map<String, Object> rel2Properties = new HashMap<String, Object>() {{
            put("key", "rel2");
            put("_incoming_node_id", 2);
            put("_outgoing_node_id", 3);
        }};

        db.addRelationship("LOVES", "Node", "one", "Node", "two", rel1Properties);
        db.updateRelationshipProperty("LOVES", "Node", "one", "Node", "two", "key", "rel2");

        Map<String, Object> actual = db.getRelationship("LOVES", "Node", "one", "Node", "two");
        Assert.assertEquals(rel2Properties, actual);
    }

    @Test
    public void shouldUpdateMultipleRelationshipProperty() {
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        Map<String, Object> rel1Properties = new HashMap<String, Object>() {{ put("key", "rel1");}};
        Map<String, Object> rel2Properties = new HashMap<String, Object>() {{ put("key", "rel2");}};

        db.addRelationship("LOVES", "Node", "one", "Node", "two", rel1Properties);
        db.addRelationship("LOVES", "Node", "one", "Node", "two", rel2Properties);
        db.updateRelationshipProperty("LOVES", "Node", "one", "Node", "two", 2,"key", "rel2");

        Map<String, Object> actual = db.getRelationship("LOVES", "Node", "one", "Node", "two", 2);
        Assert.assertEquals(rel2Properties, actual);
    }

    @Test
    public void shouldDeleteRelationshipProperty() {
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        Map<String, Object> rel1Properties = new HashMap<String, Object>() {{ put("key", "rel1"); put("key2", "rel2"); }};
        Map<String, Object> rel2Properties = new HashMap<String, Object>() {{
            put("key2", "rel2");
            put("_incoming_node_id", 2);
            put("_outgoing_node_id", 3);
        }};

        db.addRelationship("LOVES", "Node", "one", "Node", "two", rel1Properties);
        db.deleteRelationshipProperty("LOVES", "Node", "one", "Node", "two", "key");

        Map<String, Object> actual = db.getRelationship("LOVES", "Node", "one", "Node", "two");
        Assert.assertEquals(rel2Properties, actual);
    }

    @Test
    public void shouldDeleteMultipleRelationshipProperty() {
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        Map<String, Object> rel1Properties = new HashMap<String, Object>() {{ put("key", "rel1"); put("key2", "rel2"); }};
        Map<String, Object> rel2Properties = new HashMap<String, Object>() {{ put("key", "rel2"); put("key2", "rel2"); }};
        Map<String, Object> rel3Properties = new HashMap<String, Object>() {{
            put("key2", "rel2");
            put("_incoming_node_id", 2);
            put("_outgoing_node_id", 3);
        }};

        db.addRelationship("LOVES", "Node", "one", "Node", "two", rel1Properties);
        db.addRelationship("LOVES", "Node", "one", "Node", "two", rel2Properties);
        db.deleteRelationshipProperty("LOVES", "Node", "one", "Node", "two", 2, "key");

        Map<String, Object> actual = db.getRelationship("LOVES", "Node", "one", "Node", "two", 2);
        Assert.assertEquals(rel3Properties, actual);
    }

    @Test
    public void shouldUpdateRelationshipPropertyNotThere() {
        db.addNode("Node", "one");
        db.addNode("Node", "two");
        Map<String, Object> rel2Properties = new HashMap<String, Object>() {{
            put("key", "rel2");
            put("_incoming_node_id", 2);
            put("_outgoing_node_id", 3);
        }};

        db.addRelationship("LOVES", "Node", "one", "Node", "two");
        db.updateRelationshipProperty("LOVES", "Node", "one", "Node", "two", "key", "rel2");

        Map<String, Object> actual = db.getRelationship("LOVES", "Node", "one", "Node", "two");
        Assert.assertEquals(rel2Properties, actual);
    }

}
