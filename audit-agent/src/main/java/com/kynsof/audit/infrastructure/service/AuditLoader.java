package com.kynsof.audit.infrastructure.service;

import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.identity.kafka.AuditRegisterKafka;
import com.kynsof.audit.infrastructure.service.kafka.ProducerAuditRegisterEventService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.EntityType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;


@Service
public class AuditLoader {
//    @Value("${audit.register.id}")
//    private String auditRegisterId;
    @Value("${spring.application.name}")
    private String serviceName;
    private final ProducerAuditRegisterEventService producer;
    private final EntityManagerFactory entityManagerFactory;

    public AuditLoader(ProducerAuditRegisterEventService producer, EntityManagerFactory entityManagerFactory) {
        this.producer = producer;
        this.entityManagerFactory = entityManagerFactory;
    }


    @PostConstruct
    public void registerService() {
        Set<Class<?>> entities= entityManagerFactory.getMetamodel().getEntities()
                .stream()
                .map(EntityType::getJavaType)
                .collect(Collectors.toSet());

        entities.forEach(entity->{
            if (entity.isAnnotationPresent(RemoteAudit.class)){
                RemoteAudit remoteAudit = entity.getAnnotation(RemoteAudit.class);
                AuditRegisterKafka auditRegisterKafka = new AuditRegisterKafka(remoteAudit.id(),serviceName,remoteAudit.name());
                producer.send(auditRegisterKafka);
            }
        });
    }
}

