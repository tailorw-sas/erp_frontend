package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageAgencyKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageClientKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;

import com.kynsoft.finamer.invoicing.application.command.manageClient.create.CreateManageClientCommand;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageClientService {

    private final IMediator mediator;

    public ConsumerReplicateManageClientService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-client", groupId = "invoicing-entity-replica")
    public void listen(ReplicateManageClientKafka objKafka) {
        try {
            CreateManageClientCommand command = new CreateManageClientCommand(objKafka.getId(), objKafka.getCode(),
                    objKafka.getName());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageClientService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
