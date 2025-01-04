package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageCurrency;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageCurrencyKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageCurrencyService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageCurrencyService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManageCurrencyKafka entity) {

        try {
            this.producer.send("finamer-replicate-manage-currency", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManageCurrencyService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}