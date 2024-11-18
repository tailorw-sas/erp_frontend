package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoice.autoRec;

import com.kynsof.share.core.domain.kafka.entity.ManageInvoiceAutoRecKafka;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ProducerManageInvoiceAutoRecService {

    private final KafkaTemplate<String, Object> producer;

    public ProducerManageInvoiceAutoRecService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ManageInvoiceDto entity) {

        this.producer.send(
                "finamer-replicate-manage-invoice-auto-rec", 
                new ManageInvoiceAutoRecKafka(entity.getId())
        );
    }

}
