package com.zenika.quarkus.example;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "stork://discovered")
public interface DiscoveredRestClient {

    @GET
    @Path("/discovered")
    String get();

}
