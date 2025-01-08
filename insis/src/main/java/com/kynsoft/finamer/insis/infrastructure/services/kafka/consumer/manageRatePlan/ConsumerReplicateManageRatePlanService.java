package com.kynsoft.finamer.insis.infrastructure.services.kafka.consumer.manageRatePlan;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageRatePlanKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.insis.application.command.manageRatePlan.create.CreateRatePlanCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageRatePlanService {
    private final IMediator mediator;

    public ConsumerReplicateManageRatePlanService(IMediator mediator){
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-rate-plan", groupId = "innsist-entity-replica")
    public void listen(ReplicateManageRatePlanKafka entity){
        try{
            CreateRatePlanCommand command = new CreateRatePlanCommand(
                    entity.getId(),
                    entity.getCode(),
                    entity.getName(),
                    entity.getManageHotel()
            );
            mediator.send(command);
        }catch (Exception ex){
            Logger.getLogger(ConsumerReplicateManageRatePlanService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
