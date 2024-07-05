package com.kynsoft.finamer.payment.infrastructure.services.kafka.consumer.manageBankAccount;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageBankAccountKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.manageBankAccount.create.CreateManageBankAccountCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageBankAccountService {

    private final IMediator mediator;

    public ConsumerReplicateManageBankAccountService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-bank-account", groupId = "payment-entity-replica")
    public void listen(String event) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(event);

            ReplicateManageBankAccountKafka objKafka = objectMapper.treeToValue(rootNode, ReplicateManageBankAccountKafka.class);
            CreateManageBankAccountCommand command = new CreateManageBankAccountCommand(objKafka.getId(), objKafka.getAccountNumber());
            mediator.send(command);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(ConsumerReplicateManageBankAccountService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
