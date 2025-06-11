package com.tailorw.tcaInnsist.infrastructure.service.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tailorw.tcaInnsist.infrastructure.model.kafka.BookingKafka;
import com.tailorw.tcaInnsist.infrastructure.model.kafka.GroupedBookingKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateGroupedRatesService {

    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateGroupedRatesService(KafkaTemplate<String, Object> producer){
        this.producer = producer;
    }

    @Async
    public void create(GroupedBookingKafka entity){
        try{
            ObjectMapper mapper = new ObjectMapper();
            String stringSerialized = mapper.writeValueAsString(entity);
            System.out.println("finamer-replicate-grouped-rate ----> " + stringSerialized);
            producer.send("finamer-replicate-grouped-rate", stringSerialized);
        }catch (Exception ex){
            Logger.getLogger(ProducerReplicateGroupedRatesService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
