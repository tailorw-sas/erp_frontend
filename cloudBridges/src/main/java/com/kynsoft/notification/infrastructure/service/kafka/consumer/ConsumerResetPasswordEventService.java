package com.kynsoft.notification.infrastructure.service.kafka.consumer;

import com.kynsof.share.core.application.mailjet.MailJetRecipient;
import com.kynsof.share.core.application.mailjet.MailJetVar;
import com.kynsof.share.core.domain.kafka.entity.UserResetPasswordKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.notification.application.command.sendMailjetEmail.SendMailJetEMailCommand;
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
public class ConsumerResetPasswordEventService {

    @Value("${mail.template.password.reset}")
    private int resetPasswordTemplate;

    private final IMediator mediator;

    public ConsumerResetPasswordEventService(IMediator mediator){
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-reset-password", groupId = "reset-password")
    public void listen(UserResetPasswordKafka otpKafka){
        try {
            List<MailJetRecipient> mailJetRecipients = new ArrayList<>();
            mailJetRecipients.add(new MailJetRecipient(otpKafka.getEmail(),otpKafka.getFullName()));

            SendMailJetEMailCommand command = getSendMailJetEMailCommand(otpKafka, mailJetRecipients, this.resetPasswordTemplate);
            mediator.send(command);
        }catch (Exception ex){
            Logger.getLogger(ConsumerResetPasswordEventService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static @NotNull SendMailJetEMailCommand getSendMailJetEMailCommand(UserResetPasswordKafka otpKafka, List<MailJetRecipient> mailJetRecipients, int template) {
        List<MailJetVar> vars = Arrays.asList(
                new MailJetVar("name", otpKafka.getFullName()),
                new MailJetVar("temp_password", otpKafka.getPassword())
        );

        return new SendMailJetEMailCommand(mailJetRecipients, vars, new ArrayList<>(),
                "Correo de Bienvenida",template);
    }
}
