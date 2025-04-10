//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageInvoice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.response.UploadFileResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.updateSendStatus.UpdateSendStatusCommand;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.updateSendStatus.UpdateSendStatusRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsumerSendByFtpStatus {
    private static final Logger logger = Logger.getLogger(ConsumerSendByFtpStatus.class.getName());
    private final ObjectMapper objectMapper;
    private final IMediator mediator;

    public ConsumerSendByFtpStatus(ObjectMapper objectMapper, IMediator mediator) {
        this.objectMapper = objectMapper;
        this.mediator = mediator;
    }

    @KafkaListener(
            topics = {"finamer-send-by-ftp-response-topic"},
            groupId = "invoice-update-group"
    )
    @Transactional
    public void consume(ConsumerRecord<String, String> record) {
        try {
            JsonNode rootNode = this.objectMapper.readTree((String)record.value());
            JsonNode filesNode = rootNode.get("files");
            if (filesNode == null || !filesNode.isArray()) {
                logger.log(Level.WARNING, "Invalid message format: 'files' field is missing or not an array");
                return;
            }

            Map<UUID, UploadFileResponse> invoiceResponses = new HashMap();

            for(JsonNode fileNode : filesNode) {
                UUID invoiceId = UUID.fromString(fileNode.get("id").asText());
                UploadFileResponse response = (UploadFileResponse)this.objectMapper.treeToValue(fileNode.get("response"), UploadFileResponse.class);
                invoiceResponses.put(invoiceId, response);
                logger.log(Level.INFO, "✅ Invoice {0} added to the response map with status: {1}", new Object[]{invoiceId, response});
            }

            UpdateSendStatusRequest request = new UpdateSendStatusRequest();
            request.setInvoiceResponses(invoiceResponses);
            this.mediator.send(UpdateSendStatusCommand.fromRequest(request));
        } catch (JsonProcessingException e) {
            logger.log(Level.SEVERE, "Error parsing Kafka message: {0}", e.getMessage());
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error processing invoice responses: {0}", ex.getMessage());
        }

    }
}
