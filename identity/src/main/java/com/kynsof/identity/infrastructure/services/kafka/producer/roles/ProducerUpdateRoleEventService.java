package com.kynsof.identity.infrastructure.services.kafka.producer.roles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.identity.domain.dto.RoleDto;
import com.kynsof.share.core.domain.kafka.entity.RoleKafka;
import com.kynsof.share.core.domain.kafka.event.CreateEvent;
import com.kynsof.share.core.domain.kafka.event.EventType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateRoleEventService {
    private final KafkaTemplate<String, String> producer;

    public ProducerUpdateRoleEventService(KafkaTemplate<String, String> producer) {
        this.producer = producer;
    }

    @Async
    public void update(RoleDto roleDto) {

        try {
            RoleKafka event = new RoleKafka(roleDto.getId(), roleDto.getName(), roleDto.getDescription());

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(new CreateEvent<>(event, EventType.UPDATED));

            this.producer.send("finamer_role", json);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(ProducerUpdateRoleEventService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}