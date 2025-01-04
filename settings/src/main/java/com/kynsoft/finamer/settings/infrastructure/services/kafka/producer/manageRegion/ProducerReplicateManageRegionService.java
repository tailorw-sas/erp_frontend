package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageRegion;

import com.kynsof.share.core.domain.kafka.entity.ManageAgencyContactKafka;
import com.kynsof.share.core.domain.kafka.entity.ManageRegionKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageRegionService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageRegionService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ManageRegionKafka entity) {

        try {
            this.producer.send("finamer-replicate-manage-region", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManageRegionService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}