package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageRoomCategory;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageRoomCategoryKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateManageRoomCategoryService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateManageRoomCategoryService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateManageRoomCategoryKafka entity) {

        try {
            this.producer.send("finamer-update-manage-room-category", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateManageRoomCategoryService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}