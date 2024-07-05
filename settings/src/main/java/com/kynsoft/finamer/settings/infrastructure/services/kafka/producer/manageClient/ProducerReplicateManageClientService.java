package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageClient;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageClientKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageClientService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageClientService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManageClientKafka entity) {

        try {
            this.producer.send("finamer-replicate-manage-client", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManageClientService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}