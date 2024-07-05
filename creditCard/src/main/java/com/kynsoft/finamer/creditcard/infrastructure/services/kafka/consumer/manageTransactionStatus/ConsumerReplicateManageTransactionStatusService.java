package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageTransactionStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.kafka.entity.vcc.ReplicateManageTransactionStatusKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.managerTransactionStatus.create.CreateManageTransactionStatusCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageTransactionStatusService {

    private final IMediator mediator;

    public ConsumerReplicateManageTransactionStatusService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-transaction-status", groupId = "vcc-entity-replica")
    public void listen(String event) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(event);

            ReplicateManageTransactionStatusKafka objKafka = objectMapper.treeToValue(rootNode, ReplicateManageTransactionStatusKafka.class);
            CreateManageTransactionStatusCommand command = new CreateManageTransactionStatusCommand(objKafka.getId(), objKafka.getCode(), objKafka.getName());
            mediator.send(command);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(ConsumerReplicateManageTransactionStatusService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
