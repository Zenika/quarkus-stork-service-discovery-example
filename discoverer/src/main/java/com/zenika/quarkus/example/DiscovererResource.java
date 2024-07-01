package com.zenika.quarkus.example;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/discoverer")
public class DiscovererResource {

    public final DiscoveredRestClient discoveredRestClient;

    @Inject
    DiscovererResource(@RestClient DiscoveredRestClient discoveredRestClient) {
        this.discoveredRestClient = discoveredRestClient;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String get() {
        return discoveredRestClient.get();
    }
}
