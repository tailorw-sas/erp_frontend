package com.kynsoft.finamer.payment.infrastructure.services.kafka.consumer.managePaymentAttachmentStatus;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManagePaymentAttachmentStatusKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.managePaymentAttachmentStatus.update.UpdateManagePaymentAttachmentStatusCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateManagePaymentAttachmentStatusService {

    private final IMediator mediator;

    public ConsumerUpdateManagePaymentAttachmentStatusService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-payment-attachment-status", groupId = "payment-entity-replica")
    public void listen(UpdateManagePaymentAttachmentStatusKafka objKafka) {
        try {
            UpdateManagePaymentAttachmentStatusCommand command = new UpdateManagePaymentAttachmentStatusCommand(objKafka.getId(), objKafka.getName(), objKafka.getStatus(), objKafka.getDefaults());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateManagePaymentAttachmentStatusService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
