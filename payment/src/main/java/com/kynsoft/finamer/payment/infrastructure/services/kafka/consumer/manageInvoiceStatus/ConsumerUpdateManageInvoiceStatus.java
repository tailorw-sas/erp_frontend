package com.kynsoft.finamer.payment.infrastructure.services.kafka.consumer.manageInvoiceStatus;


import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageInvoiceStatusKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.manageInvoiceStatus.update.UpdateManageInvoiceStatusCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateManageInvoiceStatus {

    private final IMediator mediator;

    public ConsumerUpdateManageInvoiceStatus(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-invoice-status", groupId = "payment-entity-replica")
    public void listen(UpdateManageInvoiceStatusKafka objKafka) {
        try {
            UpdateManageInvoiceStatusCommand command = new UpdateManageInvoiceStatusCommand(objKafka.getId(), objKafka.getName());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateManageInvoiceStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
