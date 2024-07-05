package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageNightType;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageNightTypeKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateManageNightTypeService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateManageNightTypeService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateManageNightTypeKafka entity) {
        try {
            this.producer.send("finamer-update-manage-night-type", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateManageNightTypeService.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}