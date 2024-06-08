package com.kynsoft.notification.application.command.mailjetConfiguration.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.notification.domain.dto.MailjetConfigurationDto;
import com.kynsoft.notification.domain.service.IMailjetConfigurationService;
import com.kynsoft.notification.infrastructure.entity.MailjetConfiguration;
import org.springframework.stereotype.Component;

@Component
public class UpdateMailjetConfigurationCommandHandler implements ICommandHandler<UpdateMailjetConfigurationCommand> {

    private final IMailjetConfigurationService mailjetConfigurationService;

    public UpdateMailjetConfigurationCommandHandler(IMailjetConfigurationService allergyService) {
        this.mailjetConfigurationService = allergyService;
    }

    @Override
    public void handle(UpdateMailjetConfigurationCommand command) {

        MailjetConfigurationDto mailjetConfigurationDto = this.mailjetConfigurationService.findById(command.getId());
        if (command.getMailjetApiKey() != null) mailjetConfigurationDto.setMailjetApiKey(command.getMailjetApiKey());
        if (command.getMailjetApiSecret() != null) mailjetConfigurationDto.setMailjetApiSecret(command.getMailjetApiSecret());
        if (command.getFromEmail() != null) mailjetConfigurationDto.setFromEmail(command.getFromEmail());
        if (command.getFromName() != null) mailjetConfigurationDto.setFromName(command.getFromName());
        mailjetConfigurationService.update(new MailjetConfiguration(mailjetConfigurationDto));
    }
}
