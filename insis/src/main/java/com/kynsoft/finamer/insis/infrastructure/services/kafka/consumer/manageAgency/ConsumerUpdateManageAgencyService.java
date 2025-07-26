package com.kynsoft.finamer.insis.infrastructure.services.kafka.consumer.manageAgency;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageAgencyKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.insis.application.command.manageAgency.update.UpdateAgencyCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateManageAgencyService {
    private final IMediator mediator;

    public ConsumerUpdateManageAgencyService(IMediator mediator){
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-agency", groupId = "innsist-entity-replica")
    public void listen(UpdateManageAgencyKafka objKafka){
        try{
            UpdateAgencyCommand command = new UpdateAgencyCommand(
                    objKafka.getId(),
                    objKafka.getName(),
                    objKafka.getAgencyAlias(),
                    objKafka.getStatus()
            );

            mediator.send(command);
        }catch (Exception ex){
            Logger.getLogger(ConsumerUpdateManageAgencyService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
