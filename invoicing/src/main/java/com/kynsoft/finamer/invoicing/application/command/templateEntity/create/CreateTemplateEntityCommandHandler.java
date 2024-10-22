package com.kynsoft.finamer.invoicing.application.command.templateEntity.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.TemplateDto;
import com.kynsoft.finamer.invoicing.domain.services.ITemplateEntityService;
import org.springframework.stereotype.Component;

import java.util.UUID;

;

@Component
public class CreateTemplateEntityCommandHandler implements ICommandHandler<CreateTemplateEntityCommand> {

    private final ITemplateEntityService templateEntityService;


    public CreateTemplateEntityCommandHandler(ITemplateEntityService templateEntityService) {
        this.templateEntityService = templateEntityService;
    }

    @Override
    public void handle(CreateTemplateEntityCommand command) {
        UUID id = templateEntityService.create(new TemplateDto(
                command.getId(),
                command.getTemplateCode(),
                command.getName(),
                command.getLanguageCode(),
                command.getType()
        ));
        command.setId(id);
    }
}
