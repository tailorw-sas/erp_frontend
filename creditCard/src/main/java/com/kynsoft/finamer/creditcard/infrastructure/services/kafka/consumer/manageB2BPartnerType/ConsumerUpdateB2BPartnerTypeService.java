package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageB2BPartnerType;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageB2BPartnerTypeKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageB2BPartnerType.update.UpdateManageB2BPartnerTypeCommand;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateB2BPartnerTypeService {

    private final IMediator mediator;

    public ConsumerUpdateB2BPartnerTypeService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-b2b-partner-type", groupId = "vcc-entity-replica")
    public void listen(UpdateManageB2BPartnerTypeKafka objKafka) {
        try {
            UpdateManageB2BPartnerTypeCommand command = new UpdateManageB2BPartnerTypeCommand(
                    objKafka.getId(), 
                    objKafka.getDescription(),
                    objKafka.getName(),
                    Status.valueOf(objKafka.getStatus())
            );
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateB2BPartnerTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
