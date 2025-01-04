package com.kynsof.audit.infrastructure.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.audit.infrastructure.identity.kafka.AuditRegisterKafka;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProducerAuditRegisterEventService {
    private final KafkaTemplate<String, Object> producer;
    ObjectMapper objectMapper = new ObjectMapper();


    public ProducerAuditRegisterEventService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Retryable(value = Exception.class,maxAttempts = 5,backoff = @Backoff(delay = 3000))
    public void send(AuditRegisterKafka auditRegisterKafka) {
        try {
            String json = objectMapper.writeValueAsString(auditRegisterKafka);
            this.producer.send("audit-register-topic", json);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }
}
