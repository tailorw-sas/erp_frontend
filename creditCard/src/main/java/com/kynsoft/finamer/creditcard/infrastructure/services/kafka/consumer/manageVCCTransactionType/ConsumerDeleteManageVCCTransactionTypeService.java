package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageVCCTransactionType;

import com.kynsof.share.core.domain.kafka.entity.ObjectIdKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageVCCTransactionType.delete.DeleteManageVCCTransactionTypeCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerDeleteManageVCCTransactionTypeService {

    private final IMediator mediator;

    public ConsumerDeleteManageVCCTransactionTypeService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-delete-manage-vcc-transaction-type", groupId = "vcc-entity-replica")
    public void listen(ObjectIdKafka entity) {
        try {
            DeleteManageVCCTransactionTypeCommand command = new DeleteManageVCCTransactionTypeCommand(entity.getId());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerDeleteManageVCCTransactionTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
