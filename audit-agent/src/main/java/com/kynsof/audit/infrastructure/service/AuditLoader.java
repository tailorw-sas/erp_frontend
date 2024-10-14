package com.kynsof.audit.infrastructure.service;

import com.kynsof.audit.infrastructure.identity.kafka.AuditRegisterKafka;
import com.kynsof.audit.infrastructure.service.kafka.ProducerAuditRegisterEventService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Service
public class AuditLoader {
    @Value("${audit.register.id}")
    private String auditRegisterId;
    @Value("${spring.application.name}")
    private String serviceName;
    private final ProducerAuditRegisterEventService service;

    public AuditLoader(ProducerAuditRegisterEventService service) {
        this.service = service;
    }


    @PostConstruct
    public void registerService() {
        AuditRegisterKafka registerDto = new AuditRegisterKafka(auditRegisterId,serviceName);
        service.send(registerDto);

    }
}

