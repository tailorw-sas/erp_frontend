package com.kynsof.identity.infrastructure.services.kafka.producer.module;

import com.kynsof.share.core.domain.kafka.entity.ReplicateModuleKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageModuleService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageModuleService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateModuleKafka entity) {

        try {
            this.producer.send("finamer-replicate-manage-module", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManageModuleService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}