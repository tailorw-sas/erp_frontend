package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageRoomType;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageRoomTypeKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageRoomType.update.UpdateManageRoomTypeCommand;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateUpdateManageRoomTypeService {

    private final IMediator mediator;

    public ConsumerReplicateUpdateManageRoomTypeService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-room-type", groupId = "invoicing-entity-replica")
    public void listen(UpdateManageRoomTypeKafka objKafka) {
        try {

            UpdateManageRoomTypeCommand command = new UpdateManageRoomTypeCommand(
                    objKafka.getId(),
                    objKafka.getName(),
                    objKafka.getStatus()
            );
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateUpdateManageRoomTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
