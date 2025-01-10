package com.kynsoft.finamer.insis.infrastructure.services.kafka.producer.manageHotel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsoft.finamer.insis.infrastructure.model.kafka.ReplicateManageHotelKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageHotelService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageHotelService(KafkaTemplate<String, Object> producer){
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManageHotelKafka entity){
        try{
            ObjectMapper mapper = new ObjectMapper();
            String serializedString = mapper.writeValueAsString(entity);
            producer.send("finamer-innsist-replicate-manage-hotel", serializedString);
        }catch (Exception ex){
            Logger.getLogger(ProducerReplicateManageHotelService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
