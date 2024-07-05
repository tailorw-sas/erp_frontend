package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageInvoiceStatus;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageInvoiceStatusKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateManageInvoiceStatusService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateManageInvoiceStatusService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateManageInvoiceStatusKafka entity) {

        try {
            this.producer.send("finamer-update-manage-invoice-status", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateManageInvoiceStatusService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}