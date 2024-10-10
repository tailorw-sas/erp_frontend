package com.kynsof.audit.infrastructure;

import com.kynsof.audit.application.service.IAuditRegisterService;
import com.kynsof.audit.domain.dto.AuditRegisterDto;
import com.kynsof.audit.infrastructure.listener.AuditListener;
import jakarta.persistence.Entity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;


@Component
public class AuditLoader {
    @Value("${audit.enabled}")
    private boolean auditEnable;
    private final ApplicationContext applicationContext;

    private final IAuditRegisterService service;

    public AuditLoader(ApplicationContext applicationContext, IAuditRegisterService service) {
        this.applicationContext = applicationContext;
        this.service = service;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void registerEntities() {
        Map<String,Object> entities = applicationContext.getBeansWithAnnotation(Entity.class);
        if (!entities.isEmpty()){
            entities.forEach((key, value) -> {
                AuditRegisterDto registerDto = new AuditRegisterDto( value.getClass().getName());
                service.registerEntity(registerDto);
            } );
        }

    }
}

