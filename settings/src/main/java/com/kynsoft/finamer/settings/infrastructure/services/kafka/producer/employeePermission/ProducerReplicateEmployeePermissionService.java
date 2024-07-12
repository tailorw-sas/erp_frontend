package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.employeePermission;

import com.kynsof.share.core.domain.kafka.entity.ReplicateEmployeePermissionKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateEmployeePermissionService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateEmployeePermissionService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateEmployeePermissionKafka entity) {

        try {
            this.producer.send("finamer-replicate-employee-permission", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateEmployeePermissionService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}