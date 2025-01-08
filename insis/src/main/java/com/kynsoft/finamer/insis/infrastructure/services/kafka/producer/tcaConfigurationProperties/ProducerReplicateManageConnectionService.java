package com.kynsoft.finamer.insis.infrastructure.services.kafka.producer.tcaConfigurationProperties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsoft.finamer.insis.infrastructure.model.kafka.ReplicateManageConnectionKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageConnectionService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageConnectionService(KafkaTemplate<String, Object> producer){
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManageConnectionKafka entity){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            String serializedEntity = objectMapper.writeValueAsString(entity);
            producer.send("finamer-innsist-replicate-manage-connection", serializedEntity);
        }catch (Exception ex){
            Logger.getLogger(ProducerReplicateManageConnectionService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
