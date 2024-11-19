package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.paymentResourceType;


import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentResourceTypeKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.resourceType.create.CreateManageResourceTypeCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicatePaymentResourceTypeService {

    private final IMediator mediator;

    public ConsumerReplicatePaymentResourceTypeService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-payment-resource-type", groupId = "vcc-entity-replica")
    public void listen(ReplicatePaymentResourceTypeKafka objKafka) {
        try {

            CreateManageResourceTypeCommand command = new CreateManageResourceTypeCommand(objKafka.getId(), objKafka.getCode(),
                    objKafka.getName(), objKafka.isInvoice());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicatePaymentResourceTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
