package com.kynsof.identity.infrastructure.services.kafka.producer.user;

import com.kynsof.share.core.domain.kafka.entity.UserResetPasswordKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUserResetPasswordEventService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUserResetPasswordEventService(KafkaTemplate<String, Object> producer){
        this.producer = producer;
    }

    @Async
    public void create(UserResetPasswordKafka entity){
        try{
            this.producer.send("finamer-reset-password", entity);
        }catch (Exception ex){
            Logger.getLogger(ProducerUserResetPasswordEventService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
