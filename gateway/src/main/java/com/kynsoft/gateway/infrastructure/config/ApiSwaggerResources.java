package com.kynsoft.gateway.infrastructure.config;

import lombok.AllArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@AllArgsConstructor
public class ApiSwaggerResources {

    private final UpdateRouteContext updateRouteContext;

    @Bean
    public GroupedOpenApi gatewayApi() {
        return GroupedOpenApi.builder()
                .group("gateway")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public List<GroupedOpenApi> dynamicApis() {
        return updateRouteContext.getDefinitionsContext().getDefinitions().stream()
                .filter(route -> !route.getName().equalsIgnoreCase("config-service"))
                .map(route -> GroupedOpenApi.builder()
                        .group(route.getName())
                        .pathsToMatch(route.getUri() + "/**")
                        .build())
                .collect(Collectors.toList());
    }
}
