package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageHotel;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageHotelKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageHotelService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageHotelService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManageHotelKafka entity) {

        try {
             this.producer.send("finamer-replicate-manage-hotel", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManageHotelService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}