package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageCollectionStatus;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageCollectionStatusKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageCollectionStatus.update.UpdateManageCollectionStatusCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateManageCollectionStatusService {

    private final IMediator mediator;

    public ConsumerUpdateManageCollectionStatusService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-collection-status", groupId = "vcc-entity-replica")
    public void listen(UpdateManageCollectionStatusKafka objKafka) {
        try {
            UpdateManageCollectionStatusCommand command = new UpdateManageCollectionStatusCommand(objKafka.getId(), objKafka.getName());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateManageCollectionStatusService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
