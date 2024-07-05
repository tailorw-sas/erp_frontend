package com.kynsof.identity.infrastructure.services.kafka.producer.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.identity.domain.dto.UserSystemDto;
import com.kynsof.share.core.domain.kafka.entity.UserSystemKafka;
import com.kynsof.share.core.domain.kafka.event.CreateEvent;
import com.kynsof.share.core.domain.kafka.event.EventType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUserSystemUpdateEventService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUserSystemUpdateEventService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(UserSystemDto entity) {

        try {
            UserSystemKafka event = new UserSystemKafka(
                    entity.getId(),
                    entity.getUserName(),
                    entity.getEmail(), 
                    entity.getName(), 
                    entity.getLastName(),
                    entity.getImage(),
                    entity.getUserType()
            );

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(new CreateEvent<>(event, EventType.CREATED));
            this.producer.send("finamer-user-system-update", json);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(ProducerUserSystemUpdateEventService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}