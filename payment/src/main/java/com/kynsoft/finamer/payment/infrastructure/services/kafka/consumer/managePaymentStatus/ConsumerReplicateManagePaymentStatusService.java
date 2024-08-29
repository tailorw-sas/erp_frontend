package com.kynsoft.finamer.payment.infrastructure.services.kafka.consumer.managePaymentStatus;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManagePaymentStatusKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.managePaymentStatus.create.CreateManagePaymentStatusCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManagePaymentStatusService {

    private final IMediator mediator;

    public ConsumerReplicateManagePaymentStatusService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-payment-status", groupId = "payment-entity-replica")
    public void listen(ReplicateManagePaymentStatusKafka objKafka) {
        try {
            CreateManagePaymentStatusCommand command = new CreateManagePaymentStatusCommand(objKafka.getId(), objKafka.getCode(), objKafka.getName(), objKafka.getStatus(), objKafka.getApplied());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManagePaymentStatusService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
