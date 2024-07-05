package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.managePaymentTransactionType;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManagePaymentTransactionTypeKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManagePaymentTransactionTypeService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManagePaymentTransactionTypeService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManagePaymentTransactionTypeKafka entity) {

        try {
            this.producer.send("finamer-replicate-manage-payment-transaction-type", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManagePaymentTransactionTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}