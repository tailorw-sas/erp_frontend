package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageAttachmentType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageAttachmentTypeKafka;

import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageAttachmentType.create.CreateManageAttachmentTypeCommand;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageAttachmentService {

    private final IMediator mediator;

    public ConsumerReplicateManageAttachmentService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-attachment-type", groupId = "invoicing-entity-replica")
    public void listen(ReplicateManageAttachmentTypeKafka objKafka) {
        try {

            CreateManageAttachmentTypeCommand command = new CreateManageAttachmentTypeCommand(objKafka.getId(),
                    objKafka.getCode(),
                    objKafka.getName());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageAttachmentService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
