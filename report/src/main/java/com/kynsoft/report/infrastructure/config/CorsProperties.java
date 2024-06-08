package com.kynsoft.report.infrastructure.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@ConfigurationProperties(prefix = "http")
public class CorsProperties {

	private boolean corsEnabled;
	private final CorsConfiguration cors = new CorsConfiguration();

	public boolean isCorsEnabled() {
		return corsEnabled;
	}

	public void setCorsEnabled(boolean corsEnabled) {
		this.corsEnabled = corsEnabled;
	}

	public CorsConfiguration getCors() {
		return cors;
	}

}