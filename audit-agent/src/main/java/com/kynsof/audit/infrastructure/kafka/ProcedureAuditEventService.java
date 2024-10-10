package com.kynsof.audit.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.audit.domain.AuditRecord;
import com.kynsof.audit.domain.kafka.AuditKafka;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProcedureAuditEventService {

    private final KafkaTemplate<String, Object> producer;

    public ProcedureAuditEventService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void send(AuditRecord auditRecord) {
        try {
        AuditKafka auditKafka = new AuditKafka(auditRecord.getEntityName(),
                auditRecord.getUsername(),
                auditRecord.getAction(),
                auditRecord.getData());
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(auditKafka);
        this.producer.send("audit-topic", json);
        } catch (JsonProcessingException e) {
             log.error(e.getMessage());
        }
    }
}
