package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageMerchantCommission;

import com.kynsof.share.core.domain.kafka.entity.vcc.ReplicateManageMerchantCommissionKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageMerchantCommissionService {

    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageMerchantCommissionService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManageMerchantCommissionKafka entity) {

        try {
            this.producer.send("finamer-replicate-manage-merchant-commission", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManageMerchantCommissionService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}