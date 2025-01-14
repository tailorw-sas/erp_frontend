package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.importInnsist.response.undoImport;

import java.util.UUID;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerResponseUndoImportInnsistService {

    private final KafkaTemplate<String, Object> producer;

    public ProducerResponseUndoImportInnsistService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(UUID invoice) {
        try {
            this.producer.send("finamer-undo-import-innsist-response", invoice);
        } catch (Exception ex) {
            Logger.getLogger(ProducerResponseUndoImportInnsistService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
