package com.kynsoft.finamer.insis.infrastructure.services.kafka.consumer.manageRoomType;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageRoomTypeKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.insis.application.command.manageRoomType.update.UpdateRoomTypeCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateManageRoomTypeService {
    private final IMediator mediator;

    public ConsumerUpdateManageRoomTypeService(IMediator mediator){
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-room-type", groupId = "innsist-entity-replica")
    public void listen(UpdateManageRoomTypeKafka entity){
        try {
            UpdateRoomTypeCommand command = new UpdateRoomTypeCommand(
                    entity.getId(),
                    entity.getName(),
                    entity.getStatus(),
                    entity.getManageHotel()
            );
            mediator.send(command);
        }catch (Exception ex){
            Logger.getLogger(ConsumerUpdateManageRoomTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
