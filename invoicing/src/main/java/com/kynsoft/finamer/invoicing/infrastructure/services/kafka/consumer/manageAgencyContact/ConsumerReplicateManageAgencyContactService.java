package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageAgencyContact;

import com.kynsof.share.core.domain.kafka.entity.ManageAgencyContactKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageAgencyContact.create.CreateManageAgencyContactCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageAgencyContactService {

    private final IMediator mediator;

    public ConsumerReplicateManageAgencyContactService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-agency-contact", groupId = "invoicing-entity-replica")
    public void listen(ManageAgencyContactKafka objKafka) {
        try {
            CreateManageAgencyContactCommand command = new CreateManageAgencyContactCommand(
                    objKafka.getId(), objKafka.getManageAgency(),
                    objKafka.getManageRegion(), objKafka.getManageHotel(),
                    objKafka.getEmailContact());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageAgencyContactService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
