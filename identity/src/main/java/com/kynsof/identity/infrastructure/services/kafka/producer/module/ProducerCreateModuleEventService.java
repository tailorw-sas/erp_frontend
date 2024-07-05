package com.kynsof.identity.infrastructure.services.kafka.producer.module;

import com.kynsof.share.core.domain.kafka.entity.ReplicateModuleKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerCreateModuleEventService {

    private final KafkaTemplate<String, Object> producer;

    public ProducerCreateModuleEventService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    public void create(ReplicateModuleKafka entity) {

        try {
            this.producer.send("finamer-module", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerCreateModuleEventService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
