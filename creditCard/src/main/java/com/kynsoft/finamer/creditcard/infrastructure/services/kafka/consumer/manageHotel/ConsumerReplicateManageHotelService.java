package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageHotel;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageHotelKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageHotel.create.CreateManageHotelCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageHotelService {

    private final IMediator mediator;

    public ConsumerReplicateManageHotelService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-hotel", groupId = "vcc-entity-replica")
    public void listen(ReplicateManageHotelKafka entity) {
        try {
            CreateManageHotelCommand command = new CreateManageHotelCommand(entity.getId(), entity.getCode(), entity.getName(), entity.getIsApplyByVCC());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageHotel.ConsumerReplicateManageHotelService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
