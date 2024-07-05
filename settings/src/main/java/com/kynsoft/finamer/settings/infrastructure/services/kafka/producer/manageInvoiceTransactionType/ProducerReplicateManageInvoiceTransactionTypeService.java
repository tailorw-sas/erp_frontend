package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageInvoiceTransactionType;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageInvoiceTransactionTypeKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageInvoiceTransactionTypeService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageInvoiceTransactionTypeService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManageInvoiceTransactionTypeKafka entity) {

        try {
           this.producer.send("finamer-replicate-manage-invoice-transaction-type", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManageInvoiceTransactionTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}