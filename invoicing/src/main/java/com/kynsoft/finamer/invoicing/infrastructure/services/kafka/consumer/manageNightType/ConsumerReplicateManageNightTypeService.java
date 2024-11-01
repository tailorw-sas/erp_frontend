package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageNightType;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageNightTypeKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageNightType.create.CreateManageNightTypeCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageNightTypeService {

    private final IMediator mediator;

    public ConsumerReplicateManageNightTypeService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-night-type", groupId = "invoicing-entity-replica")
    public void listen(ReplicateManageNightTypeKafka objKafka) {
        try {

            CreateManageNightTypeCommand command = new CreateManageNightTypeCommand(objKafka.getId(),
                    objKafka.getCode(), objKafka.getName(), objKafka.getStatus());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageNightTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}