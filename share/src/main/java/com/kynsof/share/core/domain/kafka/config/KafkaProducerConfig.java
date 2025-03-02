package com.kynsof.share.core.domain.kafka.config;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${KAFKA_BOOTSTRAP_ADDRESS:localhost:9092}")
    private String bootstrapAddress;

    @Value("${KAFKA_MAX_REQUEST_VALUE:104857600}")
    private String maxRequestSize;

    @Value("${KAFKA_SASL_USERNAME:user}")
    private String saslUsername;

    @Value("${KAFKA_SASL_PASSWORD:password}")
    private String saslPassword;

    @Bean
    public ProducerFactory<String, Object> defaultProducerFactory() {
        Map<String, Object> configProps = createBaseProps();
        addSaslConfig(configProps, saslUsername, saslPassword);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    private Map<String, Object> createBaseProps() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, maxRequestSize);
        return configProps;
    }

    private void addSaslConfig(Map<String, Object> props, String username, String password) {
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(SaslConfigs.SASL_JAAS_CONFIG, String.format("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";", username, password));

    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
