package com.kynsoft.finamer.creditcard.application.command.manageLanguage.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageLanguageDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageLanguageService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageLanguageCommandHandler implements ICommandHandler<CreateManageLanguageCommand> {

    private final IManageLanguageService service;

    public CreateManageLanguageCommandHandler(IManageLanguageService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageLanguageCommand command) {
        service.create(new ManageLanguageDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getDefaults()
        ));
    }
}
