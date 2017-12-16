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

public class RelationshipPropertiesTest {
    static UranusServer server;

    @Before
    public void setup() throws Exception {
        Config conf = ConfigFactory.load("uranus");
        server = new UranusServer(conf);
        server.buildAndStartServer(conf);

        for (int i = -1; ++i < graphs.length; ) {
            HashMap<String, Object> properties = new HashMap<>();
            properties.put("stars", 5);
            properties.put("since", "2017-01-07");

            graphs[i].addNode("Node", "node1");
            graphs[i].addNode("Node", "node2");
            graphs[i].addNode("Node", "node3");
            graphs[i].addRelationship("FOLLOWS", "Node", "node1", "Node", "node2");
            graphs[i].addRelationship("FOLLOWS", "Node", "node1", "Node", "node3", properties);
            graphs[i].addRelationship("FOLLOWS", "Node", "node1", "Node", "node3", properties);
        }
    }

    @After
    public void shutdown() {
        server.stopServer();
    }

    @Test
    public void integrationTestGetRelationshipPropertyNotThere() {
        when().
                get("/db/relationship/FOLLOWS/Node/node0/Node/node1/property/not_there").
                then().
                assertThat().
                statusCode(404);
    }

    @Test
    public void integrationTestGetRelationshipPropertyInvalidProperty() {
        when().
                get("/db/relationship/FOLLOWS/Node/node1/Node/node3/property/not_there").
                then().
                assertThat().
                statusCode(404);
    }

    @Test
    public void integrationTestGetRelationshipProperty() {
        when().
                get("/db/relationship/FOLLOWS/Node/node1/Node/node3/property/since").
                then().
                assertThat().
                body(equalTo("\"2017-01-07\"")).
                statusCode(200).
                contentType("application/json");
    }

    @Test
    public void integrationTestGetRelationshipIntegerProperty() {
        when().
                get("/db/relationship/FOLLOWS/Node/node1/Node/node3/property/stars").
                then().
                assertThat().
                body(equalTo("5")).
                statusCode(200).
                contentType("application/json");
    }

    @Test
    public void integrationTestGetRelationshipIntegerPropertyWithNumber() {
        when().
                get("/db/relationship/FOLLOWS/Node/node1/Node/node3/2/property/stars").
                then().
                assertThat().
                body(equalTo("5")).
                statusCode(200).
                contentType("application/json");
    }

    @Test
    public void integrationTestPutRelationshipPropertyNotThere() {
        given().
                contentType("application/json").
                body(2).
        when().
                put("/db/relationship/FOLLOWS/Node/node0/Node/node3/property/stars").
                then().
                assertThat().
                statusCode(404);
    }

    @Test
    public void integrationTestPutRelationshipProperty() {
        given().
                contentType("application/json").
                body(true).
                when().
                put("/db/relationship/FOLLOWS/Node/node1/Node/node3/property/archived").
                then().
                assertThat().
                statusCode(204);
    }

    @Test
    public void integrationTestPutEmptyRelationshipProperty() {
        given().
                contentType("application/json").
                when().
                put("/db/relationship/FOLLOWS/Node/node1/Node/node3/property/archived").
                then().
                assertThat().
                statusCode(304);
    }

    @Test
    public void integrationTestPutRelationshipPropertyWithNumber() {
        given().
                contentType("application/json").
                body(true).
                when().
                put("/db/relationship/FOLLOWS/Node/node1/Node/node3/2/property/archived").
                then().
                assertThat().
                statusCode(204);
    }

    @Test
    public void integrationTestPutRelationshipProperties() {
        HashMap<String, Object> prop = new HashMap<>();
        prop.put("stars", 4);
        prop.put("archived", true);
        prop.put("_incoming_node_id", 0);
        prop.put("_outgoing_node_id", 2);

        given().
                contentType("application/json").
                body(prop).
                when().
                put("/db/relationship/FOLLOWS/Node/node1/Node/node3/properties").
                then().
                assertThat().
                body("$", equalTo(prop)).
                statusCode(201).
                contentType("application/json");
    }

    @Test
    public void integrationTestPutRelationshipPropertiesNotThere() {
        HashMap<String, Object> prop = new HashMap<>();
        prop.put("stars", 4);
        prop.put("archived", true);

        given().
                contentType("application/json").
                body(prop).
                when().
                put("/db/relationship/FOLLOWS/Node/node2/Node/node3/properties").
                then().
                assertThat().
                statusCode(404);
    }

    @Test
    public void integrationTestPutRelationshipPropertiesNodeNotThere() {
        HashMap<String, Object> prop = new HashMap<>();
        prop.put("stars", 4);
        prop.put("archived", true);

        given().
                contentType("application/json").
                body(prop).
                when().
                put("/db/relationship/FOLLOWS/Node/node2/Node/node4/properties").
                then().
                assertThat().
                statusCode(404);
    }

    @Test
    public void integrationTestPutRelationshipPropertiesEmpty() {
        given().
                contentType("application/json").
                when().
                put("/db/relationship/FOLLOWS/Node/node1/Node/node3/properties").
                then().
                assertThat().
                statusCode(201).
                contentType("application/json");
    }

    @Test
    public void integrationTestPutRelationshipPropertiesWithNumber() {
        HashMap<String, Object> prop = new HashMap<>();
        prop.put("stars", 4);
        prop.put("archived", true);
        prop.put("_incoming_node_id", 0);
        prop.put("_outgoing_node_id", 2);

        given().
                contentType("application/json").
                body(prop).
                when().
                put("/db/relationship/FOLLOWS/Node/node1/Node/node3/2/properties").
                then().
                assertThat().
                body("$", equalTo(prop)).
                statusCode(201).
                contentType("application/json");
    }

    @Test
    public void integrationTestDeleteRelationshipPropertyNotThere() {
        when().
                delete("/db/relationship/FOLLOWS/Node/node0/Node/node1/property/not_there").
                then().
                assertThat().
                statusCode(404);
    }

    @Test
    public void integrationTestDeleteRelationshipProperty() {
        when().
                delete("/db/relationship/FOLLOWS/Node/node1/Node/node3/property/since").
                then().
                assertThat().
                statusCode(204);
    }

    @Test
    public void integrationTestDeleteRelationshipPropertyWithNumber() {
        when().
                delete("/db/relationship/FOLLOWS/Node/node1/Node/node3/2/property/since").
                then().
                assertThat().
                statusCode(204);
    }

    @Test
    public void integrationTestDeleteRelationshipPropertyInvalidProperty() {
        when().
                delete("/db/relationship/FOLLOWS/Node/node1/Node/node3/property/not_there").
                then().
                assertThat().
                statusCode(404);
    }

    @Test
    public void integrationTestDeleteRelationshipProperties() {
        when().
                delete("/db/relationship/FOLLOWS/Node/node1/Node/node3/properties").
                then().
                assertThat().
                statusCode(204);
    }

    @Test
    public void integrationTestDeleteRelationshipPropertiesWithNumber() {
        when().
                delete("/db/relationship/FOLLOWS/Node/node1/Node/node3/2/properties").
                then().
                assertThat().
                statusCode(204);
    }
}
