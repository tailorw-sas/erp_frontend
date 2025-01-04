package com.kynsoft.finamer.invoicing.application.command.manageCountry.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageLanguageDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManagerCountryDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageLanguageService;
import com.kynsoft.finamer.invoicing.domain.services.IManagerCountryService;
import org.springframework.stereotype.Component;

@Component
public class CreateManagerCountryCommandHandler implements ICommandHandler<CreateManagerCountryCommand> {

    private final IManagerCountryService service;
    private final IManageLanguageService languageService;

    public CreateManagerCountryCommandHandler(IManagerCountryService service,
                                              IManageLanguageService languageService) {
        this.service = service;
        this.languageService = languageService;
    }

    @Override
    public void handle(CreateManagerCountryCommand command) {

        System.err.println("#################################################");
        System.err.println("#################################################");
        System.err.println("#################################################");
        System.err.println("#################################################: " + command.getManagerLanguage());
        System.err.println("#################################################");
        System.err.println("#################################################");
        ManageLanguageDto languageDto = this.languageService.findById(command.getManagerLanguage());
        service.create(new ManagerCountryDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getDescription(),
                command.getIsDefault(),
                command.getStatus(),
                languageDto
        ));
    }
}
