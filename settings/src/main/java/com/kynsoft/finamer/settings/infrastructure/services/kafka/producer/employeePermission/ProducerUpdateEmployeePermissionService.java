package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.employeePermission;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateEmployeePermissionKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateEmployeePermissionService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateEmployeePermissionService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateEmployeePermissionKafka entity) {

        try {
            this.producer.send("finamer-update-employee-permission", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateEmployeePermissionService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}