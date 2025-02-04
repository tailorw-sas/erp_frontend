package com.kynsoft.finamer.insis.infrastructure.services.kafka.producer.booking;

import com.kynsof.share.core.domain.kafka.entity.importInnsist.ImportInnsistKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerImportInnsistBookingService {

    private final KafkaTemplate<String, Object> producer;

    public ProducerImportInnsistBookingService(KafkaTemplate<String, Object> producer){
        this.producer = producer;
    }

    @Async
    public void create(ImportInnsistKafka entity){
        try{
            producer.send("finamer-import-innsist", entity);
        }catch (Exception ex){
            Logger.getLogger(ProducerImportInnsistBookingService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
