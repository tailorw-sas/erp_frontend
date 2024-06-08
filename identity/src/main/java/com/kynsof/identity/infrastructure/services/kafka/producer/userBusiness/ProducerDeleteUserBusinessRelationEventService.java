package com.kynsof.identity.infrastructure.services.kafka.producer.userBusiness;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.kafka.entity.DeleteUserBusinessKafka;
import com.kynsof.share.core.domain.kafka.event.CreateEvent;
import com.kynsof.share.core.domain.kafka.event.EventType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerDeleteUserBusinessRelationEventService {

    private final KafkaTemplate<String, String> producer;

    public ProducerDeleteUserBusinessRelationEventService(KafkaTemplate<String, String> producer) {
        this.producer = producer;
    }

    public void delete(UUID userId, UUID businessId) {

        try {
            DeleteUserBusinessKafka event = new DeleteUserBusinessKafka(
                    userId,
                    businessId
            );

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(new CreateEvent<>(event, EventType.DELETED));
            this.producer.send("finamer_user-business-delete", json);

        } catch (JsonProcessingException ex) {
            Logger.getLogger(ProducerDeleteUserBusinessRelationEventService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
