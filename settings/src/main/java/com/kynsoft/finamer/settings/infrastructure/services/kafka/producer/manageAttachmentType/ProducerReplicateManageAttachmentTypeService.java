package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageAttachmentType;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageAttachmentTypeKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageAttachmentTypeService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageAttachmentTypeService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManageAttachmentTypeKafka entity) {

        try {
            this.producer.send("finamer-replicate-manage-attachment-type", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManageAttachmentTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}