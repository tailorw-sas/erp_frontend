package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageRatePlan;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageRatePlanKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateManageRatePlanService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateManageRatePlanService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateManageRatePlanKafka entity) {

        try {
            this.producer.send("finamer-update-manage-rate-plan", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateManageRatePlanService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}