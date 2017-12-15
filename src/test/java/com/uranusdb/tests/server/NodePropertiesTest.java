package com.uranusdb.tests.server;

import com.uranusdb.server.UranusServer;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static com.uranusdb.server.UranusServer.graphs;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

public class NodePropertiesTest {
    static UranusServer server;

    @Before
    public void setup() throws Exception {
        Config conf = ConfigFactory.load("uranus");
        server = new UranusServer(conf);
        server.buildAndStartServer(conf);
        for (int i = -1; ++i < graphs.length; ) {
            HashMap<String, Object> property = new HashMap<String, Object>() {{ put("property", "Value"); }};
            HashMap<String, Object> props =  new HashMap<String, Object>(){{
                put("city", "Chicago");
                put("prop", new HashMap<String, Object>() {{ put("property", "Value"); }});
            }};
            HashMap<String, Object> properties = new HashMap<String, Object>() {{
                put("name", "Max"); put("age", 37);
            }};

            graphs[i].addNode("Node", "node1", properties);
            graphs[i].addNode("Node", "emptyNode");
            graphs[i].addNode("Node", "singlePropertyNode", property);
            graphs[i].addNode("Node", "complexPropertiesNode", props);
        }
    }

    @After
    public void shutdown() {
        server.stopServer();
    }

    @Test
    public void integrationTestGetNodePropertyNotThere() {
        when().
                get("/db/node/Node/node0/property/not_there").
                then().
                assertThat().
                statusCode(404);
    }

    @Test
    public void integrationTestGetNodePropertyNotThereInvalidProperty() {
        when().
                get("/db/node/Node/node1/property/not_there").
                then().
                assertThat().
                statusCode(404);
    }

    @Test
    public void integrationTestGetNodeProperty() {
        when().
                get("/db/node/Node/node1/property/name").
                then().
                assertThat().
                body(equalTo("\"Max\"")).
                statusCode(200).
                contentType("application/json");
    }

    @Test
    public void integrationTestGetNodeIntegerProperty() {
        when().
                get("/db/node/Node/node1/property/age").
                then().
                assertThat().
                body(equalTo("37")).
                statusCode(200).
                contentType("application/json");
    }

    @Test
    public void integrationTestPutNodePropertyNotThere() {
        given().
                body(200).
        when().
                put("/db/node/Node/node0/property/not_there").
                then().
                assertThat().
                statusCode(404);
    }

    @Test
    public void integrationTestPutNodeProperty() {
        HashMap<String, Object> prop = new HashMap<>();
        prop.put("weight", 200);

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("name", "Max");
        properties.put("age", 37);
        properties.put("weight", 200);

        given().
                contentType("application/json").
                body(200).
                when().
                put("/db/node/Node/node1/property/weight").
                then().
                assertThat().
                statusCode(204);
    }

    @Test
    public void integrationTestPutNodePropertyEmptyBody() {

        given().
                contentType("application/json").
                when().
                put("/db/node/Node/node1/property/weight").
                then().
                assertThat().
                statusCode(304);
    }

    @Test
    public void integrationTestPutNodeProperties() {
        HashMap<String, Object> prop = new HashMap<>();
        prop.put("weight", 220);
        prop.put("age", 38);

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("name", "Max");
        properties.put("age", 38);
        properties.put("weight", 220);

        given().
                contentType("application/json").
                body(prop).
                when().
                put("/db/node/Node/node1/properties").
                then().
                assertThat().
                body("$", equalTo(properties)).
                statusCode(201).
                contentType("application/json");
    }

    @Test
    public void integrationTestPutNodePropertiesEmpty() {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("name", "Max");
        properties.put("age", 37);

        given().
                contentType("application/json").
                when().
                put("/db/node/Node/node1/properties").
                then().
                assertThat().
                body("$", equalTo(properties)).
                statusCode(201).
                contentType("application/json");
    }

    @Test
    public void integrationTestDeleteNodePropertyNotThere() {
        when().
                delete("/db/node/Node/node0/property/not_there").
                then().
                assertThat().
                statusCode(404);
    }

    @Test
    public void integrationTestDeleteNodeProperty() {
        when().
                delete("/db/node/Node/node1/property/name").
                then().
                assertThat().
                statusCode(204);
    }

    @Test
    public void integrationTestDeleteNodeInvalidProperty() {
        when().
                delete("/db/node/Node/node1/property/not_there").
                then().
                assertThat().
                statusCode(204);
    }

    @Test
    public void integrationTestDeleteNodePropertiesNotThere() {
        when().
                delete("/db/node/Node/notThere/properties").
                then().
                assertThat().
                statusCode(404);
    }

    @Test
    public void integrationTestDeleteNodeProperties() {
        when().
                delete("/db/node/Node/node1/properties").
                then().
                assertThat().
                statusCode(204);
    }
}
