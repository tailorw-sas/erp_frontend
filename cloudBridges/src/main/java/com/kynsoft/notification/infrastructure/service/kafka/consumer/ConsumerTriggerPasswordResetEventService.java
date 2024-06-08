package com.kynsoft.notification.infrastructure.service.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.kafka.entity.UserOtpKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.notification.application.command.sendMailjetEmail.SendMailJetEMailCommand;
import com.kynsoft.notification.domain.dto.MailJetRecipient;
import com.kynsoft.notification.domain.dto.MailJetVar;
import com.kynsoft.notification.domain.dto.MailjetTemplateEnum;
import org.jetbrains.annotations.NotNull;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerTriggerPasswordResetEventService {

    private final IMediator mediator;

    public ConsumerTriggerPasswordResetEventService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer_otp", groupId = "otp")
    public void listen(String event) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(event);

            UserOtpKafka otpKafka = objectMapper.treeToValue(rootNode.get("data"), UserOtpKafka.class);
            List<MailJetRecipient> mailJetRecipients = new ArrayList<>();
            mailJetRecipients.add(new MailJetRecipient(otpKafka.getEmail(),otpKafka.getName()));

            SendMailJetEMailCommand command = getSendMailJetEMailCommand(otpKafka, mailJetRecipients);
            mediator.send(command);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(ConsumerTriggerPasswordResetEventService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static @NotNull SendMailJetEMailCommand getSendMailJetEMailCommand(UserOtpKafka otpKafka, List<MailJetRecipient> mailJetRecipients) {
        List<MailJetVar> vars = Arrays.asList(
                new MailJetVar("username", otpKafka.getName()),
                new MailJetVar("otp_token", otpKafka.getOtpCode())
        );

        int  templateId = MailjetTemplateEnum.OTP.getTemplateId();

        return new SendMailJetEMailCommand(mailJetRecipients, vars, new ArrayList<>(),
                "Código de verificación",templateId);
    }

}
