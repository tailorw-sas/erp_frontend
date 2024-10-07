package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageCurrency;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageCurrencyKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateManageCurrencyService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateManageCurrencyService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(ReplicateManageCurrencyKafka entity) {

        try {
            this.producer.send("finamer-update-manage-currency", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateManageCurrencyService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}