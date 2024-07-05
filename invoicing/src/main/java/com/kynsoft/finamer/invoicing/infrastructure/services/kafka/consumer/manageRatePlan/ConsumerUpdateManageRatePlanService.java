package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageRatePlan;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageRatePlanKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageRatePlan.update.UpdateManageRatePlanCommand;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateManageRatePlanService {

    private final IMediator mediator;

    public ConsumerUpdateManageRatePlanService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-rate-plan", groupId = "invoicing-entity-replica")
    public void listen(UpdateManageRatePlanKafka objKafka) {
        try {

            UpdateManageRatePlanCommand command = new UpdateManageRatePlanCommand(
                    objKafka.getId(),
                    objKafka.getName());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateManageRatePlanService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
