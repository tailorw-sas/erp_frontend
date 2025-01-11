package com.kynsoft.finamer.settings.infrastructure.services.kafka.consumer.manageRoomType;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageRoomTypeKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageRoomType.create.CreateManageRoomTypeCommand;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageRoomTypeService {
    private final IMediator mediator;

    public ConsumerReplicateManageRoomTypeService(IMediator mediator){
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-innsist-replicate-manage-room-type", groupId = "settings-entity-replica")
    public void listen(ReplicateManageRoomTypeKafka entity){
        try{
            CreateManageRoomTypeCommand command = new CreateManageRoomTypeCommand(
                    entity.getId(),
                    entity.getCode(),
                    "",
                    Status.ACTIVE,
                    entity.getName(),
                    entity.getHotelId()
            );
            mediator.send(command);
        }catch (Exception ex){
            Logger.getLogger(ConsumerReplicateManageRoomTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
