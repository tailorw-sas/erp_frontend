package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageVCCTransactionType;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageVCCTransactionTypeKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateManageVCCTransactionTypeService {

    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateManageVCCTransactionTypeService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateManageVCCTransactionTypeKafka entity){
        try {
            this.producer.send("finamer-update-manage-vcc-transaction-type", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateManageVCCTransactionTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
