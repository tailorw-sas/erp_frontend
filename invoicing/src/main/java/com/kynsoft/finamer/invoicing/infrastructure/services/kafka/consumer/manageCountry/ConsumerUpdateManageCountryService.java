package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageCountry;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageCountryKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateManageCountryService {

    private final IMediator mediator;

    public ConsumerUpdateManageCountryService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-country", groupId = "invoicing-entity-replica")
    public void listen(UpdateManageCountryKafka objKafka) {
        try {
//            UpdateManageAgencyCommand command = new UpdateManageAgencyCommand(objKafka.getId(), objKafka.getName(),
//                    objKafka.getClient(),objKafka.getCif(),
//                    objKafka.getAddress(),objKafka.getSentB2BPartner(),
//                    objKafka.getCityState(),objKafka.getCountry());
//            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateManageCountryService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
