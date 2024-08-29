package com.kynsoft.finamer.payment.infrastructure.services.kafka.consumer.managePaymentSource;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManagePaymentSourceKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.managePaymentSource.update.UpdateManagePaymentSourceCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateManagePaymentSourceService {

    private final IMediator mediator;

    public ConsumerUpdateManagePaymentSourceService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-payment-source", groupId = "payment-entity-replica")
    public void listen(UpdateManagePaymentSourceKafka objKafka) {
        try {
            UpdateManagePaymentSourceCommand command = new UpdateManagePaymentSourceCommand(objKafka.getId(), objKafka.getName(), objKafka.getStatus(), objKafka.getExpense());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateManagePaymentSourceService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
