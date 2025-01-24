package com.tailorw.tcaInnsist.infrastructure.service.kafka.consumer.manageHotel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.tailorw.tcaInnsist.application.command.manageHotel.delete.DeleteManageHotelCommand;
import com.tailorw.tcaInnsist.infrastructure.model.kafka.manageHotel.DeleteManageHotelKafka;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class ConsumerDeleteManageHotelService {

    private final IMediator mediator;

    @KafkaListener(topics = "finamer-innsist-delete-manage-hotel", groupId = "tcaInnsist-delete-entity")
    public void listen(String message){
        try{
            ObjectMapper mapper = new ObjectMapper();
            DeleteManageHotelKafka objKafka = mapper.readValue(message, new TypeReference<DeleteManageHotelKafka>() {});
            DeleteManageHotelCommand command = new DeleteManageHotelCommand(objKafka.getId());
            mediator.send(command);
        }catch (Exception ex){
            Logger.getLogger(ConsumerDeleteManageHotelService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
