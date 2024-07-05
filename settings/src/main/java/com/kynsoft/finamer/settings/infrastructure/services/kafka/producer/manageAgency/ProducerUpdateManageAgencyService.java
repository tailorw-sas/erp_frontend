package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageAgency;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageAgencyKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateManageAgencyService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateManageAgencyService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateManageAgencyKafka entity) {

        try {
            this.producer.send("finamer-update-manage-agency", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateManageAgencyService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}