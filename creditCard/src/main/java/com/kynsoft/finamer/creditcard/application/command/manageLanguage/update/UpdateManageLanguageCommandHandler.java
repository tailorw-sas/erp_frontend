package com.kynsoft.finamer.creditcard.application.command.manageLanguage.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageLanguageDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageLanguageService;
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
        this.service.update(dto);
    }
}
