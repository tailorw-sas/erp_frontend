package com.kynsoft.finamer.payment.infrastructure.services.kafka.consumer.manageLanguage;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageCountryKafka;
import com.kynsof.share.core.domain.kafka.entity.vcc.ReplicateManageLanguageKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.manageCountry.create.CreateManageCountryCommand;
import com.kynsoft.finamer.payment.application.command.manageLanguage.create.CreateManageLanguageCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageLanguageService {

    private final IMediator mediator;

    public ConsumerReplicateManageLanguageService(IMediator mediator){
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-language", groupId = "payment-entity-replica")
    public void listen(ReplicateManageLanguageKafka objKafka){
        try{
            CreateManageLanguageCommand command = new CreateManageLanguageCommand(
                    objKafka.getId(),
                    objKafka.getCode(),
                    objKafka.getName(),
                    objKafka.getDefaults(),
                    objKafka.getStatus()
            );

            mediator.send(command);
        }catch (Exception ex){
            Logger.getLogger(ConsumerReplicateManageLanguageService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
