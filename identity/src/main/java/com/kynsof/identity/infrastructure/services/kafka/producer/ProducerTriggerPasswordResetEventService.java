package com.kynsof.identity.infrastructure.services.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.kafka.entity.UserOtpKafka;
import com.kynsof.share.core.domain.kafka.event.CreateEvent;
import com.kynsof.share.core.domain.kafka.event.EventType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerTriggerPasswordResetEventService {
    private final KafkaTemplate<String, String> producer;

    public ProducerTriggerPasswordResetEventService(KafkaTemplate<String, String> producer) {
        this.producer = producer;
    }

    @Async
    public void create(UserOtpKafka entity) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(new CreateEvent<>(entity, EventType.CREATED));
            this.producer.send("finamer_otp", json);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(ProducerTriggerPasswordResetEventService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}