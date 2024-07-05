package com.kynsoft.finamer.payment.infrastructure.services.kafka.consumer.manageAttachmentType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageAttachmentTypeKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.manageAttachmentType.create.CreateManageAttachmentTypeCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class CreateConsumerReplicateManageAttachmentTypeService {

    private final IMediator mediator;

    public CreateConsumerReplicateManageAttachmentTypeService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-attachment-type", groupId = "payment-entity-replica")
    public void listen(String event) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(event);

            ReplicateManageAttachmentTypeKafka objKafka = objectMapper.treeToValue(rootNode, ReplicateManageAttachmentTypeKafka.class);
            CreateManageAttachmentTypeCommand command = new CreateManageAttachmentTypeCommand(objKafka.getId(), objKafka.getCode(), objKafka.getName());
            mediator.send(command);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(CreateConsumerReplicateManageAttachmentTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
