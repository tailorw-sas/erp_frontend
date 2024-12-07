package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageTimeZone;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageTimeZoneKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageTimeZoneService {

    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageTimeZoneService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManageTimeZoneKafka entity) {

        try {
            this.producer.send("finamer-replicate-manage-time-zone", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManageTimeZoneService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
