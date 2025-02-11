package com.kynsoft.notification.infrastructure.service.kafka.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.kafka.entity.UserOtpKafka;
import com.kynsof.share.core.domain.kafka.event.CreateEvent;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.notification.application.command.sendMailjetEmail.SendMailJetEMailCommand;
import com.kynsof.share.core.application.mailjet.MailJetRecipient;
import com.kynsof.share.core.application.mailjet.MailJetVar;
import com.kynsoft.notification.domain.dto.MailjetTemplateEnum;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerTriggerPasswordResetEventService {

    @Value("${mail.template.opt}")
    private int otpTemplate;

    private final IMediator mediator;

    public ConsumerTriggerPasswordResetEventService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-otp", groupId = "otp")
    public void listen(String message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            CreateEvent<UserOtpKafka> event = mapper.readValue(message, new TypeReference<CreateEvent<UserOtpKafka>>() {});
            UserOtpKafka otpKafka = event.getData();

            List<MailJetRecipient> mailJetRecipients = new ArrayList<>();
            mailJetRecipients.add(new MailJetRecipient(otpKafka.getEmail(),otpKafka.getName()));

            SendMailJetEMailCommand command = getSendMailJetEMailCommand(otpKafka, mailJetRecipients, this.otpTemplate);
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerTriggerPasswordResetEventService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static @NotNull SendMailJetEMailCommand getSendMailJetEMailCommand(UserOtpKafka otpKafka, List<MailJetRecipient> mailJetRecipients, int otpTemplate) {
        List<MailJetVar> vars = Arrays.asList(
                new MailJetVar("username", otpKafka.getName()),
                new MailJetVar("otp_token", otpKafka.getOtpCode())
        );

        return new SendMailJetEMailCommand(mailJetRecipients, vars, new ArrayList<>(),
                "Código de verificación",otpTemplate);
    }

}
