package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageCreditCardType;

import com.kynsof.share.core.domain.kafka.entity.vcc.ReplicateManageCreditCardTypeKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageCreditCardTypeService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageCreditCardTypeService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManageCreditCardTypeKafka entity) {

        try {
            this.producer.send("finamer-replicate-manage-credit-card-type", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManageCreditCardTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}