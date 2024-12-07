package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.paymentResourceType;

import com.kynsof.share.core.domain.kafka.entity.update.UpdatePaymentResourceTypeKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.resourceType.update.UpdateManageResourceTypeCommand;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateUpdatePaymentResourceTypeService {

    private final IMediator mediator;

    public ConsumerReplicateUpdatePaymentResourceTypeService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-payment-resource-type", groupId = "vcc-entity-replica")
    public void listen(UpdatePaymentResourceTypeKafka objKafka) {
        try {

            UpdateManageResourceTypeCommand command = new UpdateManageResourceTypeCommand(
                    objKafka.getId(),
                    objKafka.getName(),
                    objKafka.isVcc(),
                    Status.valueOf(objKafka.getStatus())
            );
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateUpdatePaymentResourceTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
