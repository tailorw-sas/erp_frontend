package com.kynsof.identity.infrastructure.services.kafka.producer.permission;

import com.kynsof.share.core.domain.kafka.entity.ReplicatePermissionKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManagePermissionService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManagePermissionService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicatePermissionKafka entity) {

        try {
            this.producer.send("finamer-replicate-manage-permission", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManagePermissionService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}