package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageTransactionStatus;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageTransactionStatusKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateManageTransactionStatusService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateManageTransactionStatusService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateManageTransactionStatusKafka entity) {

        try {
            this.producer.send("finamer-update-manage-transaction-status", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateManageTransactionStatusService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}