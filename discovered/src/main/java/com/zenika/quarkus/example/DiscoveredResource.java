package com.zenika.quarkus.example;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import static com.zenika.quarkus.example.DiscoveryRegistration.SERVICE_ID;
import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;

@Path("/discovered")
public class DiscoveredResource {

    @GET
    @Produces(TEXT_PLAIN)
    public String get() {
        return "a GET request has been done to `discovered` node with id : " + SERVICE_ID;
    }

}
