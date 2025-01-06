package com.kynsoft.finamer.insis.infrastructure.services.kafka.consumer.manageHotel;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageHotelKafka;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageHotelKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.insis.application.command.manageHotel.update.UpdateHoteCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateManageHotelService {
    private final IMediator mediator;

    public ConsumerUpdateManageHotelService(IMediator mediator){
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-hotel", groupId = "innsist-entity-replica")
    public void listen(ReplicateManageHotelKafka entity){
        try {
            UpdateHoteCommand command = new UpdateHoteCommand(
                    entity.getId(),
                    entity.getCode(),
                    entity.getName(),
                    entity.getStatus(),
                    entity.getManageTradingCompany()
            );
            mediator.send(command);
        }catch (Exception ex){
            Logger.getLogger(ConsumerUpdateManageHotelService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
