package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageCityState;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageCityStateKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageCountryKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageCityStateService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageCityStateService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManageCityStateKafka entity) {

        try {
            this.producer.send("finamer-replicate-manage-city-state", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManageCityStateService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}