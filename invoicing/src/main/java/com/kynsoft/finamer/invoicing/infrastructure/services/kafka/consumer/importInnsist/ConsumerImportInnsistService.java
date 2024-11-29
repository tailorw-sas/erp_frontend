package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.importInnsist;

import com.kynsof.share.core.domain.kafka.entity.importInnsist.ImportInnsistKafka;
import com.kynsoft.finamer.invoicing.infrastructure.services.ImportInnsistServiceImpl;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerImportInnsistService {

    private final ImportInnsistServiceImpl service;

    public ConsumerImportInnsistService(ImportInnsistServiceImpl service) {
        this.service = service;
    }

    @KafkaListener(topics = "finamer-import-innsist", groupId = "invoicing-entity-replica")
    public void listen(ImportInnsistKafka objKafka) {
        try {
            this.service.createInvoiceFromGroupedBooking(objKafka);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerImportInnsistService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
