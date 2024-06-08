package com.kynsoft.gateway.infrastructure.config;

import com.kynsoft.gateway.domain.dto.RouteDTO;
import lombok.AllArgsConstructor;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ApiSwaggerResources implements SwaggerResourcesProvider {
	
	private final UpdateRouteContext updateRouteContext;

    @Override
    public List<SwaggerResource> get() {
    	
        List<SwaggerResource> resources = new ArrayList<>();
        
        resources.add(swaggerResource("gateway", "/v3/api-docs", "3.0"));
        
        for (RouteDTO route : updateRouteContext.getDefinitionsContext().getDefinitions()) {
            if (!route.getName().equalsIgnoreCase("config-service")) {
                resources.add(swaggerResource(route.getName(), route.getUri() + "/v3/api-docs", "3.0"));
            }
        }
        
        return resources;
    }

    private SwaggerResource swaggerResource(String name, String location, String version) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion(version);
        return swaggerResource;
    }

    
}