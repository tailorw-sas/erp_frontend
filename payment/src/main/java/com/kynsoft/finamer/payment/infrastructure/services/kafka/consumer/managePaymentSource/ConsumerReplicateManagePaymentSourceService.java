package com.kynsoft.finamer.payment.infrastructure.services.kafka.consumer.managePaymentSource;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManagePaymentSourceKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.managePaymentSource.create.CreateManagePaymentSourceCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManagePaymentSourceService {

    private final IMediator mediator;

    public ConsumerReplicateManagePaymentSourceService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-payment-source", groupId = "payment-entity-replica")
    public void listen(ReplicateManagePaymentSourceKafka objKafka) {
        try {
            CreateManagePaymentSourceCommand command = new CreateManagePaymentSourceCommand(objKafka.getId(), objKafka.getCode(), objKafka.getName(), objKafka.getStatus(), objKafka.getExpense());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManagePaymentSourceService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
