package com.kynsof.audit.infrastructure.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.audit.application.service.IAuditRegisterService;
import com.kynsof.audit.domain.AuditRecord;
import com.kynsof.audit.domain.dto.AuditRegisterRecordDto;
import com.kynsof.audit.infrastructure.kafka.ProcedureAuditEventService;
import com.kynsof.share.core.domain.kafka.event.EventType;
import com.kynsof.share.utils.StaticSpringContext;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Mono;

import java.util.Objects;

public class AuditListener {

    private IAuditRegisterService service;
    private ProcedureAuditEventService procedure;

    @PostPersist
    public void afterPersist(Object object) {
        this.populateBeans();
        AuditRegisterRecordDto recordDto = service.getAuditRegisterService(object.getClass().getName());
        if (Objects.nonNull(recordDto) && recordDto.isAuditCreate()) {
            this.sendAuditRecord(object, EventType.CREATED);
        }
    }

    @PostUpdate
    public void afterUpdate(Object object) {
        this.populateBeans();
        AuditRegisterRecordDto recordDto = service.getAuditRegisterService(object.getClass().getName());
        if (Objects.nonNull(recordDto) && recordDto.isAuditUpdate()) {
            this.sendAuditRecord(object, EventType.UPDATED);
        }
    }

    @PostRemove
    public void afterDelete(Object object) {
        this.populateBeans();
        AuditRegisterRecordDto recordDto = service.getAuditRegisterService(object.getClass().getName());
        if (Objects.nonNull(recordDto) && recordDto.isAuditDelete()) {
            this.sendAuditRecord(object, EventType.DELETED);
        }
    }

    private void sendAuditRecord(Object object, EventType eventType) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            String data = objectMapper.writeValueAsString(object);
            ReactiveSecurityContextHolder.getContext().subscribe(securityContext -> {
                Authentication authentication = securityContext.getAuthentication();
                String user = authentication.getName();
                AuditRecord auditRecord = new AuditRecord(object.getClass().getName(), user, eventType.name(), data);
                procedure.send(auditRecord);
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    private void populateBeans() {
        service = StaticSpringContext.getBean(IAuditRegisterService.class);
        procedure = StaticSpringContext.getBean(ProcedureAuditEventService.class);
    }
}
