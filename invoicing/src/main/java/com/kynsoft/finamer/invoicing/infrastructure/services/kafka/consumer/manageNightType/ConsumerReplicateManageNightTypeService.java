package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageNightType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageNightTypeKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageNightType.create.CreateManageNightTypeCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageNightTypeService {

    private final IMediator mediator;

    public ConsumerReplicateManageNightTypeService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-night-type", groupId = "invoicing-entity-replica")
    public void listen(String event) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ReplicateManageNightTypeKafka objKafka = objectMapper.readValue(event, ReplicateManageNightTypeKafka.class);
            CreateManageNightTypeCommand command = new CreateManageNightTypeCommand(objKafka.getId(), objKafka.getCode(), objKafka.getName());
            mediator.send(command);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(ConsumerReplicateManageNightTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}