package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageAttachmentType;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageAttachmentTypeKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateManageAttachmentTypeService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateManageAttachmentTypeService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateManageAttachmentTypeKafka entity) {

        try {
            this.producer.send("finamer-update-manage-attachment-type", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateManageAttachmentTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}