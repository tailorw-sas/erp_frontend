package com.kynsoft.finamer.payment.infrastructure.services.kafka.producer.resourceType;


import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentResourceTypeKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateResourceTypeService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateResourceTypeService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicatePaymentResourceTypeKafka entity) {

        try {
            this.producer.send("finamer-replicate-payment-resource-type", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateResourceTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}