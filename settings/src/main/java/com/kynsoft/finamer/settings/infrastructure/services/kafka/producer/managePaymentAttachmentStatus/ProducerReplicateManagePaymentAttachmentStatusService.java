package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.managePaymentAttachmentStatus;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManagePaymentAttachmentStatusKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManagePaymentAttachmentStatusService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManagePaymentAttachmentStatusService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManagePaymentAttachmentStatusKafka entity) {

        try {
            this.producer.send("finamer-replicate-manage-payment-attachment-status", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManagePaymentAttachmentStatusService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}