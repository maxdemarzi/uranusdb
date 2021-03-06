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

public class RelationshipTest {
    static UranusServer server;

    @Before
    public void setup() throws Exception {
        Config conf = ConfigFactory.load("uranus");
        server = new UranusServer(conf);
        server.buildAndStartServer(conf);

        for (int i = -1; ++i < graphs.length; ) {
            HashMap<String, Object> properties = new HashMap<>();
            properties.put("stars", 5);
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
    public void integrationTestGetRelationshipNotThere() {
        when().
                get("/db/relationship/FOLLOWS/Node/node0/Node/node1").
                then().
                assertThat().
                statusCode(404);
    }

    @Test
    public void integrationTestGetEmptyRelationship() {

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("~incoming_node_id", 0);
        properties.put("~outgoing_node_id", 1);
        properties.put("~id", 0);
        properties.put("~type", "FOLLOWS");

        when().
                get("/db/relationship/FOLLOWS/Node/node1/Node/node2").
                then().
                assertThat().
                body("$", equalTo(properties)).
                statusCode(200).
                contentType("application/json");
    }

    @Test
    public void integrationTestGetRelationshipWithNumber() {
        HashMap<String, Object> properties =  new HashMap<>();
        properties.put("stars", 5);
        properties.put("~incoming_node_id", 0);
        properties.put("~outgoing_node_id", 2);
        properties.put("~id", 2);
        properties.put("~count", 2);
        properties.put("~type", "FOLLOWS");

        when().
                get("/db/relationship/FOLLOWS/Node/node1/Node/node3/2").
                then().
                assertThat().
                body("$", equalTo(properties)).
                statusCode(200);
    }

    @Test
    public void integrationTestGetSinglePropertyRelationship() {
        HashMap<String, Object> properties =  new HashMap<>();
        properties.put("stars", 5);
        properties.put("~incoming_node_id", 0);
        properties.put("~outgoing_node_id", 2);
        properties.put("~id", 2);
        properties.put("~count", 2);
        properties.put("~type", "FOLLOWS");

        when().
                get("/db/relationship/FOLLOWS/Node/node1/Node/node3").
                then().
                assertThat().
                body("$", equalTo(properties)).
                statusCode(200).
                contentType("application/json");
    }

    @Test
    public void integrationTestCreateEmptyRelationship() {
        HashMap<String, Object> properties =  new HashMap<>();
        properties.put("~incoming_node_id", 1);
        properties.put("~outgoing_node_id", 0);
        properties.put("~id", 3);
        properties.put("~type", "FOLLOWS");

        given().
                contentType("application/json").
                when().
                post("/db/relationship/FOLLOWS/Node/node2/Node/node1").
                then().
                assertThat().
                body("$", equalTo(properties)).
                statusCode(201).
                contentType("application/json");
    }

    @Test
    public void integrationTestCreateSinglePropertyRelationship() {
        HashMap<String, Object> prop =  new HashMap<>();
        prop.put("stars", 5);
        prop.put("~incoming_node_id", 0);
        prop.put("~outgoing_node_id", 2);

        HashMap<String, Object> properties =  new HashMap<>();
        properties.put("stars", 5);
        properties.put("~incoming_node_id", 0);
        properties.put("~outgoing_node_id", 2);
        properties.put("~id", 2);
        properties.put("~count", 2);
        properties.put("~type", "FOLLOWS");

        given().
                contentType("application/json").
                body(prop).
                when().
                post("/db/relationship/FOLLOWS/Node/node1/Node/node3").
                then().
                assertThat().
                body("$", equalTo(properties)).
                statusCode(201).
                contentType("application/json");
    }
    @Test
    public void integrationTestDeleteRelationshipNotThere() {
        when().
                delete("/db/relationship/NOT_THERE/Node/node0/Node/node1").
                then().
                assertThat().
                statusCode(404);
    }

    @Test
    public void integrationTestDeleteRelationship() {
        when().
                delete("/db/relationship/FOLLOWS/Node/node1/Node/node2").
                then().
                assertThat().
                statusCode(204);
    }

    @Test
    public void integrationTestDeleteRelationshipWithNumber() {
        when().
                delete("/db/relationship/FOLLOWS/Node/node1/Node/node2/1").
                then().
                assertThat().
                statusCode(204);
    }
}
