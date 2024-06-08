package com.kynsoft.notification.application.command.templateEntity.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.notification.domain.dto.MailjetConfigurationDto;
import com.kynsoft.notification.domain.dto.TemplateDto;
import com.kynsoft.notification.domain.service.IMailjetConfigurationService;
import com.kynsoft.notification.domain.service.ITemplateEntityService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

;

@Component
public class CreateTemplateEntityCommandHandler implements ICommandHandler<CreateTemplateEntityCommand> {

    private final IMailjetConfigurationService mailjetConfigurationService;
    private final ITemplateEntityService templateEntityService;


    public CreateTemplateEntityCommandHandler(IMailjetConfigurationService mailjetConfigurationService, ITemplateEntityService templateEntityService) {
        this.mailjetConfigurationService = mailjetConfigurationService;
        this.templateEntityService = templateEntityService;
    }

    @Override
    public void handle(CreateTemplateEntityCommand command) {
       MailjetConfigurationDto mailjetConfigurationDto = mailjetConfigurationService.findById(command.getMailjetConfigId());
        UUID id = templateEntityService.create(new TemplateDto(
                UUID.randomUUID(),
                command.getTemplateCode(),
                command.getName(),
                command.getDescription(),
                mailjetConfigurationDto,
                LocalDateTime.now()
        ));
        command.setId(id);
    }
}
