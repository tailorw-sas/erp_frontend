package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageCreditCardType;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageCreditCardTypeKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateManageCreditCardTypeService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateManageCreditCardTypeService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateManageCreditCardTypeKafka entity) {

        try {
            this.producer.send("finamer-update-manage-credit-card-type", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateManageCreditCardTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}