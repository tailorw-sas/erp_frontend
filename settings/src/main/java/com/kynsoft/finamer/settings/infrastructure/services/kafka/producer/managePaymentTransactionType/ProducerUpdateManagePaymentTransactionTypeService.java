package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.managePaymentTransactionType;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManagePaymentTransactionTypeKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateManagePaymentTransactionTypeService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateManagePaymentTransactionTypeService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateManagePaymentTransactionTypeKafka entity) {

        try {
            this.producer.send("finamer-update-manage-payment-transaction-type", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateManagePaymentTransactionTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}