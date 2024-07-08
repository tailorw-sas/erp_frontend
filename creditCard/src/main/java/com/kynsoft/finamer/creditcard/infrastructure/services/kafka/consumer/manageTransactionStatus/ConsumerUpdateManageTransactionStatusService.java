package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageTransactionStatus;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageTransactionStatusKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.managerTransactionStatus.update.UpdateManageTransactionStatusCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateManageTransactionStatusService {

    private final IMediator mediator;

    public ConsumerUpdateManageTransactionStatusService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-transaction-status", groupId = "vcc-entity-replica")
    public void listen(UpdateManageTransactionStatusKafka objKafka) {
        try {
            UpdateManageTransactionStatusCommand command = new UpdateManageTransactionStatusCommand(objKafka.getId(), objKafka.getName());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateManageTransactionStatusService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
