package com.kynsoft.finamer.payment.infrastructure.services.kafka.consumer.manageinvoiceAutoRec;

import com.kynsof.share.core.domain.kafka.entity.ManageInvoiceAutoRecKafka;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.payment.domain.services.IManageInvoiceService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerManageInvoiceAutoRecService {

    private final IManageInvoiceService service;

    public ConsumerManageInvoiceAutoRecService(IManageInvoiceService service) {
        this.service = service;
    }

    @KafkaListener(topics = "finamer-replicate-manage-invoice-auto-rec", groupId = "payment-entity-replica")
    public void listen(ManageInvoiceAutoRecKafka objKafka) {
        ManageInvoiceDto invoiceDto = this.service.findById(objKafka.getId());
        invoiceDto.setAutoRec(Boolean.TRUE);
        this.service.update(invoiceDto);
    }

}
