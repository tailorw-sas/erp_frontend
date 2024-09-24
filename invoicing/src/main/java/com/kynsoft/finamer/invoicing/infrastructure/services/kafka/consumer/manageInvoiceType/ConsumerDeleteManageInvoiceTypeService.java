package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageInvoiceType;

import com.kynsof.share.core.domain.kafka.entity.ReplicateDeleteManageInvoiceTypeKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageInvoiceType.delete.DeleteManageInvoiceTypeCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerDeleteManageInvoiceTypeService {

    private final IMediator mediator;

    public ConsumerDeleteManageInvoiceTypeService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-delete-manage-invoice-type", groupId = "invoicing-entity-replica")
    public void listen(ReplicateDeleteManageInvoiceTypeKafka objKafka) {
        try {

            DeleteManageInvoiceTypeCommand command = new DeleteManageInvoiceTypeCommand(objKafka.getId());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerDeleteManageInvoiceTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
