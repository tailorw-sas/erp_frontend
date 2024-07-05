package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageInvoiceType;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageInvoiceTypeKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageInvoiceTypeService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageInvoiceTypeService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManageInvoiceTypeKafka entity) {

        try {
            this.producer.send("finamer-replicate-manage-invoice-type", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManageInvoiceTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}