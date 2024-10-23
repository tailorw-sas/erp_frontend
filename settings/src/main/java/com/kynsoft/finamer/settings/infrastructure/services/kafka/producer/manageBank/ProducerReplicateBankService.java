package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageBank;

import com.kynsof.share.core.domain.kafka.entity.ManageBankKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateBankService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateBankService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void replicate(ManageBankKafka entity) {

        try {
            this.producer.send("finamer-replicate-manage-bank", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateBankService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}