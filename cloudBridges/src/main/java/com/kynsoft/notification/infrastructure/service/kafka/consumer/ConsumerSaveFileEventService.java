package com.kynsoft.notification.infrastructure.service.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.kafka.entity.FileKafka;
import com.kynsof.share.core.domain.kafka.event.EventType;
import com.kynsof.share.core.infrastructure.util.CustomMultipartFile;
import com.kynsoft.notification.domain.dto.AFileDto;
import com.kynsoft.notification.domain.service.IAFileService;
import com.kynsoft.notification.infrastructure.service.AmazonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerSaveFileEventService {

    @Autowired
    private AmazonClient amazonClient;

    @Autowired
    private IAFileService fileService;

    // Ejemplo de un método listener
    @KafkaListener(topics = "finamer-file", groupId = "file")
    public void listen(String event) {
        try {

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(event);

            FileKafka eventRead = objectMapper.treeToValue(rootNode.get("data"), FileKafka.class);
            EventType eventType = objectMapper.treeToValue(rootNode.get("type"), EventType.class);

            if (eventType.equals(EventType.CREATED)) {
                //Definir accion
                System.err.println("#######################################################");
                System.err.println("#######################################################");
                System.err.println("CREATED");
                System.err.println("#######################################################");
                System.err.println("#######################################################");

                MultipartFile file = new CustomMultipartFile(eventRead.getFile(), eventRead.getFileName());
                try {
                    String fileUrl = amazonClient.save(file, eventRead.getFileName());
                    this.fileService.create(new AFileDto(eventRead.getId(), eventRead.getFileName(), eventRead.getMicroServiceName(), fileUrl));
                } catch (IOException ex) {
                    Logger.getLogger(ConsumerSaveFileEventService.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
            if (eventType.equals(EventType.DELETED)) {
                System.err.println("#######################################################");
                System.err.println("#######################################################");
                System.err.println("DELETED");
                System.err.println("#######################################################");
                System.err.println("#######################################################");
                    AFileDto deleteFile = this.fileService.findById(eventRead.getId());
                    amazonClient.delete(deleteFile.getUrl());
                    this.fileService.delete(deleteFile);

            }
            if (eventType.equals(EventType.UPDATED)) {
                //Definir accion
                System.err.println("#######################################################");
                System.err.println("#######################################################");
                System.err.println("SE EJECUTA UN EVENTO DE ACTUALIZACION");
                System.err.println("#######################################################");
                System.err.println("#######################################################");

                //this.service.update(new PatientDto(UUID.fromString(eventRead.getId()), "", eventRead.getFirstname(), eventRead.getLastname(), "", PatientStatus.ACTIVE));
            }

        } catch (JsonProcessingException ex) {
            Logger.getLogger(ConsumerSaveFileEventService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
