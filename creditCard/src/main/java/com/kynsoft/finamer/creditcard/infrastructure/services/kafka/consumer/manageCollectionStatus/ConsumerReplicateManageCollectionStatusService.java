package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageCollectionStatus;

import com.kynsof.share.core.domain.kafka.entity.vcc.ReplicateManageCollectionStatusKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageCollectionStatus.create.CreateManageCollectionStatusCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageCollectionStatusService {

    private final IMediator mediator;

    public ConsumerReplicateManageCollectionStatusService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-collection-status", groupId = "vcc-entity-replica")
    public void listen(ReplicateManageCollectionStatusKafka entity) {
        try {
            CreateManageCollectionStatusCommand command = new CreateManageCollectionStatusCommand(entity.getId(), entity.getCode(), entity.getName());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageCollectionStatusService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
