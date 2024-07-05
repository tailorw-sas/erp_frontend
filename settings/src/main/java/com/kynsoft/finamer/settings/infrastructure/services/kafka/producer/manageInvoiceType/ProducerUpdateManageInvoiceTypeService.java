package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageInvoiceType;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageInvoiceTypeKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateManageInvoiceTypeService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateManageInvoiceTypeService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateManageInvoiceTypeKafka entity) {

        try {
            this.producer.send("finamer-update-manage-invoice-type", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateManageInvoiceTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}