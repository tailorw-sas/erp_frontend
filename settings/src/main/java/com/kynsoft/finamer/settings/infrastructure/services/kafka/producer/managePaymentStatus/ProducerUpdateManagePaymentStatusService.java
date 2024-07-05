package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.managePaymentStatus;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManagePaymentStatusKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateManagePaymentStatusService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateManagePaymentStatusService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateManagePaymentStatusKafka entity) {

        try {
            this.producer.send("finamer-update-manage-payment-status", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateManagePaymentStatusService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}