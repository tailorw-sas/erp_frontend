package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageAgencyContact;

import com.kynsof.share.core.domain.kafka.entity.ManageAgencyContactKafka;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageAgencyKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageAgency.update.UpdateManageAgencyCommand;
import com.kynsoft.finamer.invoicing.application.command.manageAgencyContact.update.UpdateManageAgencyContactCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateManageAgencyContactService {

    private final IMediator mediator;

    public ConsumerUpdateManageAgencyContactService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-agency-contact", groupId = "invoicing-entity-replica")
    public void listen(ManageAgencyContactKafka objKafka) {
        try {

            UpdateManageAgencyContactCommand command = new UpdateManageAgencyContactCommand(
                    objKafka.getId(), objKafka.getManageAgency(),
                    objKafka.getManageRegion(), objKafka.getManageHotel(),
                    objKafka.getEmailContact()
            );
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateManageAgencyContactService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
