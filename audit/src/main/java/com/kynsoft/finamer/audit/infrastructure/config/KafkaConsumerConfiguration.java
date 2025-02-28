package com.kynsoft.finamer.audit.infrastructure.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class KafkaConsumerConfiguration {

    @Value("${KAFKA_BOOTSTRAP_ADDRESS}")
    private String boostrapServer;

    @Value("${KAFKA_GROUP_ID}")
    private String groupId;

    @Value("${KAFKA_SASL_USERNAME:user}")
    private String saslUsername;

    @Value("${KAFKA_SASL_PASSWORD:password}")
    private String saslPassword;

    List<String> profilesWithSasl = List.of("qa", "production");

    @Bean
    public ConsumerFactory<String, String> consumerFactory(Environment environment) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServer);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        //if(profileNeedsSasl(environment)){
            addSaslConfig(configProps, saslUsername, saslPassword);
        //}
        /*
        if (saslUsername != null && !saslUsername.isEmpty() && saslPassword != null && !saslPassword.isEmpty()) {
            addSaslConfig(configProps, saslUsername, saslPassword);
        }*/

        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    private void addSaslConfig(Map<String, Object> props, String username, String password) {
        props.put("security.protocol", "SASL_PLAINTEXT");
        props.put(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-256");
        props.put("sasl.jaas.config", String.format("org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";", username, password));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(Environment environment) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(environment));
        return factory;
    }

    private boolean profileNeedsSasl(Environment environment){
        List<String> activeProfiles = Arrays.asList(environment.getActiveProfiles());
        return activeProfiles.stream().anyMatch(profilesWithSasl::contains);
    }
}
