package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.managePaymentSource;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManagePaymentSourceKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateManagePaymentSourceService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateManagePaymentSourceService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateManagePaymentSourceKafka entity) {

        try {
            this.producer.send("finamer-update-manage-payment-source", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateManagePaymentSourceService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}