package com.kynsof.share.core.domain.kafka.producer.email;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.kafka.config.SimpleEmailKafka;
import com.kynsof.share.core.domain.kafka.event.CreateEvent;
import com.kynsof.share.core.domain.kafka.event.EventType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerEmailEventService {
    private final KafkaTemplate<String, String> producer;

    public ProducerEmailEventService(KafkaTemplate<String, String> producer) {
        this.producer = producer;
    }

    public void create(SimpleEmailKafka entity) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(new CreateEvent<>(entity, EventType.CREATED));

            this.producer.send("email", json);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(ProducerEmailEventService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}