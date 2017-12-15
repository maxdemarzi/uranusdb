package com.uranusdb.tests.server;

import com.uranusdb.server.UranusServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static com.uranusdb.server.UranusServer.graphs;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

public class NodeTest {
    static UranusServer server;

    @Before
    public void setup() throws Exception {
        server = new UranusServer();
        server.buildAndStartServer();
        HashMap<String, Object> property =  new HashMap<String, Object>() {{ put("property", "Value"); }};
        HashMap<String, Object> props =  new HashMap<>();
        props.put("city", "Chicago");
        props.put("prop", property);

        for (int i = -1; ++i < graphs.length; ) {
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
    public void integrationTestGetNodeNotThere() {
        when().
                get("/db/node/Node/notThere").
                then().
                assertThat().
                statusCode(404);
    }

    @Test
    public void integrationTestGetEmptyNode() {
        when().
                get("/db/node/Node/emptyNode").
                then().
                assertThat()
                .body(equalTo("{}"))
                .statusCode(200)
                .contentType("application/json");
    }

    @Test
    public void integrationTestGetSinglePropertyNode() {
        HashMap<String, Object> prop =  new HashMap<>();
        prop.put("property", "Value");

        when().
                get("/db/node/Node/singlePropertyNode").
                then().
                assertThat()
                .body("$", equalTo(prop))
                .statusCode(200)
                .contentType("application/json");
    }

    @Test
    public void integrationTestCreateEmptyNode() {
        given().
                contentType("application/json;charset=UTF-8").
                body("{}").
                when().
                post("/db/node/Node/emptyNode").
                then().
                assertThat().
                body("$", equalTo(new HashMap<>())).
                statusCode(201).
                contentType("application/json");
    }

    @Test
    public void integrationTestCreateSinglePropertyNode() {
        HashMap<String, Object> prop =  new HashMap<>();
        prop.put("property", "Value");

        given().
                contentType("application/json;charset=UTF-8").
                body(prop).
                when().
                post("/db/node/Node/singlePropertyNode").
                then().
                assertThat().
                body("$", equalTo(prop)).
                statusCode(201).
                contentType("application/json");
    }

    @Test
    public void integrationTestPutNodeNotThere() {
        when().
                put("/db/node/Node/notThere/properties").
                then().
                assertThat().
                statusCode(404);
    }

    @Test
    public void integrationTestUpdateSinglePropertyNode() {
        HashMap<String, Object> prop =  new HashMap<>();
        prop.put("name", "Value2");

        given().
                contentType("application/json;charset=UTF-8").
                body(prop).
                when().
                put("/db/node/Node/singlePropertyNode/property/name").
                then().
                assertThat().
                statusCode(204);
    }

    @Test
    public void integrationTestDeleteNodeNotThere() {
        when().
                delete("/db/node/Node/notThere").
                then().
                assertThat().
                statusCode(404);
    }

    @Test
    public void integrationTestDeleteEmptyNode() {
        when().
                delete("/db/node/Node/emptyNode").
                then().
                assertThat().
                statusCode(204);
    }
}
