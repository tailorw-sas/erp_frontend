package com.kynsoft.finamer.payment.infrastructure.services.kafka.consumer.manageClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageClientKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.manageClient.create.CreateManageClientCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class CreateConsumerReplicateManageClientService {

    private final IMediator mediator;

    public CreateConsumerReplicateManageClientService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-client", groupId = "payment-entity-replica")
    public void listen(String event) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(event);

            ReplicateManageClientKafka objKafka = objectMapper.treeToValue(rootNode, ReplicateManageClientKafka.class);
            CreateManageClientCommand command = new CreateManageClientCommand(objKafka.getId(), objKafka.getCode(), objKafka.getName());
            mediator.send(command);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(CreateConsumerReplicateManageClientService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
