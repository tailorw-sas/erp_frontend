package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageCountry;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageB2BPartnerTypeKafka;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageCountryKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateManageCountryService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateManageCountryService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateManageCountryKafka entity) {

        try {
            this.producer.send("finamer-update-manage-country", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateManageCountryService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}