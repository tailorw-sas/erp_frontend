package com.kynsof.identity.infrastructure.services.kafka.producer.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.kafka.entity.UserKafka;
import com.kynsof.share.core.domain.kafka.event.CreateEvent;
import com.kynsof.share.core.domain.kafka.event.EventType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerRegisterUserEventService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerRegisterUserEventService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(UserKafka entity) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(new CreateEvent<>(entity, EventType.CREATED));
            this.producer.send("finamer-user", json);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(ProducerRegisterUserEventService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}