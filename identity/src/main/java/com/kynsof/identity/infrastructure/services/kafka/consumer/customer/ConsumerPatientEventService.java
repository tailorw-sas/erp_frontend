package com.kynsof.identity.infrastructure.services.kafka.consumer.customer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.identity.domain.dto.CustomerDto;
import com.kynsof.identity.domain.interfaces.service.ICustomerService;
import com.kynsof.share.core.domain.kafka.entity.CustomerKafka;
import com.kynsof.share.utils.ConfigureTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerPatientEventService {

    @Autowired
    private ICustomerService service;

    @KafkaListener(topics = "finamer_create-custumer", groupId = "identity-patient")
    public void listen(String event) {
        try {
            System.err.println("#######################################################");
            System.err.println("#######################################################");
            System.err.println("ENTRO");
            System.err.println("#######################################################");
            System.err.println("#######################################################");
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(event);

            CustomerKafka eventRead = objectMapper.treeToValue(rootNode.get("data"), CustomerKafka.class);

            this.service.create(new CustomerDto(UUID.fromString(eventRead.getId()), eventRead.getFirstName(), eventRead.getLastName(), eventRead.getEmail(), ConfigureTimeZone.getTimeZone()));
        } catch (JsonProcessingException ex) {
            System.err.println("########################");
            System.err.println("ERROR: " + ex.getMessage());
            Logger.getLogger(ConsumerPatientEventService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
