package com.kynsoft.gateway.infrastructure.config;

import com.kynsoft.gateway.domain.dto.RouteDTO;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties.SwaggerUrl;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.Ordered;

import java.util.HashSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig implements ApplicationListener<RefreshRoutesEvent> ,Ordered  {
	
	private final UpdateRouteContext updateDefinitionsContext;
	
	private final SwaggerUiConfigProperties cachedSwaggerUiConfig;
	
    @Bean
    @Scope(scopeName=ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Set<SwaggerUrl> loadSwaggerUrls() {
    	
       Set<SwaggerUrl> urls = new HashSet<>();
    	
    	urls.add(new SwaggerUrl("gateway", "/v3/api-docs", "gateway"));
    	
    	for (RouteDTO route : updateDefinitionsContext.getDefinitionsContext().getDefinitions()) {
    		urls.add(new SwaggerUrl(route.getName(), route.getUri() + "/v3/api-docs", route.getName()));
    	}
    	
    	cachedSwaggerUiConfig.setUrls(null);
    	cachedSwaggerUiConfig.setUrls(urls);
    	return urls;	
    }

	@Override
	public int getOrder() {
		return 2;
	}

	@Override
	public void onApplicationEvent(RefreshRoutesEvent event) {
		this.loadSwaggerUrls();
	}

}