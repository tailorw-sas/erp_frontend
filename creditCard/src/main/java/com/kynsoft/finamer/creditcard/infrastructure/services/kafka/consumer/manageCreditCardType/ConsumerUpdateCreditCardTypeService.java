package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageCreditCardType;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageCreditCardTypeKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageCreditCardType.update.UpdateManageCreditCardTypeCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateCreditCardTypeService {

    private final IMediator mediator;

    public ConsumerUpdateCreditCardTypeService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-credit-card-type", groupId = "vcc-entity-replica")
    public void listen(UpdateManageCreditCardTypeKafka objKafka) {
        try {
            UpdateManageCreditCardTypeCommand command = new UpdateManageCreditCardTypeCommand(objKafka.getId(), objKafka.getName());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateCreditCardTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
