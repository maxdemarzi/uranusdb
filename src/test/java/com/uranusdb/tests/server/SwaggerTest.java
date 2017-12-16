package com.uranusdb.tests.server;

import com.uranusdb.server.UranusServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.when;

public class SwaggerTest {
    static UranusServer server;

    @Before
    public void setup() throws Exception {
        server = new UranusServer();
        server.buildAndStartServer();
    }

    @After
    public void shutdown() {
        server.stopServer();
    }

    @Test
    public void integrationTestGetUranusDBYAML() {
        when().
                get("/swagger/uranusdb.yaml").
                then().
                assertThat().
                statusCode(200);
    }

    @Test
    public void integrationTestGetSwaggerUI() {
        when().
                get("/swagger").
                then().
                assertThat().
                statusCode(200);
    }
}
