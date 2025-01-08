package com.tailorw.tcaInnsist.infrastructure.service.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class ProducerRequestReplicateTcaConfigurationPropertiesService {
    private final KafkaTemplate<String, Object> producer;

    @Async
    public void create(){
        try {
            producer.send("finamer-tcaInnsist-replicate-all-tcaConfigurationProperties", "Update all TcaConfigurationProperties");
            System.out.println("Mensaje enviado al kafka: Update all TcaConfigurationProperties");
        } catch (Exception ex) {
            Logger.getLogger(ProducerRequestReplicateTcaConfigurationPropertiesService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
