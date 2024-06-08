package com.kynsof.identity.infrastructure.services.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.identity.domain.dto.CustomerDto;
import com.kynsof.identity.domain.interfaces.service.ICustomerService;
import com.kynsof.share.core.domain.kafka.entity.UserKafka;
import com.kynsof.share.core.domain.kafka.event.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUserEventService {

    @Autowired
    private ICustomerService service;

    // Ejemplo de un método listener
    @KafkaListener(topics = "finamer_user", groupId = "identity-user")
    public void listen(String event) {
        try {

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(event);

            UserKafka eventRead = objectMapper.treeToValue(rootNode.get("data"), UserKafka.class);
            EventType eventType = objectMapper.treeToValue(rootNode.get("type"), EventType.class);

            if (eventType.equals(EventType.CREATED)) {
                //Definir accion
                this.service.create(new CustomerDto(UUID.fromString(eventRead.getId()), eventRead.getFirstname(),
                        eventRead.getLastname(), eventRead.getEmail(), LocalDateTime.now()));
            }
            if (eventType.equals(EventType.UPDATED)) {
                //Definir accion
                this.service.update(new CustomerDto(UUID.fromString(eventRead.getId()), eventRead.getFirstname(),
                        eventRead.getLastname(), eventRead.getEmail(), LocalDateTime.now()));
            }
        } catch (JsonProcessingException ex) {
            Logger.getLogger(ConsumerUserEventService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
