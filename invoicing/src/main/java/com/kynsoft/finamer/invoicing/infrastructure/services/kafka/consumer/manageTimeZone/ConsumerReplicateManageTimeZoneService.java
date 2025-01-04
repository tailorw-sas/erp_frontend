package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageTimeZone;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageTimeZoneKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageTimeZone.create.CreateManagerTimeZoneCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageTimeZoneService {

    private final IMediator mediator;

    public ConsumerReplicateManageTimeZoneService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-time-zone", groupId = "invoicing-entity-replica")
    public void listen(ReplicateManageTimeZoneKafka entity) {
        try {
            CreateManagerTimeZoneCommand command = new CreateManagerTimeZoneCommand(entity.getId(), entity.getCode(), entity.getDescription(), entity.getName(), entity.getElapse(), entity.getStatus());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageTimeZoneService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
