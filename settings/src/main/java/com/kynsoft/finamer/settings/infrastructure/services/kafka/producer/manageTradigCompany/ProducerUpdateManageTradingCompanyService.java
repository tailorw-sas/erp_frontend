package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageTradigCompany;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageTradingCompanyKafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateManageTradingCompanyService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateManageTradingCompanyService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateManageTradingCompanyKafka entity) {

        try {
            this.producer.send("finamer-update-manage-trading-company", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateManageTradingCompanyService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}