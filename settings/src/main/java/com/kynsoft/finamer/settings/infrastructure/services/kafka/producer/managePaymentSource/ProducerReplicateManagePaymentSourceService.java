package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.managePaymentSource;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManagePaymentSourceKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManagePaymentSourceService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManagePaymentSourceService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManagePaymentSourceKafka entity) {

        try {
            this.producer.send("finamer-replicate-manage-payment-source", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManagePaymentSourceService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}