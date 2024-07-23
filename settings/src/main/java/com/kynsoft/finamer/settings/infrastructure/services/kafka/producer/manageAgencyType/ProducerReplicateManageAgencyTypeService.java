package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageAgencyType;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageAgencyTypeKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageAgencyTypeService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageAgencyTypeService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManageAgencyTypeKafka entity) {

        try {
            this.producer.send("finamer-replicate-manage-agency-type", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManageAgencyTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}