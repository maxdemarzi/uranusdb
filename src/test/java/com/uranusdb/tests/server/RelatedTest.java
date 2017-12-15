package com.uranusdb.tests.server;

import com.uranusdb.server.UranusServer;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static com.uranusdb.server.UranusServer.graphs;
import static io.restassured.RestAssured.when;

public class RelatedTest {
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
            graphs[i].addNode("Node", "node4");
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
    public void integrationTestGetRelatedNotThere() {
        when().
                get("/db/related/Node/node1/Node/node4").
                then().
                assertThat().
                statusCode(404);
    }

    @Test
    public void integrationTestGetRelated() {
        when().
                get("/db/related/Node/node1/Node/node2").
                then().
                assertThat().
                statusCode(200);
    }

    @Test
    public void integrationTestGetRelatedByType() {
        when().
                get("/db/related/FOLLOWS/Node/node1/Node/node2").
                then().
                assertThat().
                statusCode(200);
    }

    @Test
    public void integrationTestGetRelatedByTypeBadRelationship() {
        when().
                get("/db/related/LIKES/Node/node1/Node/node2").
                then().
                assertThat().
                statusCode(404);
    }
}
