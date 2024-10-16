
package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageVCCTransactionType;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageVCCTransactionTypeKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageVCCTransactionType.update.UpdateManageVCCTransactionTypeCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateManageVCCTransactionTypeService {

    private final IMediator mediator;

    public ConsumerUpdateManageVCCTransactionTypeService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-vcc-transaction-type", groupId = "vcc-entity-replica")
    public void listen(UpdateManageVCCTransactionTypeKafka objKafka) {
        try {
            UpdateManageVCCTransactionTypeCommand command = new UpdateManageVCCTransactionTypeCommand(objKafka.getId(), objKafka.getName(), objKafka.getIsDefault());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateManageVCCTransactionTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
