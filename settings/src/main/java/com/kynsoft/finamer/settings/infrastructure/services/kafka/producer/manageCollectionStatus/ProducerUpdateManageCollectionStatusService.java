package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageCollectionStatus;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageCollectionStatusKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateManageCollectionStatusService {

    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateManageCollectionStatusService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateManageCollectionStatusKafka entity) {

        try {
            this.producer.send("finamer-update-manage-collection-status", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateManageCollectionStatusService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
