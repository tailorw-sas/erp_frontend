package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoiceStatus;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageInvoiceStatusKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageInvoiceStatusService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageInvoiceStatusService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManageInvoiceStatusKafka entity) {

        try {
            this.producer.send("finamer-replicate-manage-invoice-status", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManageInvoiceStatusService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}