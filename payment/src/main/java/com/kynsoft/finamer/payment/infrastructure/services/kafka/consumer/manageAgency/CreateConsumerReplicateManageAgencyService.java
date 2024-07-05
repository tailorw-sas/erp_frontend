package com.kynsoft.finamer.payment.infrastructure.services.kafka.consumer.manageAgency;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageAgencyKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.manageAgency.create.CreateManageAgencyCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class CreateConsumerReplicateManageAgencyService {

    private final IMediator mediator;

    public CreateConsumerReplicateManageAgencyService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-agency", groupId = "payment-entity-replica")
    public void listen(String event) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(event);

            ReplicateManageAgencyKafka objKafka = objectMapper.treeToValue(rootNode, ReplicateManageAgencyKafka.class);
            CreateManageAgencyCommand command = new CreateManageAgencyCommand(objKafka.getId(), objKafka.getCode(), objKafka.getName());
            mediator.send(command);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(CreateConsumerReplicateManageAgencyService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
