package com.tailorw.tcaInnsist.infrastructure.service.kafka.consumer.manageHotel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.tailorw.tcaInnsist.application.command.manageHotel.create.CreateManageHotelCommand;
import com.tailorw.tcaInnsist.application.command.manageHotel.createMany.CreateManyManageHotelCommand;
import com.tailorw.tcaInnsist.infrastructure.model.kafka.manageHotel.ReplicateAllManageHotelKafka;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class ConsumerReplicateAllManageHotelService {

    private final IMediator mediator;

    @KafkaListener(topics = "finamer-innsist-replicate-all-manage-hotel", groupId = "tcaInnsist-replicate-all-entity")
    public void listen(String event){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            ReplicateAllManageHotelKafka objKafka = objectMapper.readValue(event, new TypeReference<ReplicateAllManageHotelKafka>() {});

            List<CreateManageHotelCommand> createCommands = objKafka.getManageHotels()
                            .stream()
                                    .map(manageHotelKafka -> {
                                        return new CreateManageHotelCommand(
                                                manageHotelKafka.getId(),
                                                manageHotelKafka.getCode(),
                                                manageHotelKafka.getName(),
                                                manageHotelKafka.getRoomType(),
                                                manageHotelKafka.getTradingCompanyId()
                                        );
                                    }).toList();
            CreateManyManageHotelCommand command = new CreateManyManageHotelCommand(createCommands);
            mediator.send(command);

        } catch (JsonProcessingException ex) {
            Logger.getLogger(ConsumerReplicateAllManageHotelService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
