package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageContact;

import com.kynsof.share.core.domain.kafka.entity.ManageContactKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageContact.create.CreateManageContactCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageContactService {

    private final IMediator mediator;

    public ConsumerReplicateManageContactService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-contact", groupId = "invoicing-entity-replica")
    public void listen(ManageContactKafka objKafka) {
        try {
            CreateManageContactCommand command = new CreateManageContactCommand(
                    objKafka.getId(), 
                    objKafka.getCode(), 
                    objKafka.getDescription(), 
                    objKafka.getName(),
                    objKafka.getManageHotel(), 
                    objKafka.getEmail(), 
                    objKafka.getPhone(), 
                    objKafka.getPosition()
            );
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageContactService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
