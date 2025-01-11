package com.kynsoft.finamer.settings.infrastructure.services.kafka.consumer.manageRatePlan;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageRatePlanKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageRatePlan.create.CreateManageRatePlanCommand;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
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

    @KafkaListener(topics = "finamer-innsist-replicate-manage-rate-plan", groupId = "settings-entity-replica")
    public void listen(ReplicateManageRatePlanKafka entity){
        try{
            CreateManageRatePlanCommand command = new CreateManageRatePlanCommand(
                    entity.getId(),
                    entity.getCode(),
                    "",
                    entity.getName(),
                    entity.getHotelId(),
                    Status.ACTIVE
            );
            mediator.send(command);
        }catch (Exception ex){
            Logger.getLogger(ConsumerReplicateManageRatePlanService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
