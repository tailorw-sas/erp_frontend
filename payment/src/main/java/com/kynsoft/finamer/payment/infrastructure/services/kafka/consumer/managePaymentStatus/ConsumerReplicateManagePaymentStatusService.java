package com.kynsoft.finamer.payment.infrastructure.services.kafka.consumer.managePaymentStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManagePaymentStatusKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.managePaymentStatus.create.CreateManagePaymentStatusCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManagePaymentStatusService {

    private final IMediator mediator;

    public ConsumerReplicateManagePaymentStatusService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-payment-status", groupId = "payment-entity-replica")
    public void listen(String event) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(event);

            ReplicateManagePaymentStatusKafka objKafka = objectMapper.treeToValue(rootNode, ReplicateManagePaymentStatusKafka.class);
            CreateManagePaymentStatusCommand command = new CreateManagePaymentStatusCommand(objKafka.getId(), objKafka.getCode(), objKafka.getName());
            mediator.send(command);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(ConsumerReplicateManagePaymentStatusService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
