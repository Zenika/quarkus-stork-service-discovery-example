package com.zenika.quarkus.example;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.TEXT;

@QuarkusTest
@TestHTTPEndpoint(HealthCheckResource.class)
class HealthCheckResourceTest {

    @Test
    void testHealthCheckGet() {
        given()
            .contentType(TEXT)
        .when()
            .get()
        .then()
            .statusCode(204);
    }

}