package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.producer.manageVCCTransactionType;

import com.kynsof.share.core.domain.kafka.entity.vcc.ReplicateManageVCCTransactionTypeKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageVCCTransactionTypeService {

    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageVCCTransactionTypeService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManageVCCTransactionTypeKafka entity){
        try {
            this.producer.send("finamer-replicate-manage-vcc-transaction-type", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManageVCCTransactionTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
