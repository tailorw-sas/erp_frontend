package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageEmployee;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageEmployeeKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateManageEmployeeService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateManageEmployeeService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateManageEmployeeKafka entity) {

        try {
            this.producer.send("finamer-update-manage-employee", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateManageEmployeeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}