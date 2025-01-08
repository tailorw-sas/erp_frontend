package com.kynsoft.finamer.insis.infrastructure.services.kafka.producer.manageHotel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsoft.finamer.insis.infrastructure.model.kafka.ReplicateManageHotels;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicaAllManageHotelService {

    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicaAllManageHotelService(KafkaTemplate<String, Object> producer){
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManageHotels entity){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            String serializedList = objectMapper.writeValueAsString(entity);
            this.producer.send("finamer-innsist-replicate-all-manage-hotel", serializedList);
        }catch(Exception ex){
            Logger.getLogger(ProducerReplicaAllManageHotelService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
