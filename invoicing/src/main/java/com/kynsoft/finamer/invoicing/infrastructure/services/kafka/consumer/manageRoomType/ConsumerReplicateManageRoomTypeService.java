package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageRoomType;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageRoomTypeKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageRoomType.create.CreateManageRoomTypeCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageRoomTypeService {

    private final IMediator mediator;

    public ConsumerReplicateManageRoomTypeService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-room-type", groupId = "invoicing-entity-replica")
    public void listen(ReplicateManageRoomTypeKafka objKafka) {
        try {

            CreateManageRoomTypeCommand command = new CreateManageRoomTypeCommand(objKafka.getId(), objKafka.getCode(),
                    objKafka.getName(), objKafka.getStatus());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageRoomTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
