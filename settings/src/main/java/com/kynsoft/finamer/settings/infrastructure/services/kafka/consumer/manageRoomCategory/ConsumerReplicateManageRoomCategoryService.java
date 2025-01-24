package com.kynsoft.finamer.settings.infrastructure.services.kafka.consumer.manageRoomCategory;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageRoomCategoryKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageRoomCategory.create.CreateManageRoomCategoryCommand;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageRoomCategoryService {

    private final IMediator mediator;

    public ConsumerReplicateManageRoomCategoryService(IMediator mediator){
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-innsist-replicate-manage-room-category", groupId = "settings-entity-replica")
    public void listen(ReplicateManageRoomCategoryKafka objKafka){
        try {
            CreateManageRoomCategoryCommand command = new CreateManageRoomCategoryCommand(
                    objKafka.getId(),
                    objKafka.getCode(),
                    Status.ACTIVE,
                    objKafka.getName(),
                    ""
            );
            mediator.send(command);
        }catch (Exception ex){
            Logger.getLogger(ConsumerReplicateManageRoomCategoryService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
