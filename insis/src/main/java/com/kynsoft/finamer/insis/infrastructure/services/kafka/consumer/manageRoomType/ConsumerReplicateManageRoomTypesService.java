package com.kynsoft.finamer.insis.infrastructure.services.kafka.consumer.manageRoomType;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageRoomTypeKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.insis.application.command.manageRatePlan.create.CreateRatePlanCommand;
import com.kynsoft.finamer.insis.application.command.manageRoomType.create.CreateRoomTypeCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageRoomTypesService {
    private final IMediator mediator;

    public ConsumerReplicateManageRoomTypesService(IMediator mediator){
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-room-type", groupId = "innsist-entity-replica")
    public void listen(ReplicateManageRoomTypeKafka entity){
        try{
            CreateRoomTypeCommand command = new CreateRoomTypeCommand(
                entity.getId(),
                    entity.getCode(),
                    entity.getName(),
                    entity.getHotelId()
            );
            mediator.send(command);
        }catch (Exception ex){
            Logger.getLogger(ConsumerReplicateManageRoomTypesService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
