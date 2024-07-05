package com.kynsof.identity.infrastructure.services.kafka.producer.business;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.identity.domain.dto.BusinessDto;
import com.kynsof.share.core.domain.kafka.entity.BusinessKafka;
import com.kynsof.share.core.domain.kafka.event.CreateEvent;
import com.kynsof.share.core.domain.kafka.event.EventType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerCreateBusinessEventService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerCreateBusinessEventService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    public void create(BusinessDto entity) {

        try {
            BusinessKafka event = new BusinessKafka(entity.getId(), entity.getName(), entity.getLatitude(), entity.getLongitude(), entity.getAddress(),
                    entity.getLogo());
//
//            ObjectMapper objectMapper = new ObjectMapper();
//            String json = objectMapper.writeValueAsString(new CreateEvent<>(event, EventType.CREATED));
            this.producer.send("finamer-business", event);
        } catch (Exception ex) {
            Logger.getLogger(ProducerCreateBusinessEventService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}