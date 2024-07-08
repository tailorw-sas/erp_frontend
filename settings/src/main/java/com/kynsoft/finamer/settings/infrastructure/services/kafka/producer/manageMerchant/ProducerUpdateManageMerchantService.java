package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageMerchant;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageMerchantKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateManageMerchantService {

    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateManageMerchantService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateManageMerchantKafka entity) {

        try {
            this.producer.send("finamer-update-manage-merchant", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateManageMerchantService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
