package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.importInnsist.response;

import com.kynsof.share.core.domain.kafka.entity.importInnsist.ImportInnsistResponseKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerResponseImportInnsistService {

    private final KafkaTemplate<String, Object> producer;

    public ProducerResponseImportInnsistService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ImportInnsistResponseKafka entity) {
        try {
            this.producer.send("finamer-import-innsist-response", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerResponseImportInnsistService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
