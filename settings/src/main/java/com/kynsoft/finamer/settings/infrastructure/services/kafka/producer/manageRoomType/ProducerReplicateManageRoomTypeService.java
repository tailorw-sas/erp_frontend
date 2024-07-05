package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageRoomType;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageRoomTypeKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageRoomTypeService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageRoomTypeService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManageRoomTypeKafka entity) {

        try {
            this.producer.send("finamer-replicate-manage-room-type", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManageRoomTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}