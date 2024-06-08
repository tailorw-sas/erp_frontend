package com.kynsoft.notification.application.command.templateEntity.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.notification.domain.dto.MailjetConfigurationDto;
import com.kynsoft.notification.domain.dto.TemplateDto;
import com.kynsoft.notification.domain.service.IMailjetConfigurationService;
import com.kynsoft.notification.domain.service.ITemplateEntityService;
import com.kynsoft.notification.infrastructure.entity.TemplateEntity;
import org.springframework.stereotype.Component;

@Component
public class UpdateTemplateEntityCommandHandler implements ICommandHandler<UpdateTemplateEntityCommand> {

    private final IMailjetConfigurationService mailjetConfigurationService;
    private final ITemplateEntityService templateEntityService;

    public UpdateTemplateEntityCommandHandler(IMailjetConfigurationService allergyService, ITemplateEntityService templateEntityService) {
        this.mailjetConfigurationService = allergyService;
        this.templateEntityService = templateEntityService;
    }

    @Override
    public void handle(UpdateTemplateEntityCommand command) {
        TemplateDto templateDto = templateEntityService.findById(command.getId());
        MailjetConfigurationDto mailjetConfigurationDto = this.mailjetConfigurationService.findById(command.getMailjetConfigId());
        if (command.getTemplateCode() != null) templateDto.setTemplateCode(command.getTemplateCode());
        if (command.getName() != null) templateDto.setName(command.getName());
        if (command.getDescription() != null) templateDto.setDescription(command.getDescription());
        if (command.getMailjetConfigId() != null) templateDto.setMailjetConfigurationDto(mailjetConfigurationDto);
        templateEntityService.update(new TemplateEntity(templateDto));
    }
}
