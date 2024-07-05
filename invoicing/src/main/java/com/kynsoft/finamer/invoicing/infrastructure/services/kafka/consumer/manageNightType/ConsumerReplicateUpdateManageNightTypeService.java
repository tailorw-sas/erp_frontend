package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageNightType;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageNightTypeKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageNightType.update.UpdateManageNightTypeCommand;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateUpdateManageNightTypeService {

    private final IMediator mediator;

    public ConsumerReplicateUpdateManageNightTypeService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-night-type", groupId = "invoicing-entity-replica")
    public void listen(UpdateManageNightTypeKafka objKafka) {
        try {

            UpdateManageNightTypeCommand command = new UpdateManageNightTypeCommand(
                    objKafka.getId(),
                    objKafka.getName());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateUpdateManageNightTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
