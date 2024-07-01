package com.zenika.quarkus.example;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static com.zenika.quarkus.example.DiscoveryRegistration.SERVICE_ID;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.TEXT;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class DiscoveredResourceTest {

    @Test
    void testDiscoveryGet() {
        given()
            .contentType(TEXT)
        .when()
            .get("/discovered")
        .then()
            .statusCode(200)
            .body(is("a GET request has been done to `discovered` node with id : " + SERVICE_ID));


    }

}