package com.kynsoft.finamer.insis.infrastructure.services.kafka.producer.tcaConfigurationProperties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsoft.finamer.insis.infrastructure.model.kafka.DeleteTcaConfigurationPropertiesKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerDeleteTcaConfigurationService {

    private final KafkaTemplate<String, Object> producer;

    public ProducerDeleteTcaConfigurationService(KafkaTemplate<String, Object> producer){
        this.producer = producer;
    }

    @Async
    public void delete(DeleteTcaConfigurationPropertiesKafka entity){
        try{
            ObjectMapper mapper = new ObjectMapper();
            String stringSerialized = mapper.writeValueAsString(entity);
            producer.send("finamer-innsist-delete-tca-configuration", stringSerialized);
        }catch (Exception ex){
            Logger.getLogger(ProducerDeleteTcaConfigurationService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
