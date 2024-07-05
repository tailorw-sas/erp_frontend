package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageLanguage;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageLanguageKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateManageLanguageService {

    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateManageLanguageService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateManageLanguageKafka entity) {

        try {
            this.producer.send("finamer-update-manage-language", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateManageLanguageService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
