package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageMerchantCommission;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageMerchantCommissionKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateManageMerchantCommissionService {

    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateManageMerchantCommissionService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateManageMerchantCommissionKafka entity) {

        try {
            this.producer.send("finamer-update-manage-merchant-commission", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateManageMerchantCommissionService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}