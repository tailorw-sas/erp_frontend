package com.kynsoft.finamer.settings.application.command.managerLanguage.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManagerLanguageDto;
import com.kynsoft.finamer.settings.domain.services.IManagerLanguageService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManagerLanguageCommandHandler implements ICommandHandler<DeleteManagerLanguageCommand> {

    private final IManagerLanguageService service;

    public DeleteManagerLanguageCommandHandler(IManagerLanguageService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManagerLanguageCommand command) {
        ManagerLanguageDto dto = service.findById(command.getId());

        service.delete(dto);
    }
}
