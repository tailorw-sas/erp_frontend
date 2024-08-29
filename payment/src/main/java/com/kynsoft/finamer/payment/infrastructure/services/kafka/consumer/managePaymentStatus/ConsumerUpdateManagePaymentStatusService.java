package com.kynsoft.finamer.payment.infrastructure.services.kafka.consumer.managePaymentStatus;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManagePaymentStatusKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.managePaymentStatus.update.UpdateManagePaymentStatusCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateManagePaymentStatusService {

    private final IMediator mediator;

    public ConsumerUpdateManagePaymentStatusService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-payment-status", groupId = "payment-entity-replica")
    public void listen(UpdateManagePaymentStatusKafka objKafka) {
        try {
            UpdateManagePaymentStatusCommand command = new UpdateManagePaymentStatusCommand(objKafka.getId(), objKafka.getName(), objKafka.getStatus(), objKafka.getApplied());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateManagePaymentStatusService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
