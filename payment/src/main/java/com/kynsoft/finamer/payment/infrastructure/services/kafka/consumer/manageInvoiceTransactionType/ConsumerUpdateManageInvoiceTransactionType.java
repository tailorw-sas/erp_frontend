package com.kynsoft.finamer.payment.infrastructure.services.kafka.consumer.manageInvoiceTransactionType;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageInvoiceTransactionTypeKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.manageInvoinceTransactionType.update.UpdateManageInvoiceTransactionTypeCommand;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateManageInvoiceTransactionType {

    private final IMediator mediator;

    public ConsumerUpdateManageInvoiceTransactionType(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-invoice-transaction-type", groupId = "payment-entity-replica")
    public void listen(UpdateManageInvoiceTransactionTypeKafka objKafka) {
        try {

            UpdateManageInvoiceTransactionTypeCommand command = new UpdateManageInvoiceTransactionTypeCommand(
                    objKafka.getId(),
                    objKafka.getName());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateManageInvoiceTransactionType.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
