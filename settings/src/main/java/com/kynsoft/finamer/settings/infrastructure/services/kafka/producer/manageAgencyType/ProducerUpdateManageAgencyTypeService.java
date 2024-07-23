package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageAgencyType;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageAgencyTypeKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateManageAgencyTypeService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateManageAgencyTypeService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateManageAgencyTypeKafka entity) {

        try {
            this.producer.send("finamer-update-manage-agency-type", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateManageAgencyTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}