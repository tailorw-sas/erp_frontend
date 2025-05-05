package com.kynsoft.finamer.insis.infrastructure.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setObjectMapper(objectMapper);
        List<MediaType> mediaTypes = new ArrayList<>(jsonConverter.getSupportedMediaTypes());
        mediaTypes.add(MediaType.APPLICATION_JSON);
        mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        mediaTypes.add(new MediaType("application", "*+json", StandardCharsets.UTF_8));
        jsonConverter.setSupportedMediaTypes(mediaTypes);

        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        restTemplate.getMessageConverters().add(1, jsonConverter);

        return restTemplate;
    }

    @Bean
    @LoadBalanced
    public WebClient.Builder getWebClient(){

        // Crea un ObjectMapper de Jackson que excluya los campos null y configura otras características necesarias
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper .registerModule(new JavaTimeModule());

        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .codecs(clientCodecConfigurer -> {
                    clientCodecConfigurer.defaultCodecs().jackson2JsonEncoder(new Jackson2CborEncoder(objectMapper, MediaType.APPLICATION_JSON,MediaType.APPLICATION_JSON_UTF8,
                            new MediaType("application", "*+json", StandardCharsets.UTF_8)));
                    clientCodecConfigurer.defaultCodecs().jackson2JsonDecoder(new Jackson2CborDecoder(objectMapper, MediaType.APPLICATION_JSON,MediaType.APPLICATION_JSON_UTF8,
                            new MediaType("application", "*+json", StandardCharsets.UTF_8)));
                });
    }
}
