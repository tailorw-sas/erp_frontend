package com.kynsoft.notification.infrastructure.service.kafka.producer;

import com.kynsof.share.core.domain.kafka.entity.TemplateKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateTemplateEntityService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateTemplateEntityService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void replicate(TemplateKafka entity) {

        try {
            this.producer.send("finamer-replicate-template-entity", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateTemplateEntityService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}