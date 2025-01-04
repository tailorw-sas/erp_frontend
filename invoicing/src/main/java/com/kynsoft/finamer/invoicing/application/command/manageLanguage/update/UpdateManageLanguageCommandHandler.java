package com.kynsoft.finamer.invoicing.application.command.manageLanguage.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageLanguageDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageLanguageService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageLanguageCommandHandler implements ICommandHandler<UpdateManageLanguageCommand> {

    private final IManageLanguageService service;

    public UpdateManageLanguageCommandHandler(IManageLanguageService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageLanguageCommand command) {
        ManageLanguageDto dto = service.findById(command.getId());
        dto.setName(command.getName());
        dto.setDefaults(command.getDefaults());
        dto.setStatus(command.getStatus());
        this.service.update(dto);
    }
}
