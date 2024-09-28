package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageInvoiceStatus;

import com.kynsof.share.core.domain.kafka.entity.ReplicateDeleteManageInvoiceStatusKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerDeleteManageInvoiceStatusService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerDeleteManageInvoiceStatusService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void delete(ReplicateDeleteManageInvoiceStatusKafka entity) {

        try {
            this.producer.send("finamer-delete-manage-invoice-status", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerDeleteManageInvoiceStatusService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}