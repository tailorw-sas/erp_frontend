package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageInvoiceTransactionType;

import com.kynsof.share.core.domain.kafka.entity.ObjectIdKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageInvoiceTransactionType.delete.DeleteManageInvoiceTransactionTypeCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerDeleteManageInvoiceTransactionTypeService {

    private final IMediator mediator;

    public ConsumerDeleteManageInvoiceTransactionTypeService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-delete-manage-invoice-transaction-type", groupId = "invoicing-entity-replica")
    public void listen(ObjectIdKafka objKafka) {
        try {

            DeleteManageInvoiceTransactionTypeCommand command = new DeleteManageInvoiceTransactionTypeCommand(
                    objKafka.getId());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerDeleteManageInvoiceTransactionTypeService.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
    }

}
