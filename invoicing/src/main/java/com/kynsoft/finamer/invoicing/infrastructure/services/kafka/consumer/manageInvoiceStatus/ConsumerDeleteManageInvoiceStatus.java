package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageInvoiceStatus;

import com.kynsof.share.core.domain.kafka.entity.ReplicateDeleteManageInvoiceStatusKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageInvoiceStatus.delete.DeleteManageInvoiceStatusCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerDeleteManageInvoiceStatus {

    private final IMediator mediator;

    public ConsumerDeleteManageInvoiceStatus(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-delete-manage-invoice-status", groupId = "invoicing-entity-replica")
    public void listen(ReplicateDeleteManageInvoiceStatusKafka objKafka) {
        try {
            DeleteManageInvoiceStatusCommand command = new DeleteManageInvoiceStatusCommand(objKafka.getId());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerDeleteManageInvoiceStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
