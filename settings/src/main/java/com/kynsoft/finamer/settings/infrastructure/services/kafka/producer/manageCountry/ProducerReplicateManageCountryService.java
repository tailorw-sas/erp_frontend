package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageCountry;

import com.kynsof.share.core.domain.kafka.entity.ReplicateB2BPartnerTypeKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageCountryKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageCountryService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageCountryService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManageCountryKafka entity) {

        try {
            this.producer.send("finamer-replicate-manage-countru", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManageCountryService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}