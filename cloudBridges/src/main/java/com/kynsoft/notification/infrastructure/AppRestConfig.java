package com.kynsoft.notification.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppRestConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}