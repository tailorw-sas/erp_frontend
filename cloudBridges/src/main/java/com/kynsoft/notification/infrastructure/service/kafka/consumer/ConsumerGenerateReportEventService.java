package com.kynsoft.notification.infrastructure.service.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.kafka.entity.GenerateReportKafka;
import com.kynsof.share.core.domain.kafka.event.EventType;
import com.kynsoft.notification.domain.service.IEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerGenerateReportEventService {

    @Autowired
    private IEmailService service;

    // Ejemplo de un método listener
    @KafkaListener(topics = "finamer_generate-report", groupId = "generate-report")
    public void listen(String event) {
        try {
            System.err.println("#######################################################");
            System.err.println("#######################################################");
            System.err.println("SE EJECUTA GENERAR REPORT");
            System.err.println("#######################################################");
            System.err.println("#######################################################");

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(event);

            GenerateReportKafka eventRead = objectMapper.treeToValue(rootNode.get("data"), GenerateReportKafka.class);
            EventType eventType = objectMapper.treeToValue(rootNode.get("type"), EventType.class);

            System.err.println("#######################################################");
            System.err.println("#######################################################");
            System.err.println("SE EJENERA REPORT");
            System.err.println("#######################################################");
            System.err.println("#######################################################");

        } catch (JsonProcessingException ex) {
            Logger.getLogger(ConsumerGenerateReportEventService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
