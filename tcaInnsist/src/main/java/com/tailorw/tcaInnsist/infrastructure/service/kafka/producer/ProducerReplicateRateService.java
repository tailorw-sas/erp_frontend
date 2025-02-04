package com.tailorw.tcaInnsist.infrastructure.service.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tailorw.tcaInnsist.infrastructure.model.kafka.ManageRateKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;

@Service
public class ProducerReplicateRateService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateRateService(KafkaTemplate<String, Object> producer){
        this.producer = producer;
    }

    @Async
    public void create(ManageRateKafka entity){
        try{
            ObjectMapper mapper = new ObjectMapper();
            String stringSerialized = mapper.writeValueAsString(entity);
            producer.send("finamer-replicate-tca-rate", stringSerialized);
        }catch (Exception ex){
            Logger.getLogger(ProducerReplicateRateService.class.getName()).log(Level.INFO, null, ex);
        }
    }
}
