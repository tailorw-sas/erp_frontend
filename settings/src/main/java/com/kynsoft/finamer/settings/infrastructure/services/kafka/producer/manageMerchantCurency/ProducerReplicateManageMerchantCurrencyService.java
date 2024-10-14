package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageMerchantCurency;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageMerchantCurrencyKafka;
import com.kynsoft.finamer.settings.infrastructure.identity.ManagerMerchantConfig;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageMerchantCurrencyService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageMerchantCurrencyService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManageMerchantCurrencyKafka entity) {

        try {
            this.producer.send("finamer-replicate-manage-merchant-currency", entity);
        } catch (Exception ex) {
            Logger.getLogger(ManagerMerchantConfig.class.getName()).log(Level.SEVERE, "Error producer topic", ex);
        }
    }
}
