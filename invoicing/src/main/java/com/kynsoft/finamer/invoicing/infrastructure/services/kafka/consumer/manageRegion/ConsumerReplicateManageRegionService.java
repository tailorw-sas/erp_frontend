package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageRegion;

import com.kynsof.share.core.domain.kafka.entity.ManageRegionKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageRatePlanKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageRatePlan.create.CreateManageRatePlanCommand;
import com.kynsoft.finamer.invoicing.application.command.manageRegion.create.CreateManageRegionCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageRegionService {

    private final IMediator mediator;

    public ConsumerReplicateManageRegionService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-region", groupId = "invoicing-entity-replica")
    public void listen(ManageRegionKafka objKafka) {
        try {

            CreateManageRegionCommand command = new CreateManageRegionCommand(
                    objKafka.getId(), objKafka.getCode(),
                    objKafka.getName());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageRegionService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
