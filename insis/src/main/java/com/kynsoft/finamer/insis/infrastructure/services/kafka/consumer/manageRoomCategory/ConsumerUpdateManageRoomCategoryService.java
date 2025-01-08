package com.kynsoft.finamer.insis.infrastructure.services.kafka.consumer.manageRoomCategory;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageRoomCategoryKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.insis.application.command.manageRoomCategory.update.UpdateManageRoomCategoryCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateManageRoomCategoryService {
    private final IMediator mediator;

    public ConsumerUpdateManageRoomCategoryService( IMediator mediator){
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-room-category", groupId = "innsist-entity-replica")
    public void listen(UpdateManageRoomCategoryKafka objKafka){
        try{
            UpdateManageRoomCategoryCommand command = new UpdateManageRoomCategoryCommand(
                    objKafka.getId(),
                    objKafka.getName(),
                    objKafka.getStatus()
            );
            mediator.send(command);
        }catch (Exception ex){
            Logger.getLogger(ConsumerUpdateManageRoomCategoryService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
