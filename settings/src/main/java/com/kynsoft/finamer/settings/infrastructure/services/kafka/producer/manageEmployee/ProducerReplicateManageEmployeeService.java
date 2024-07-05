package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageEmployee;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageEmployeeKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageEmployeeService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageEmployeeService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManageEmployeeKafka entity) {

        try {
            this.producer.send("finamer-replicate-manage-employee", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManageEmployeeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}