package com.kynsoft.gateway.infrastructure.config;

import com.kynsoft.gateway.domain.dto.RouteDTO;
import lombok.AllArgsConstructor;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import reactor.core.publisher.Flux;

@AllArgsConstructor
public class ApiRouteLocator implements RouteLocator {

    private final UpdateRouteContext updateRouteContext;

    private final RouteLocatorBuilder routeLocatorBuilder;


    @Override
    public Flux<Route> getRoutes() {
        RouteLocatorBuilder.Builder routesBuilder = routeLocatorBuilder.routes();

        for (RouteDTO route : updateRouteContext.getDefinitionsContext().getDefinitions()) {

            if (!route.getName().equalsIgnoreCase("config-service")) {
                routesBuilder.route(route.getName(),
                        r -> r.path(route.getPath())
                                .filters(f ->
                                        f.stripPrefix(1)
                                                .dedupeResponseHeader("Access-Control-Allow-Credentials", "RETAIN_FIRST")
                                                .dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_FIRST")
                                                .dedupeResponseHeader("Access-Control-Request-Headers", "RETAIN_FIRST"))
                                .uri(route.getUri()));
            }
        }

        return routesBuilder.build().getRoutes();
    }

}