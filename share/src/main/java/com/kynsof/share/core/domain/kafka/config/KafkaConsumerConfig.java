package com.kynsof.share.core.domain.kafka.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${KAFKA_BOOTSTRAP_ADDRESS:localhost:9092}")
    private String bootstrapAddress;

    @Value("${KAFKA_GROUP_ID:group-id}")
    private String groupId;

    @Value("${KAFKA_SASL_USERNAME:user}")
    private String saslUsername;

    @Value("${KAFKA_SASL_PASSWORD:password}")
    private String saslPassword;

    List<String> profilesWithSasl = List.of("qa", "production");

    @Bean
    public ConsumerFactory<String, Object> consumerFactory(Environment environment) {
        Map<String, Object> configProps = createBaseProps();
        if(profileNeedsSasl(environment)){
            addSaslConfig(configProps, saslUsername, saslPassword);
        }

        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    private boolean profileNeedsSasl(Environment environment){
        List<String> activeProfiles = Arrays.asList(environment.getActiveProfiles());
        return activeProfiles.stream().anyMatch(profilesWithSasl::contains);
    }

//    public ConsumerFactory<String, Object> defaultConsumerFactory() {
//        System.out.println("************** Ingreso a defaultConsumerFactory sin SASL");
//        Map<String, Object> configProps = createBaseProps();
        // Verificar si SASL es requerido
//        if (saslUsername != null && !saslUsername.isEmpty() && saslPassword != null && !saslPassword.isEmpty()) {
//            addSaslConfig(configProps, saslUsername, saslPassword);
//        } else {
//            // Asegúrate de que no esté configurado ningún protocolo de seguridad si no se requiere
//            configProps.remove("security.protocol");
//            configProps.remove("sasl.mechanism");
//            configProps.remove("sasl.jaas.config");
//        }
//        configProps.remove("security.protocol");
//        configProps.remove("sasl.mechanism");
//        configProps.remove("sasl.jaas.config");
//        return new DefaultKafkaConsumerFactory<>(configProps);
//    }

    private Map<String, Object> createBaseProps() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class.getName());
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class.getName());
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        configProps.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class.getName());
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return configProps;
    }

    private void addSaslConfig(Map<String, Object> props, String username, String password) {
        props.put("security.protocol", "SASL_PLAINTEXT");
        props.put(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-256");
        props.put("sasl.jaas.config", String.format("org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";", username, password));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(Environment environment) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(environment));
        return factory;
    }
}