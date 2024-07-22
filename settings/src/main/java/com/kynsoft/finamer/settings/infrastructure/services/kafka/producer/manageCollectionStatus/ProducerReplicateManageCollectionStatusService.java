package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageCollectionStatus;

import com.kynsof.share.core.domain.kafka.entity.vcc.ReplicateManageCollectionStatusKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageCollectionStatusService {

    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageCollectionStatusService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManageCollectionStatusKafka entity) {

        try {
            this.producer.send("finamer-replicate-manage-collection-status", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManageCollectionStatusService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
