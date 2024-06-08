package com.kynsof.identity.infrastructure.services.kafka.consumer.customer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.identity.domain.dto.CustomerDto;
import com.kynsof.identity.domain.interfaces.service.ICustomerService;
import com.kynsof.share.core.domain.kafka.entity.UpdateCustomerKafka;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateCustomerEventService {

    @Autowired
    private ICustomerService service;

    @KafkaListener(topics = "finamer_update-custumer", groupId = "identity-patient-update")
    public void listen(String event) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(event);

            UpdateCustomerKafka eventRead = objectMapper.treeToValue(rootNode.get("data"), UpdateCustomerKafka.class);

            CustomerDto update = this.service.findById(UUID.fromString(eventRead.getId()));
            update.setFirstName(eventRead.getFirstName());
            update.setLastName(eventRead.getLastName());
            this.service.update(update);
        } catch (JsonProcessingException ex) {
            System.err.println("########################");
            System.err.println("ERROR: " + ex.getMessage());
            Logger.getLogger(ConsumerUpdateCustomerEventService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
