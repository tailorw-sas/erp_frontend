package com.kynsoft.finamer.insis.infrastructure.services.kafka.producer.manageRoomCategory;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageRoomCategoryKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageRoomCategoryService {

    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageRoomCategoryService(KafkaTemplate<String, Object> producer){
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManageRoomCategoryKafka entity){
        try{
            producer.send("finamer-innsist-replicate-manage-room-category", entity);
        }catch (Exception ex){
            Logger.getLogger(ProducerReplicateManageRoomCategoryService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
