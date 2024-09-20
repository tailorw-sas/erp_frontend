package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageAttachmentType;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageAttachmentTypeKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageAttachmentType.update.UpdateManageAttachmentTypeCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateManageAttachmentType {

    private final IMediator mediator;

    public ConsumerUpdateManageAttachmentType(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-attachment-type", groupId = "invoicing-entity-replica")
    public void listen(UpdateManageAttachmentTypeKafka objKafka) {
        try {
            // ObjectMapper objectMapper = new ObjectMapper();
            // JsonNode rootNode = objectMapper.readTree(event);
            //
            // ReplicateAttachmentTypeKafka objKafka = objectMapper.treeToValue(rootNode,
            // ReplicateAttachmentTypeKafka.class);
            UpdateManageAttachmentTypeCommand command = new UpdateManageAttachmentTypeCommand(objKafka.getId(),
                    objKafka.getName(), objKafka.getStatus(), objKafka.getDefaults(),objKafka.isAttachInvDefault());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateManageAttachmentType.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
