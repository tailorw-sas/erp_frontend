package com.kynsoft.finamer.payment.infrastructure.services.kafka.consumer.manageCountry;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageCountryKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.manageCountry.create.CreateManageCountryCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageCountryService {

    private final IMediator mediator;

    public ConsumerReplicateManageCountryService(IMediator mediator){
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-country", groupId = "payment-entity-replica")
    public void listen(ReplicateManageCountryKafka objKafka){
        try{
            CreateManageCountryCommand command = new CreateManageCountryCommand(
                    objKafka.getId(),
                    objKafka.getCode(),
                    objKafka.getName(),
                    objKafka.getDescription(),
                    objKafka.isDefault(),
                    objKafka.getStatus(),
                    objKafka.getLanguage(),
                    objKafka.getIso3()
            );

            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageCountryService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
