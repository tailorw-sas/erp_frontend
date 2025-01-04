package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageInvoiceType;

import com.kynsof.share.core.domain.kafka.entity.ReplicateDeleteManageInvoiceTypeKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerDeleteManageInvoiceTypeService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerDeleteManageInvoiceTypeService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void delete(ReplicateDeleteManageInvoiceTypeKafka entity) {

        try {
            this.producer.send("finamer-delete-manage-invoice-type", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerDeleteManageInvoiceTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}