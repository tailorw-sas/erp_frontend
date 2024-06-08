package com.kynsoft.notification.application.command.mailjetConfiguration.create;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.notification.domain.dto.MailjetConfigurationDto;
import com.kynsoft.notification.domain.service.IMailjetConfigurationService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class CreateMailjetConfigurationCommandHandler implements ICommandHandler<CreateMailjetConfigurationCommand> {

    private final IMailjetConfigurationService mailjetConfigurationService;


    public CreateMailjetConfigurationCommandHandler(IMailjetConfigurationService mailjetConfigurationService) {
        this.mailjetConfigurationService = mailjetConfigurationService;
    }

    @Override
    public void handle(CreateMailjetConfigurationCommand command) {

        UUID id = mailjetConfigurationService.create(new MailjetConfigurationDto(
                UUID.randomUUID(),
                command.getMailjetApiSecret(),
                command.getMailjetApiKey(),
                command.getFromEmail(),
                command.getFromName(),
                LocalDateTime.now()
        ));
        command.setId(id);
    }
}
