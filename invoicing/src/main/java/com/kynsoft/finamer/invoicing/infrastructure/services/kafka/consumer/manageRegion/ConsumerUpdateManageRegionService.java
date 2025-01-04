package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageRegion;

import com.kynsof.share.core.domain.kafka.entity.ManageRegionKafka;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageRatePlanKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageRatePlan.update.UpdateManageRatePlanCommand;
import com.kynsoft.finamer.invoicing.application.command.manageRegion.update.UpdateManageRegionCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateManageRegionService {

    private final IMediator mediator;

    public ConsumerUpdateManageRegionService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-region", groupId = "invoicing-entity-replica")
    public void listen(ManageRegionKafka objKafka) {
        try {

            UpdateManageRegionCommand command = new UpdateManageRegionCommand(
                    objKafka.getId(),
                    objKafka.getName());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateManageRegionService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
