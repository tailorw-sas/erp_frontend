package com.kynsof.audit.infrastructure.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kynsof.audit.domain.EventType;
import com.kynsof.audit.infrastructure.core.UserContext;
import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.identity.kafka.AuditKafka;
import com.kynsof.audit.infrastructure.service.kafka.ProducerAuditEventService;
import com.kynsof.audit.infrastructure.utils.SpringContext;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuditEntityListener {
    private Logger logger = LoggerFactory.getLogger(AuditEntityListener.class);

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
        String username = UserContext.getCurrentUser();
        try {
            String data = objectMapper.writeValueAsString(object);
            ProducerAuditEventService procedure = SpringContext.getBean(ProducerAuditEventService.class);
            AuditKafka auditKafka = new AuditKafka();
            auditKafka.setData(data);
            RemoteAudit remoteAudit = object.getClass().getAnnotation(RemoteAudit.class);
            auditKafka.setAuditRegisterId(remoteAudit.id());
            auditKafka.setEntityName(remoteAudit.name());
            auditKafka.setAction(eventType.name());
            auditKafka.setTime(LocalDateTime.now());
            auditKafka.setUsername(username);
            procedure.send(auditKafka);

        } catch (JsonProcessingException e) {
           logger.error("Ha ocurrido un error al procesar la entidad");
        }

    }

}
