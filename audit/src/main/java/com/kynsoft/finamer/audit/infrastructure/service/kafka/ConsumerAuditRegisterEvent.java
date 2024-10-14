package com.kynsoft.finamer.audit.infrastructure.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsoft.finamer.audit.application.command.configuration.create.CreateAuditRegisterCommand;
import com.kynsoft.finamer.audit.infrastructure.bus.IMediator;
import com.kynsoft.finamer.audit.infrastructure.identity.kafka.AuditRegisterKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerAuditRegisterEvent {
    private final IMediator mediator;

    public ConsumerAuditRegisterEvent(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "audit-register-topic", groupId = "audit", containerFactory = "kafkaListenerContainerFactory")
    public void consumer(String event) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            AuditRegisterKafka auditKafka = objectMapper.readValue(event, AuditRegisterKafka.class);
            CreateAuditRegisterCommand createAuditCommand=new CreateAuditRegisterCommand(UUID.fromString(auditKafka.getAuditRegisterId()), auditKafka.getServiceName()) ;
            mediator.send(createAuditCommand);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerAuditEventService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
