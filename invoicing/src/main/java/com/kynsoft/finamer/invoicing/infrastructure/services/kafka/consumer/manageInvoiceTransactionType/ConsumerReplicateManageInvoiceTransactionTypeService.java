package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageInvoiceTransactionType;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageInvoiceTransactionTypeKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageInvoiceTransactionType.create.CreateManageInvoiceTransactionTypeCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageInvoiceTransactionTypeService {

    private final IMediator mediator;

    public ConsumerReplicateManageInvoiceTransactionTypeService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-invoice-transaction-type", groupId = "invoicing-entity-replica")
    public void listen(ReplicateManageInvoiceTransactionTypeKafka objKafka) {
        try {

            CreateManageInvoiceTransactionTypeCommand command = new CreateManageInvoiceTransactionTypeCommand(
                    objKafka.getId(), objKafka.getCode(), objKafka.getName(), objKafka.isDefaults());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageInvoiceTransactionTypeService.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
    }

}
