package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageContact;

import com.kynsof.share.core.domain.kafka.entity.ManageContactKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageContactService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageContactService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ManageContactKafka entity) {

        try {
            this.producer.send("finamer-replicate-manage-contact", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManageContactService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}