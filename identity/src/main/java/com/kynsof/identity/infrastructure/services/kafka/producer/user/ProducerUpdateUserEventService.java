package com.kynsof.identity.infrastructure.services.kafka.producer.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.identity.application.command.auth.registry.UserRequest;
import com.kynsof.share.core.domain.kafka.entity.UserKafka;
import com.kynsof.share.core.domain.kafka.event.CreateEvent;
import com.kynsof.share.core.domain.kafka.event.EventType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateUserEventService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateUserEventService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UserRequest entity, String clientId) {

        try {
            UserKafka event = new UserKafka(clientId, entity.getUserName(), entity.getEmail(), entity.getName(), entity.getLastName(), "", "", "", "");

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(new CreateEvent<>(event, EventType.UPDATED));

            this.producer.send("finamer-user", json);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(ProducerUpdateUserEventService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}