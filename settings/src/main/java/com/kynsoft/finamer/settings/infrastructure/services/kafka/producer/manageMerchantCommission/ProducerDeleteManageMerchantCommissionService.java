package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageMerchantCommission;

import com.kynsof.share.core.domain.kafka.entity.vcc.DeleteManageMerchantCommissionKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerDeleteManageMerchantCommissionService {

    private final KafkaTemplate<String, Object> producer;

    public ProducerDeleteManageMerchantCommissionService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void delete(DeleteManageMerchantCommissionKafka entity) {

        try {
            this.producer.send("finamer-delete-manage-merchant-commission", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerDeleteManageMerchantCommissionService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
