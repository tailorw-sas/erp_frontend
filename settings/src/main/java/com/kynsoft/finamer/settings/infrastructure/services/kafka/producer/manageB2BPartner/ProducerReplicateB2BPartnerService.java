package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageB2BPartner;

import com.kynsof.share.core.domain.kafka.entity.ReplicateB2BPartnerKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageAgencyKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateB2BPartnerService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateB2BPartnerService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateB2BPartnerKafka entity) {

        try {
            this.producer.send("finamer-replicate-b2b-partner", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateB2BPartnerService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}