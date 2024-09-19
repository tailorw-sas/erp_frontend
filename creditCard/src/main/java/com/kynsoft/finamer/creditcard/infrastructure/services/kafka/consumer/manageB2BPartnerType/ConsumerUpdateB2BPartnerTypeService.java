package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageB2BPartnerType;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageAgencyKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
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

    @KafkaListener(topics = "finamer-update-manage-client", groupId = "vcc-entity-replica")
    public void listen(UpdateManageAgencyKafka objKafka) {
        try {
//            UpdateManageAgencyCommand command = new UpdateManageAgencyCommand(objKafka.getId(), objKafka.getName(),
//                    objKafka.getClient(),objKafka.getCif(),
//                    objKafka.getAddress(),objKafka.getSentB2BPartner(),
//                    objKafka.getCityState(),objKafka.getCountry());
//            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateB2BPartnerTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
