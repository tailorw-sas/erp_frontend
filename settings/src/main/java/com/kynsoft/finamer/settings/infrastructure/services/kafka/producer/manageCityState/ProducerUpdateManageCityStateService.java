package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageCityState;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageCityStateKafka;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageCityStateKafka;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageCountryKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateManageCityStateService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateManageCityStateService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateManageCityStateKafka entity) {

        try {
            this.producer.send("finamer-update-manage-city-state", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateManageCityStateService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}