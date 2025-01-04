package com.kynsoft.finamer.payment.application.command.templateEntity.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.TemplateDto;
import com.kynsoft.finamer.payment.domain.services.ITemplateEntityService;
import com.kynsoft.finamer.payment.infrastructure.identity.TemplateEntity;
import org.springframework.stereotype.Component;

@Component
public class UpdateTemplateEntityCommandHandler implements ICommandHandler<UpdateTemplateEntityCommand> {

    private final ITemplateEntityService templateEntityService;

    public UpdateTemplateEntityCommandHandler(ITemplateEntityService templateEntityService) {
        this.templateEntityService = templateEntityService;
    }

    @Override
    public void handle(UpdateTemplateEntityCommand command) {
        TemplateDto templateDto = templateEntityService.findById(command.getId());

        if (command.getTemplateCode() != null) templateDto.setTemplateCode(command.getTemplateCode());
        if (command.getName() != null) templateDto.setName(command.getName());
        if (command.getLanguageCode() != null) templateDto.setLanguageCode(command.getLanguageCode());
        if (command.getType() != null) templateDto.setType(command.getType());

        templateEntityService.update(new TemplateEntity(templateDto));
    }
}
