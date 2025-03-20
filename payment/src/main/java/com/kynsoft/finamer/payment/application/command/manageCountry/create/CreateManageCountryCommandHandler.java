package com.kynsoft.finamer.payment.application.command.manageCountry.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.ManageCountryDto;
import com.kynsoft.finamer.payment.domain.dto.ManageLanguageDto;
import com.kynsoft.finamer.payment.domain.services.IManageCountryService;
import com.kynsoft.finamer.payment.domain.services.IManageLanguageService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageCountryCommandHandler implements ICommandHandler<CreateManageCountryCommand> {

    private final IManageCountryService service;
    private final IManageLanguageService manageLanguageService;

    public CreateManageCountryCommandHandler(IManageCountryService service,
                                             IManageLanguageService manageLanguageService){
        this.service = service;
        this.manageLanguageService = manageLanguageService;
    }

    @Override
    public void handle(CreateManageCountryCommand command) {

        ManageLanguageDto language = manageLanguageService.findById(command.getLanguage());

        ManageCountryDto country = new ManageCountryDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getDescription(),
                command.getIsDefault(),
                command.getStatus(),
                language,
                command.getIso3()
        );

        service.create(country);
    }
}
