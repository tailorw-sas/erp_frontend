package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageRoomType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageRoomTypeKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageRoomType.create.CreateManageRoomTypeCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageRoomTypeService {

    private final IMediator mediator;

    public ConsumerReplicateManageRoomTypeService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-room-type", groupId = "invoicing-entity-replica")
    public void listen(String event) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(event);

            ReplicateManageRoomTypeKafka objKafka = objectMapper.treeToValue(rootNode, ReplicateManageRoomTypeKafka.class);
            CreateManageRoomTypeCommand command = new CreateManageRoomTypeCommand(objKafka.getId(), objKafka.getCode(), objKafka.getName());
            mediator.send(command);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(ConsumerReplicateManageRoomTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
