package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageAgency;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageAgencyKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageAgencyService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageAgencyService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManageAgencyKafka entity) {

        try {
            this.producer.send("finamer-replicate-manage-agency", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManageAgencyService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}