package com.kynsoft.finamer.audit.infrastructure.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.audit.application.command.audit.create.CreateAuditCommand;
import com.kynsoft.finamer.audit.domain.dto.AuditRecordDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerAuditEventService {
    private final IMediator mediator;

    public ConsumerAuditEventService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "audit-topic", groupId = "audit", containerFactory = "kafkaListenerContainerFactory")
    public void consumer(String event) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            AuditRecordDto auditRecordDto = objectMapper.readValue(event, AuditRecordDto.class);
            CreateAuditCommand createAuditCommand = new CreateAuditCommand(auditRecordDto.getEntityName(),
                    auditRecordDto.getUsername(),
                    auditRecordDto.getAction(),
                    auditRecordDto.getAction(),
                    auditRecordDto.getTag()
                    );
            mediator.send(createAuditCommand);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerAuditEventService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
