package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageBankAccount;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageBankAccountKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateManageBankAccount {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateManageBankAccount(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateManageBankAccountKafka entity) {

        try {
            this.producer.send("finamer-update-manage-bank-account", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateManageBankAccount.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}