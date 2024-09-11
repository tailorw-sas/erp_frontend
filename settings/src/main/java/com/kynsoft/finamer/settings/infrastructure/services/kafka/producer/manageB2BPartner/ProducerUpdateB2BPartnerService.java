package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageB2BPartner;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageAgencyKafka;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageB2BPartnerKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateB2BPartnerService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateB2BPartnerService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateManageB2BPartnerKafka entity) {

        try {
            this.producer.send("finamer-update-b2b-partner", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateB2BPartnerService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}