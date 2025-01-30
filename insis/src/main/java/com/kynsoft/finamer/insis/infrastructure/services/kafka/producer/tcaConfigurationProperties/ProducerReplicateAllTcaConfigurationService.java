package com.kynsoft.finamer.insis.infrastructure.services.kafka.producer.tcaConfigurationProperties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsoft.finamer.insis.infrastructure.model.kafka.ReplicateTcaConfigurations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateAllTcaConfigurationService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateAllTcaConfigurationService(KafkaTemplate<String, Object> producer){
        this.producer = producer;
    }

    @Async
    public void create(ReplicateTcaConfigurations entity){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            String serializedString  = objectMapper.writeValueAsString(entity);
            producer.send("finamer-innsist-replicate-all-tca-configuration", serializedString);
        }catch (Exception ex){
            Logger.getLogger(ProducerReplicateAllTcaConfigurationService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
