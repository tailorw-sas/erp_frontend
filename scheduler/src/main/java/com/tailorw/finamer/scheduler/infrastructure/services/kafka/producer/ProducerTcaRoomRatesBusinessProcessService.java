package com.tailorw.finamer.scheduler.infrastructure.services.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tailorw.finamer.scheduler.infrastructure.model.kafka.SyncRatesByInvoiceDateKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerTcaRoomRatesBusinessProcessService {

    private final KafkaTemplate<String, Object> producer;

    public ProducerTcaRoomRatesBusinessProcessService(KafkaTemplate<String, Object> producer){
        this.producer = producer;
    }

    @Async
    public void create(SyncRatesByInvoiceDateKafka entity){
        try{
            ObjectMapper mapper = new ObjectMapper();
            String stringSerialized = mapper.writeValueAsString(entity);
            producer.send("finamer-sync-rates-by-date", stringSerialized);
        }catch (Exception ex){
            Logger.getLogger(ProducerTcaRoomRatesBusinessProcessService.class.getName()).log(Level.INFO, ex, null);
        }
    }
}
