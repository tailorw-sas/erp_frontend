package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageB2BPartner;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageAgencyKafka;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageB2BPartnerKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageAgency.update.UpdateManageAgencyCommand;
import com.kynsoft.finamer.invoicing.application.command.manageB2BPartner.update.UpdateManagerB2BPartnerCommand;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateB2BPartnerService {

    private final IMediator mediator;

    public ConsumerUpdateB2BPartnerService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-b2b-partner", groupId = "invoicing-entity-replica")
    public void listen(UpdateManageB2BPartnerKafka objKafka) {
        try {
            UpdateManagerB2BPartnerCommand command = new UpdateManagerB2BPartnerCommand(objKafka.getId(),
                    objKafka.getDescription(),objKafka.getName(), Status.valueOf(objKafka.getStatus())
                    ,"","","","","",objKafka.getB2BPartnerTypeDto());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateB2BPartnerService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
