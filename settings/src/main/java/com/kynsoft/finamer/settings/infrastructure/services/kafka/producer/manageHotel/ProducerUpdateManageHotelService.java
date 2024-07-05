package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageHotel;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageHotelKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateManageHotelService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateManageHotelService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateManageHotelKafka entity) {

        try {
             this.producer.send("finamer-update-manage-hotel", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateManageHotelService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}