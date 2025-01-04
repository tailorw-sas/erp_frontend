package com.kynsoft.finamer.settings.infrastructure.services.kafka.consumer.manageModule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.kafka.entity.ReplicateModuleKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageModule.create.CreateManageModuleCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageModuleService {

    private final IMediator mediator;

    public ConsumerReplicateManageModuleService(IMediator mediator) {
        this.mediator = mediator;
    }

    //@KafkaListener(topics = "finamer-module", groupId = "settings-entity-replica")
    public void listen(String event) {
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode rootNode = objectMapper.readTree(event);
//
//            ReplicateModuleKafka objKafka = objectMapper.treeToValue(rootNode, ReplicateModuleKafka.class);
//            CreateManageModuleCommand command = new CreateManageModuleCommand(objKafka.getId(), objKafka.getCode(), objKafka.getName(), objKafka.getStatus());
//            mediator.send(command);
//        } catch (JsonProcessingException ex) {
//            Logger.getLogger(com.kynsoft.finamer.settings.infrastructure.services.kafka.consumer.manageModule.ConsumerReplicateManageModuleService.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
}
