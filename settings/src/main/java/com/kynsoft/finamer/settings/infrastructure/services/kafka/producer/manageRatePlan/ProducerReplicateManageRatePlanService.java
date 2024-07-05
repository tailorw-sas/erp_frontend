package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageRatePlan;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageRatePlanKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageRatePlanService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageRatePlanService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManageRatePlanKafka entity) {

        try {
            this.producer.send("finamer-replicate-manage-rate-plan", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManageRatePlanService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}