package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageB2BPartnerType;

import com.kynsof.share.core.domain.kafka.entity.ReplicateB2BPartnerTypeKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateB2BPartnerTypeService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateB2BPartnerTypeService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateB2BPartnerTypeKafka entity) {

        try {
            this.producer.send("finamer-replicate-b2b-partner-type", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateB2BPartnerTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}