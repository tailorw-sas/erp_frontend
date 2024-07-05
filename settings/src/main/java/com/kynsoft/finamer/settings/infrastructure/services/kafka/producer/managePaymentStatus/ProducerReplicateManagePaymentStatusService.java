package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.managePaymentStatus;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManagePaymentStatusKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManagePaymentStatusService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManagePaymentStatusService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManagePaymentStatusKafka entity) {

        try {
            this.producer.send("finamer-replicate-manage-payment-status", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManagePaymentStatusService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}