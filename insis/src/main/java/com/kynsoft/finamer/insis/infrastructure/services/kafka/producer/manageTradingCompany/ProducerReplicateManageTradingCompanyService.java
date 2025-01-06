package com.kynsoft.finamer.insis.infrastructure.services.kafka.producer.manageTradingCompany;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsoft.finamer.insis.infrastructure.model.kafka.ReplicateManageTradingCompanyKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageTradingCompanyService {

    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageTradingCompanyService(KafkaTemplate<String, Object> producer){
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManageTradingCompanyKafka entity){
        try{
            ObjectMapper mapper = new ObjectMapper();
            String stringSerialized = mapper.writeValueAsString(entity);
            producer.send("finamer-innsist-replicate-manage-trading-company", stringSerialized);
        }catch (Exception ex){
            Logger.getLogger(ProducerReplicateManageTradingCompanyService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
