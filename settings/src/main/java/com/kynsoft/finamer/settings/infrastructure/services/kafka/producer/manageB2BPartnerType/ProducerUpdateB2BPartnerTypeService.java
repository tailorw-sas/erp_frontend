package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageB2BPartnerType;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageB2BPartnerTypeKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateB2BPartnerTypeService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateB2BPartnerTypeService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateManageB2BPartnerTypeKafka entity) {

        try {
            this.producer.send("finamer-update-b2b-partner-type", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateB2BPartnerTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}