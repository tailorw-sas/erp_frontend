package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageRoomCategory;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageRoomCategoryKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageRoomCategory.create.CreateManageRoomCategoryCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageRoomCategoryService {

    private final IMediator mediator;

    public ConsumerReplicateManageRoomCategoryService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-room-category", groupId = "invoicing-entity-replica")
    public void listen(ReplicateManageRoomCategoryKafka objKafka) {
        try {

            CreateManageRoomCategoryCommand command = new CreateManageRoomCategoryCommand(objKafka.getId(),
                    objKafka.getCode(), objKafka.getName(), objKafka.getStatus());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageRoomCategoryService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
