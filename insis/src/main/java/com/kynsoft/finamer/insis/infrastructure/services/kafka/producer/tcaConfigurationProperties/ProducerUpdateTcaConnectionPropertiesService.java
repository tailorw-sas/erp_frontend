package com.kynsoft.finamer.insis.infrastructure.services.kafka.producer.tcaConfigurationProperties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsoft.finamer.insis.infrastructure.model.kafka.ReplicateManageConnectionKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateTcaConnectionPropertiesService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateTcaConnectionPropertiesService(KafkaTemplate<String, Object> producer){
        this.producer = producer;
    }

    @Async
    public void update(ReplicateManageConnectionKafka entity){
        try{
            ObjectMapper mapper = new ObjectMapper();
            String serializedString = mapper.writeValueAsString(entity);
            producer.send("finamer-innsist-update-tca-connection", serializedString);
        }catch (Exception ex){
            Logger.getLogger(ProducerUpdateTcaConnectionPropertiesService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
