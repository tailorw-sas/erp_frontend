package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageCountry;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageAgencyKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageCountryKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageAgency.create.CreateManageAgencyCommand;
import com.kynsoft.finamer.invoicing.application.command.manageCountry.create.CreateManagerCountryCommand;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EGenerationType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageCountryService {

    private final IMediator mediator;

    public ConsumerReplicateManageCountryService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-country", groupId = "invoicing-entity-replica")
    public void listen(ReplicateManageCountryKafka objKafka) {
        try {
//
            CreateManagerCountryCommand command = new CreateManagerCountryCommand(objKafka.getId(),objKafka.getCode(),objKafka.getName(),objKafka.getDescription(),
                    "","",false,null, Status.valueOf(objKafka.getStatus()));
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageCountryService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
