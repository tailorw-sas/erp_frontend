package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageTransactionStatus;

import com.kynsof.share.core.domain.kafka.entity.vcc.ReplicateManageTransactionStatusKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageTransactionStatusService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageTransactionStatusService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManageTransactionStatusKafka entity) {

        try {
            this.producer.send("finamer-replicate-manage-transaction-status", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManageTransactionStatusService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}