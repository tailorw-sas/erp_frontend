package com.kynsoft.finamer.insis.infrastructure.services.kafka.consumer.manageRatePlan;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageRatePlanKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.insis.application.command.manageRatePlan.update.UpdateRatePlanCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateManageRatePlanService {
    private final IMediator mediator;

    public ConsumerUpdateManageRatePlanService(IMediator mediator){
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-rate-plan", groupId = "innsist-entity-replica")
    public void listen(UpdateManageRatePlanKafka entity){
        try{
            UpdateRatePlanCommand command = new UpdateRatePlanCommand(
                    entity.getId(),
                    entity.getName(),
                    entity.getStatus(),
                    entity.getMangeHotel()
            );
            mediator.send(command);
        }catch (Exception ex){
            Logger.getLogger(ConsumerUpdateManageRatePlanService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
