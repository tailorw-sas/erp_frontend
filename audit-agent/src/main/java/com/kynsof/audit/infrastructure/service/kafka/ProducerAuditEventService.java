package com.kynsof.audit.infrastructure.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kynsof.audit.infrastructure.identity.kafka.AuditKafka;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ProducerAuditEventService {
    @Value("${spring.application.name}")
    private String serviceName;


    private final KafkaTemplate<String, Object> producer;
    private final ObjectMapper objectMapper;


    public ProducerAuditEventService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Retryable(value = Exception.class, maxAttempts = 5, backoff = @Backoff(delay = 3000))
    public void send(AuditKafka auditKafka) {
        try {
            auditKafka.setServiceName(serviceName);
            String json = objectMapper.writeValueAsString(auditKafka);
            this.producer.send("audit-topic", json);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }

    }

}
