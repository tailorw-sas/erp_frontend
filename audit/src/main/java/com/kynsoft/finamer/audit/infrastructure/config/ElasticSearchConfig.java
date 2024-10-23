package com.kynsoft.finamer.audit.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.elasticsearch.support.HttpHeaders;

import java.time.Duration;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.kynsoft.finamer.audit.infrastructure.repository.elastic")
public class ElasticSearchConfig extends ElasticsearchConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchConfig.class);

    @Value("${elasticsearch.url}")
    private String elasticSearchUrl;

    @Value("${elasticsearch.username}")
    private String elasticSearchUsername;

    @Value("${elasticsearch.password}")
    private String elasticSearchPassword;

    @Override
    public ClientConfiguration clientConfiguration() {
        logger.info("ElasticSearch URL: {}", elasticSearchUrl);
        logger.info("ElasticSearch Username: {}", elasticSearchUsername);
        // Por razones de seguridad, no se registra la contraseña.

        return ClientConfiguration.builder()
                .connectedTo(elasticSearchUrl)
                .withConnectTimeout(Duration.ofSeconds(5))
                .withSocketTimeout(Duration.ofSeconds(3))
                .withBasicAuth(elasticSearchUsername, elasticSearchPassword)
                .build();
    }
}