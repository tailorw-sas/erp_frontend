package com.kynsoft.gateway.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RoutesConfig {
    
	private final UpdateRouteContext updateRouteContext;

	
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder routeLocatorBuilder) {
      return new ApiRouteLocator(updateRouteContext, routeLocatorBuilder);
    }
   
}