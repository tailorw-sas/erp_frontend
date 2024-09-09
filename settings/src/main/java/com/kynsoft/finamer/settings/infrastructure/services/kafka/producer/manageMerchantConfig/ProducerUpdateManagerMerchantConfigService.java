package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageMerchantConfig;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageMerchantConfigKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateManagerMerchantConfigService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateManagerMerchantConfigService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateManageMerchantConfigKafka entity) {

        try {
            this.producer.send("finamer-update-manage-merchant-config", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateManagerMerchantConfigService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
