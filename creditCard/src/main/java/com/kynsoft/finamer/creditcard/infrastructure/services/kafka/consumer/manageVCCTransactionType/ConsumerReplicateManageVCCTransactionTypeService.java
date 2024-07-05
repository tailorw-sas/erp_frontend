package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageVCCTransactionType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.kafka.entity.vcc.ReplicateManageVCCTransactionTypeKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageVCCTransactionType.create.CreateManageVCCTransactionTypeCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageVCCTransactionTypeService {

    private final IMediator mediator;

    public ConsumerReplicateManageVCCTransactionTypeService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-vcc-transaction-type", groupId = "vcc-entity-replica")
    public void listen(String event) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(event);

            ReplicateManageVCCTransactionTypeKafka objKafka = objectMapper.treeToValue(rootNode, ReplicateManageVCCTransactionTypeKafka.class);
            CreateManageVCCTransactionTypeCommand command = new CreateManageVCCTransactionTypeCommand(objKafka.getId(), objKafka.getCode(), objKafka.getName());
            mediator.send(command);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(ConsumerReplicateManageVCCTransactionTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
