package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageRoomType;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageRoomTypeKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateManageRoomTypeService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateManageRoomTypeService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateManageRoomTypeKafka entity) {

        try {
            this.producer.send("finamer-update-manage-room-type", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateManageRoomTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}