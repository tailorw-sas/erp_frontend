package com.tailorw.tcaInnsist.infrastructure.service.kafka.consumer.manageHotel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.tailorw.tcaInnsist.application.command.manageHotel.create.CreateManageHotelCommand;
import com.tailorw.tcaInnsist.infrastructure.model.kafka.manageHotel.ReplicateManageHotelKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageHotelService {
    private final IMediator mediator;

    public ConsumerReplicateManageHotelService(IMediator mediator){
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-innsist-replicate-manage-hotel", groupId = "tcaInnsist-replicate-entity")
    public void listen(String message){
        try{
            ObjectMapper mapper = new ObjectMapper();
            ReplicateManageHotelKafka objKafka = mapper.readValue(message, new TypeReference<ReplicateManageHotelKafka>(){});
            CreateManageHotelCommand command = new CreateManageHotelCommand(
                    objKafka.getId(),
                    objKafka.getCode(),
                    objKafka.getName(),
                    objKafka.getRoomType(),
                    objKafka.getTradingCompanyId());
            mediator.send(command);
        }catch (Exception ex){
            Logger.getLogger(ConsumerReplicateManageHotelService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
