package com.kynsoft.finamer.payment.infrastructure.services.kafka.producer.resourceType;


import com.kynsof.share.core.domain.kafka.entity.update.UpdatePaymentResourceTypeKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateResourceTypeService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateResourceTypeService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdatePaymentResourceTypeKafka entity) {

        try {
            this.producer.send("finamer-update-payment-resource-type", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateResourceTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}