package com.zenika.quarkus.example;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
@TestHTTPEndpoint(DiscovererResource.class)
class DiscovererResourceTest {

    private static final String REST_CLIENT_RESPONSE = "a GET request has been done to `discovered` node with id : ";

    @InjectMock
    @RestClient
    DiscoveredRestClient discoveredRestClient;

    @BeforeEach
    void setUp() {
        when(discoveredRestClient.get())
                .thenReturn(REST_CLIENT_RESPONSE);
    }

    @Test
    void testHelloEndpoint() {
        given()
        .when()
            .get()
        .then()
            .statusCode(200)
            .body(is(REST_CLIENT_RESPONSE));
        verify(discoveredRestClient).get();
    }

}