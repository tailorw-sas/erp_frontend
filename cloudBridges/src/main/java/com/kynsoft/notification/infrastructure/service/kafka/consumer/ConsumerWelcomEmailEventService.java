package com.kynsoft.notification.infrastructure.service.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.kafka.entity.UserWelcomKafka;
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
public class ConsumerWelcomEmailEventService {

    private final IMediator mediator;

    public ConsumerWelcomEmailEventService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-welcom-email", groupId = "notification-welcom")
    public void listen(String event) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(event);

            //TODO yannier capturar el evento con el nombre del usuario, el usuario y la contraseña para enviar por correo
            UserWelcomKafka otpKafka = objectMapper.treeToValue(rootNode, UserWelcomKafka.class);
            List<MailJetRecipient> mailJetRecipients = new ArrayList<>();
            mailJetRecipients.add(new MailJetRecipient(otpKafka.getEmail(),otpKafka.getFullName()));

            SendMailJetEMailCommand command = getSendMailJetEMailCommand(otpKafka, mailJetRecipients);
            mediator.send(command);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(ConsumerWelcomEmailEventService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static @NotNull SendMailJetEMailCommand getSendMailJetEMailCommand(UserWelcomKafka otpKafka, List<MailJetRecipient> mailJetRecipients) {
        List<MailJetVar> vars = Arrays.asList(
                new MailJetVar("user_name", otpKafka.getUserName()),
                new MailJetVar("name", otpKafka.getFullName()),
                new MailJetVar("temp_password", otpKafka.getPassword()),
                new MailJetVar("temp_email", otpKafka.getEmail())
        );

        int  templateId = MailjetTemplateEnum.WELCOM.getTemplateId();

        return new SendMailJetEMailCommand(mailJetRecipients, vars, new ArrayList<>(),
                "Correo de Bienvenida",templateId);
    }

}
