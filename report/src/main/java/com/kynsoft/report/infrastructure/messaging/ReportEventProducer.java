package com.kynsoft.report.infrastructure.messaging;

import com.kynsoft.report.domain.events.ReportProcessingEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

@Service
public class ReportEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(ReportEventProducer.class);
    private static final String REPORT_PROCESSING_TOPIC = "report.processing.events";


    private KafkaTemplate<String, Object> kafkaTemplate;

    public ReportEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishReportProcessingEvent(ReportProcessingEvent event) {
        try {
            String key = event.getServerRequestId();

            logger.info("Publishing report processing event (modern) | ServerID: {} | ClientID: {} | EventType: {}",
                    event.getServerRequestId(), event.getClientRequestId(), event.getEventType());

            kafkaTemplate.send(REPORT_PROCESSING_TOPIC, key, event)
                    .thenAccept(result -> {
                        logger.info("Event published successfully | ServerID: {} | ClientID: {} | Partition: {} | Offset: {}",
                                event.getServerRequestId(),
                                event.getClientRequestId(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    })
                    .exceptionally(ex -> {
                        logger.error("Failed to publish event | ServerID: {} | ClientID: {}",
                                event.getServerRequestId(), event.getClientRequestId(), ex);
                        return null;
                    });

        } catch (Exception e) {
            logger.error("Error publishing report processing event | ServerID: {} | ClientID: {}",
                    event.getServerRequestId(), event.getClientRequestId(), e);
            throw new RuntimeException("Error publishing report processing event", e);
        }
    }
}
