package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageBankAccount;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageBankAccountKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageBankAccount {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageBankAccount(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManageBankAccountKafka entity) {

        try {
            this.producer.send("finamer-replicate-manage-bank-account", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManageBankAccount.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}