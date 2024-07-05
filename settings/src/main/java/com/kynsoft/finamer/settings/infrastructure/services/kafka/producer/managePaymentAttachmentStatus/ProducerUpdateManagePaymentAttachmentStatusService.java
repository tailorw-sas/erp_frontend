package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.managePaymentAttachmentStatus;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManagePaymentAttachmentStatusKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateManagePaymentAttachmentStatusService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateManagePaymentAttachmentStatusService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateManagePaymentAttachmentStatusKafka entity) {

        try {
            this.producer.send("finamer-update-payment-attachment-status", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateManagePaymentAttachmentStatusService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}