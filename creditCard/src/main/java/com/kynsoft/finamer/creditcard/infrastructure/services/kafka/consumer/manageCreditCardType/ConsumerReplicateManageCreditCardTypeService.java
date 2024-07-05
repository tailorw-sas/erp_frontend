package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageCreditCardType;

import com.kynsof.share.core.domain.kafka.entity.vcc.ReplicateManageCreditCardTypeKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageCreditCardType.create.CreateManageCreditCardTypeCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageCreditCardTypeService {

    private final IMediator mediator;

    public ConsumerReplicateManageCreditCardTypeService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-credit-card-type", groupId = "vcc-entity-replica")
    public void listen(ReplicateManageCreditCardTypeKafka entity) {
        try {
            CreateManageCreditCardTypeCommand command = new CreateManageCreditCardTypeCommand(entity.getId(), entity.getCode(), entity.getName());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageCreditCardTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
