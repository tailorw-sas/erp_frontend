package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageMerchantConfig;

import com.kynsof.share.core.domain.kafka.entity.vcc.ReplicateManageMerchantKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageMerchantConfigService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageMerchantConfigService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManageMerchantKafka entity) {

        try {
            this.producer.send("finamer-replicate-manage-merchant-config", entity);
        } catch (Exception ex) {
            Logger.getLogger(com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageMerchant.ProducerReplicateManageMerchantService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
