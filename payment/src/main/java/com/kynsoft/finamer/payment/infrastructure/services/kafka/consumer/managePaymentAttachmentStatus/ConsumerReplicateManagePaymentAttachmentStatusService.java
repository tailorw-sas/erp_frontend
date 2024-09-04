package com.kynsoft.finamer.payment.infrastructure.services.kafka.consumer.managePaymentAttachmentStatus;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManagePaymentAttachmentStatusKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.managePaymentAttachmentStatus.create.CreateManagePaymentAttachmentStatusCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManagePaymentAttachmentStatusService {

    private final IMediator mediator;

    public ConsumerReplicateManagePaymentAttachmentStatusService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-payment-attachment-status", groupId = "payment-entity-replica")
    public void listen(ReplicateManagePaymentAttachmentStatusKafka objKafka) {
        try {
            CreateManagePaymentAttachmentStatusCommand command = new CreateManagePaymentAttachmentStatusCommand(objKafka.getId(), objKafka.getCode(), objKafka.getName(), objKafka.getStatus(), objKafka.getDefaults());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManagePaymentAttachmentStatusService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
