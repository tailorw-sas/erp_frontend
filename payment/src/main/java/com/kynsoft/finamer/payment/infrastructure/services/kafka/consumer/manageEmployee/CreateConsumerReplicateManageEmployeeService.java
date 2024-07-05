package com.kynsoft.finamer.payment.infrastructure.services.kafka.consumer.manageEmployee;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageEmployeeKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.manageEmployee.create.CreateManageEmployeeCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class CreateConsumerReplicateManageEmployeeService {

    private final IMediator mediator;

    public CreateConsumerReplicateManageEmployeeService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-employee", groupId = "payment-entity-replica")
    public void listen(String event) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(event);

            ReplicateManageEmployeeKafka objKafka = objectMapper.treeToValue(rootNode, ReplicateManageEmployeeKafka.class);
            CreateManageEmployeeCommand command = new CreateManageEmployeeCommand(objKafka.getId(), objKafka.getFirstName(), objKafka.getLastName(), objKafka.getEmail());
            mediator.send(command);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(CreateConsumerReplicateManageEmployeeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
