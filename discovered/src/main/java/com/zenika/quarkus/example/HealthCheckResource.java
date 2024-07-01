package com.zenika.quarkus.example;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/health")
public class HealthCheckResource {

    @GET
    public Response alive() {
        return Response.noContent().build();
    }

}
