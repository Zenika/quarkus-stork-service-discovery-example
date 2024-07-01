package com.zenika.quarkus.example;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
class DiscovererResourceTest {
    @Test
    void testHelloEndpoint() {
        given()
                .when().get("/discoverer")
                .then()
                .statusCode(200)
                .body(containsString("a GET request has been done to `discovered` node with id : "));
    }

}