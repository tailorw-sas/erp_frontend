package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageHotel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageHotelKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageHotel.create.CreateManageHotelCommand;
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

    @KafkaListener(topics = "finamer-replicate-manage-hotel", groupId = "invoicing-entity-replica")
    public void listen(ReplicateManageHotelKafka objKafka) {
        try {

            CreateManageHotelCommand command = new CreateManageHotelCommand(objKafka.getId(), objKafka.getCode(),
                    objKafka.getName(), objKafka.getManageTradingCompany());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageHotelService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
