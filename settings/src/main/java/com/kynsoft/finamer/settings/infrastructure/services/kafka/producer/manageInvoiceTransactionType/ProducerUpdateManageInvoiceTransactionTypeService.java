package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageInvoiceTransactionType;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageInvoiceTransactionTypeKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateManageInvoiceTransactionTypeService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateManageInvoiceTransactionTypeService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateManageInvoiceTransactionTypeKafka entity) {

        try {
           this.producer.send("finamer-update-manage-invoice-transaction-type", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateManageInvoiceTransactionTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}