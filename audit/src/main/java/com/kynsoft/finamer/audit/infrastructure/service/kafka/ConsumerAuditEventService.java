package com.kynsoft.finamer.audit.infrastructure.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kynsoft.finamer.audit.application.command.audit.create.CreateAuditCommand;
import com.kynsoft.finamer.audit.infrastructure.identity.kafka.AuditKafka;
import com.kynsoft.finamer.audit.infrastructure.bus.IMediator;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerAuditEventService {
    private final IMediator mediator;
    private ObjectMapper objectMapper;

    public ConsumerAuditEventService(IMediator mediator) {
        this.mediator = mediator;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @KafkaListener(topics = "audit-topic", groupId = "audit", containerFactory = "kafkaListenerContainerFactory")
    public void consumer(String event) {
      //  ObjectMapper objectMapper = new ObjectMapper();
        try {
            AuditKafka auditKafka = objectMapper.readValue(event, AuditKafka.class);
            CreateAuditCommand createAuditCommand = new CreateAuditCommand(auditKafka.getEntityName(),
                    auditKafka.getUsername(),
                    auditKafka.getAction(),
                    auditKafka.getData(),
                    auditKafka.getTag(),
                    auditKafka.getTime(),
                    auditKafka.getServiceName(),
                    UUID.fromString(auditKafka.getAuditRegisterId()));
            mediator.send(createAuditCommand);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerAuditEventService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
