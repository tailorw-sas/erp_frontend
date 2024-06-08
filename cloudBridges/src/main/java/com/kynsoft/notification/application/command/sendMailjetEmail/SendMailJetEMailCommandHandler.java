package com.kynsoft.notification.application.command.sendMailjetEmail;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.notification.domain.dto.EmailRequest;
import com.kynsoft.notification.domain.dto.TemplateDto;
import com.kynsoft.notification.domain.service.IEmailService;
import com.kynsoft.notification.domain.service.ITemplateEntityService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class SendMailJetEMailCommandHandler implements ICommandHandler<SendMailJetEMailCommand> {

    private final IEmailService service;
    private final ITemplateEntityService templateEntityService;


    public SendMailJetEMailCommandHandler(IEmailService service, ITemplateEntityService templateEntityService) {
        this.service = service;

        this.templateEntityService = templateEntityService;
    }

    @Override
    public void handle(SendMailJetEMailCommand command) {

        TemplateDto templateDto = templateEntityService.findByTemplateCode(String.valueOf(command.getTemplateId()));

        EmailRequest emailRequest = new EmailRequest(command.getRecipientEmail(), command.getMailJetVars(),
                new ArrayList<>(), templateDto.getName(), command.getTemplateId());
        try {
            this.service.sendEmailMailjet(emailRequest, templateDto.getMailjetConfigurationDto().getMailjetApiKey(),
                    templateDto.getMailjetConfigurationDto().getMailjetApiSecret(), templateDto.getMailjetConfigurationDto().getFromEmail(),
                    templateDto.getMailjetConfigurationDto().getFromName());

        } catch (Exception ignored) {

        }
    }
}
