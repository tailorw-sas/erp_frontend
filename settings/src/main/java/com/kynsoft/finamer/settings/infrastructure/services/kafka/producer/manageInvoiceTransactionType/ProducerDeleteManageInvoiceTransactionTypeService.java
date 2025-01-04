package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageInvoiceTransactionType;

import com.kynsof.share.core.domain.kafka.entity.ObjectIdKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerDeleteManageInvoiceTransactionTypeService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerDeleteManageInvoiceTransactionTypeService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void delete(ObjectIdKafka entity) {

        try {
           this.producer.send("finamer-delete-manage-invoice-transaction-type", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerDeleteManageInvoiceTransactionTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}