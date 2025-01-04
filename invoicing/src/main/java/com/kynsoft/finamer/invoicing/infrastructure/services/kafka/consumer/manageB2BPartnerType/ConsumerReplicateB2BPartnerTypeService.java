package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageB2BPartnerType;

import com.kynsof.share.core.domain.kafka.entity.ReplicateB2BPartnerTypeKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageAgencyKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageAgency.create.CreateManageAgencyCommand;
import com.kynsoft.finamer.invoicing.application.command.manageB2BPartnerType.create.CreateManageB2BPartnerTypeCommand;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EGenerationType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateB2BPartnerTypeService {

    private final IMediator mediator;

    public ConsumerReplicateB2BPartnerTypeService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-b2b-partner-type", groupId = "invoicing-entity-replica")
    public void listen(ReplicateB2BPartnerTypeKafka objKafka) {
        try {
            CreateManageB2BPartnerTypeCommand command =new CreateManageB2BPartnerTypeCommand(objKafka.getId(),objKafka.getCode(),
                    objKafka.getDescription(),objKafka.getName(), Status.valueOf(objKafka.getStatus()));
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateB2BPartnerTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
