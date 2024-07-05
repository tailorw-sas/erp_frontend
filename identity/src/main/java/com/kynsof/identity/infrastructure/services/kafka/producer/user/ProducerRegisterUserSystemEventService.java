package com.kynsof.identity.infrastructure.services.kafka.producer.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.identity.application.command.auth.registrySystemUser.UserSystemKycloackRequest;
import com.kynsof.share.core.domain.kafka.entity.UserSystemKafka;
import com.kynsof.share.core.domain.kafka.event.CreateEvent;
import com.kynsof.share.core.domain.kafka.event.EventType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerRegisterUserSystemEventService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerRegisterUserSystemEventService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(UserSystemKycloackRequest entity, String clientId, String image) {

        try {
            UserSystemKafka event = new UserSystemKafka(
                    UUID.fromString(clientId),
                    entity.getUserName(),
                    entity.getEmail(), 
                    entity.getName(),
                    entity.getLastName(),
                    image,
                    entity.getUserType()

            );

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(new CreateEvent<>(event, EventType.CREATED));
            this.producer.send("finamer-user-system", json);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(ProducerRegisterUserSystemEventService.class.getName()).log(Level.SEVERE, null, ex);
       }
    }

}