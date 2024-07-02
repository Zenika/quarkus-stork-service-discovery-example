package com.zenika.quarkus.example;

import java.time.Instant;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.UriBuilder;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.consul.CheckOptions;
import io.vertx.ext.consul.CheckStatus;
import io.vertx.ext.consul.ConsulClientOptions;
import io.vertx.ext.consul.ServiceOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.consul.ConsulClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import static java.lang.Boolean.TRUE;

@ApplicationScoped
public class DiscoveryRegistration {

    private static final Logger LOGGER = Logger.getLogger(DiscoveryRegistration.class);
    public static final String SERVICE_ID = UUID.randomUUID().toString();
    private final ConsulClient consulClient;
    private final ServiceOptions serviceOptions;

    @Inject
    DiscoveryRegistration(@ConfigProperty(name = "service-registrar.host", defaultValue = "localhost") String consulHost,
                          @ConfigProperty(name = "service-registrar.port", defaultValue = "8500") int consulPort,
                          @ConfigProperty(name = "quarkus.http.host") String applicationHost,
                          @ConfigProperty(name = "quarkus.http.port") int applicationPort,
                          @ConfigProperty(name = "quarkus.application.name") String serviceName,
                          Vertx vertx) {
        LOGGER.infof("service registrar is available at http://%s:%d", consulHost, consulPort);
        var consulClientOptions = new ConsulClientOptions().setHost(consulHost).setPort(consulPort);
        consulClient = ConsulClient.create(vertx, consulClientOptions);
        var host = applicationHost.equals("0.0.0.0") ? "localhost" : applicationHost;
        var healthUri = UriBuilder.fromResource(HealthCheckResource.class)
                .scheme("http")
                .host(host)
                .port(applicationPort)
                .build();
        var checkOption = new CheckOptions()
                .setId("httpServiceCheck-%s".formatted(SERVICE_ID))
                .setName("discovered-service-check")
                .setInterval("1s")
                .setDeregisterAfter("5s")
                .setStatus(CheckStatus.PASSING)
                .setNotes("some notes")
                .setHttp(healthUri.toString());
        serviceOptions = new ServiceOptions()
                .setId(SERVICE_ID)
                .setName(serviceName)
                //.setAddress(host)
                .setPort(applicationPort)
                .setCheckOptions(checkOption);
    }

    public void register(@Observes StartupEvent event) {
        consulClient.registerService(serviceOptions)
                .subscribe()
                .with(
                        unused -> LOGGER.infof("service with id %s registered at %s.", SERVICE_ID, Instant.now()),
                        failure -> LOGGER.errorf("service with id %s failed to register", SERVICE_ID, failure)
                );
    }

    public void deregister(@Observes ShutdownEvent event) {
        consulClient.deregisterServiceAndAwait(SERVICE_ID);
        LOGGER.infof("service with id %s deregistered at %s.", SERVICE_ID, Instant.now());
    }

    @Scheduled(every = "3s")
    public void reconnect() {
        consulClient.catalogServiceNodes(serviceOptions.getName())
                .flatMap(serviceList -> {
                    if (serviceList.getList().stream().anyMatch(service -> service.getId().equals(SERVICE_ID))) {
                        return Uni.createFrom().item(false);
                    }
                    return consulClient.registerService(serviceOptions).map(unused -> true);
                })
                .subscribe()
                .with(
                        reconnected -> {
                            if(TRUE.equals(reconnected)) {
                                LOGGER.infof("service with id %s reconnected at %s.", SERVICE_ID, Instant.now());
                            }
                        },
                        failure -> LOGGER.errorf("service with id %s failed to reconnect", SERVICE_ID, failure)
                );
    }

}
