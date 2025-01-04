package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageAgencyContact;

import com.kynsof.share.core.domain.kafka.entity.ManageAgencyContactKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageAgencyContactService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageAgencyContactService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ManageAgencyContactKafka entity) {

        try {
            this.producer.send("finamer-replicate-manage-agency-contact", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManageAgencyContactService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}