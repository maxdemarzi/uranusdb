package com.uranusdb.tests.server;

import com.uranusdb.server.UranusServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.when;

public class NoOpTest {
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
    public void integrationTestGetNoOp() {
        when().
                get("/db/noop").
                then().
                assertThat().
                statusCode(200);
    }
}
