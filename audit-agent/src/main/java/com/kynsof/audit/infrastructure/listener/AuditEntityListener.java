package com.kynsof.audit.infrastructure.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kynsof.audit.domain.EventType;
import com.kynsof.audit.infrastructure.identity.kafka.AuditKafka;
import com.kynsof.audit.infrastructure.service.kafka.ProducerAuditEventService;
import com.kynsof.audit.infrastructure.utils.SpringContext;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuditEntityListener {


    private final ObjectMapper objectMapper;


    public AuditEntityListener() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @PostPersist
    public void afterPersist(Object object) {
        this.sendAuditRecord(object, EventType.CREATED);
    }

    @PostUpdate
    public void afterUpdate(Object object) {
        this.sendAuditRecord(object, EventType.UPDATED);

    }

    @PostRemove
    public void afterDelete(Object object) {
        this.sendAuditRecord(object, EventType.DELETED);
    }

    private void sendAuditRecord(Object object, EventType eventType) {
//         try {
   //     String data = objectMapper.writeValueAsString(object);
        ProducerAuditEventService procedure = SpringContext.getBean(ProducerAuditEventService.class);
        AuditKafka auditKafka = new AuditKafka();
  //      auditKafka.setData(data);
        auditKafka.setEntityName(object.getClass().getName());
        auditKafka.setAction(eventType.name());
        auditKafka.setTime(LocalDateTime.now());
        procedure.send(auditKafka);

//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//         }

    }

}
