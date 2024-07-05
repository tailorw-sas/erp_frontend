package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageClient;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageClientKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateManageClientService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateManageClientService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateManageClientKafka entity) {

        try {
            this.producer.send("finamer-update-manage-client", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateManageClientService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}