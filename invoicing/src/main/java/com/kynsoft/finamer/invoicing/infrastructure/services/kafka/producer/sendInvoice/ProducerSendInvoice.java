package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.sendInvoice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.kynsof.share.core.domain.response.FileDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManagerB2BPartnerDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProducerSendInvoice {
    private final KafkaTemplate<String, Object> producer;
    private static final Logger logger = Logger.getLogger(ProducerSendInvoice.class.getName());

    public ProducerSendInvoice(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(List<FileDto> fileDtos, ManagerB2BPartnerDto b2BPartner) {
        if (fileDtos == null || fileDtos.isEmpty() || b2BPartner == null) {
            logger.log(Level.WARNING, "No data to send to Kafka");
            return;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            int systemLoad = Runtime.getRuntime().availableProcessors();
            int batchSize = Math.max(10, systemLoad * 5);

            List<List<FileDto>> batches = Lists.partition(fileDtos, batchSize);
            Map<String, String> b2bPartnerData = Map.of(
                    "ip", b2BPartner.getIp(),
                    "userName", b2BPartner.getUserName(),
                    "password", b2BPartner.getPassword(),
                    "url", b2BPartner.getUrl()
            );

            List<CompletableFuture<Void>> batchFutures = batches.stream()
                    .map(batch -> CompletableFuture.runAsync(() -> {
                        try {
                            Map<String, Object> message = Map.of(
                                    "files", batch,
                                    "b2bPartner", b2bPartnerData
                            );

                            String jsonMessage = objectMapper.writeValueAsString(message);
                            producer.send("finamer-send-invoice-topic", jsonMessage);

                        } catch (JsonProcessingException e) {
                            logger.log(Level.SEVERE, "Error serializing batch message: {0}", e.getMessage());
                        }
                    }))
                    .toList();

            CompletableFuture.allOf(batchFutures.toArray(new CompletableFuture[0])).join();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error in Kafka batch processing", ex);
        }
    }
}