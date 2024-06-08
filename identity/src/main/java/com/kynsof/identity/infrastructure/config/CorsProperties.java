package com.kynsof.identity.infrastructure.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "http")
public class CorsProperties {

	@Setter
    private boolean corsEnabled;
	private final CorsConfiguration cors = new CorsConfiguration();

}