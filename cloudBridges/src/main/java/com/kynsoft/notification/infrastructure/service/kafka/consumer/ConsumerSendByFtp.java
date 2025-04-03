//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kynsoft.notification.infrastructure.service.kafka.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.response.FileDto;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.notification.application.command.sendByFtp.SendByFtpCommand;
import com.kynsoft.notification.application.command.sendByFtp.SendByFtpRequest;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerSendByFtp {
    private static final Logger logger = Logger.getLogger(ConsumerSendByFtp.class.getName());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final IMediator mediator;

    public ConsumerSendByFtp(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(
            topics = {"finamer-send-by-ftp-topic"},
            groupId = "invoice-processing-group"
    )
    public void consume(ConsumerRecord<String, String> record) {
        try {
            String message = (String)record.value();
            logger.info("Received Kafka message: " + message);
            var data = this.objectMapper.readValue(message, Map.class);
            var fileDtos = this.objectMapper.convertValue(data.get("files"), new TypeReference<List<FileDto>>() {});
            var b2bPartnerData = this.objectMapper.convertValue(data.get("b2bPartner"), Map.class);
            SendByFtpRequest sendByFtpRequest = new SendByFtpRequest();
            sendByFtpRequest.setServer((String)b2bPartnerData.get("ip"));
            sendByFtpRequest.setUserName((String)b2bPartnerData.get("userName"));
            sendByFtpRequest.setPassword((String)b2bPartnerData.get("password"));
            sendByFtpRequest.setUrl((String)b2bPartnerData.get("url"));
            sendByFtpRequest.setBucketName((String)b2bPartnerData.get("bucketName"));
            sendByFtpRequest.setFileDtos(fileDtos);
            SendByFtpCommand command = SendByFtpCommand.fromRequest(sendByFtpRequest);
            this.mediator.send(command);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "❌ Error processing Kafka message", e);
        }

    }
}
