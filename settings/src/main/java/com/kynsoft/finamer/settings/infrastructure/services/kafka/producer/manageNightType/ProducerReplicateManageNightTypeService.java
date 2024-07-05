package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageNightType;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageNightTypeKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageNightTypeService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageNightTypeService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManageNightTypeKafka entity) {
        try {
            this.producer.send("finamer-replicate-manage-night-type", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManageNightTypeService.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}