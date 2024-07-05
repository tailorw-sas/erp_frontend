package com.kynsof.identity.infrastructure.services.kafka.producer.user.welcom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.kafka.entity.UserWelcomKafka;
import com.kynsof.share.core.domain.kafka.event.CreateEvent;
import com.kynsof.share.core.domain.kafka.event.EventType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUserWelcomEventService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUserWelcomEventService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(UserWelcomKafka entity) {

        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            String json = objectMapper.writeValueAsString(new CreateEvent<>(entity, EventType.CREATED));
            this.producer.send("finamer-welcom-email", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUserWelcomEventService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}