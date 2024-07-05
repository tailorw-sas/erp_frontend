package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageRatePlan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageRatePlanKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageRatePlan.create.CreateManageRatePlanCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageRatePlanService {

    private final IMediator mediator;

    public ConsumerReplicateManageRatePlanService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-rate-plan", groupId = "invoicing-entity-replica")
    public void listen(ReplicateManageRatePlanKafka objKafka) {
        try {

            CreateManageRatePlanCommand command = new CreateManageRatePlanCommand(objKafka.getId(), objKafka.getCode(),
                    objKafka.getName());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageRatePlanService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
