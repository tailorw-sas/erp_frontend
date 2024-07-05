package com.kynsoft.notification.infrastructure.service.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsof.share.core.domain.request.SendEmailRequest;
import com.kynsoft.notification.application.command.sendMailjetEmail.SendMailJetEMailCommand;
import com.kynsoft.notification.domain.dto.MailJetAttachment;
import com.kynsoft.notification.domain.dto.MailJetRecipient;
import com.kynsoft.notification.domain.dto.MailJetVar;
import com.kynsoft.notification.domain.dto.MailjetTemplateEnum;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerEmailEventService {
    private final IMediator mediator;

    public ConsumerEmailEventService(IMediator service) {
        this.mediator = service;
    }

    @KafkaListener(topics = "finamer-email-confirmation-cite", groupId = "cloud-bridges")
    public void listen(String event) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(event);

            SendEmailRequest sendEmailRequest = objectMapper.treeToValue(rootNode.get("data"), SendEmailRequest.class);
            List<MailJetRecipient> mailJetRecipients = new ArrayList<>();
            mailJetRecipients.add(new MailJetRecipient(sendEmailRequest.getToEmail(), sendEmailRequest.getName()));

            List<MailJetVar> mailJetVars = List.of(
                    new MailJetVar("name", sendEmailRequest.getName())
            );
            List<MailJetAttachment> mailJetAttachments = new ArrayList<>();
            mailJetAttachments.add(new MailJetAttachment("application/pdf", "cite.pdf", sendEmailRequest.getFile()));
            int templateId = MailjetTemplateEnum.EMAIL_CONFIRMATION_CITE.getTemplateId();

            SendMailJetEMailCommand command = new SendMailJetEMailCommand(mailJetRecipients, mailJetVars, mailJetAttachments,
                    sendEmailRequest.getSubject(), templateId);
            mediator.send(command);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(ConsumerEmailEventService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
