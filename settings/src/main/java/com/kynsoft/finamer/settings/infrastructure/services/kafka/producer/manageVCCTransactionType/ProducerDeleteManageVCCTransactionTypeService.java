package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageVCCTransactionType;

import com.kynsof.share.core.domain.kafka.entity.ObjectIdKafka;
import com.kynsof.share.core.domain.kafka.entity.vcc.ReplicateManageVCCTransactionTypeKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerDeleteManageVCCTransactionTypeService {

    private final KafkaTemplate<String, Object> producer;

    public ProducerDeleteManageVCCTransactionTypeService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void delete(ObjectIdKafka entity){
        try {
            this.producer.send("finamer-delete-manage-vcc-transaction-type", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerDeleteManageVCCTransactionTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
