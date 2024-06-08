package com.kynsoft.finamer.settings.application.command.managerCountry.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.settings.domain.dto.ManagerCountryDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerLanguageDto;
import com.kynsoft.finamer.settings.domain.rules.managerCountry.ManagerCountryCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.managerCountry.ManagerCountryCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.managerCountry.ManagerCountryDialCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.managerCountry.ManagerCountryNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.rules.managerCountry.ManagerCountryNameMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.services.IManagerCountryService;
import com.kynsoft.finamer.settings.domain.services.IManagerLanguageService;
import org.springframework.stereotype.Component;

@Component
public class CreateManagerCountryCommandHandler implements ICommandHandler<CreateManagerCountryCommand> {

    private final IManagerLanguageService serviceLanguage;
    private final IManagerCountryService service;

    public CreateManagerCountryCommandHandler(IManagerCountryService service,
            IManagerLanguageService serviceLanguage) {
        this.service = service;
        this.serviceLanguage = serviceLanguage;
    }

    @Override
    public void handle(CreateManagerCountryCommand command) {
        RulesChecker.checkRule(new ManagerCountryCodeSizeRule(command.getCode()));
        //RulesChecker.checkRule(new ManagerCountryDialCodeSizeRule(command.getDialCode()));
        RulesChecker.checkRule(new ManagerCountryNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManagerLanguage(), "id", "Manager Language ID cannot be null."));
        RulesChecker.checkRule(new ManagerCountryCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));
        RulesChecker.checkRule(new ManagerCountryNameMustBeUniqueRule(this.service, command.getName(), command.getId()));

        ManagerLanguageDto languageDto = this.serviceLanguage.findById(command.getManagerLanguage());

        service.create(new ManagerCountryDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getDescription(),
                command.getDialCode(),
                command.getIso3(),
                command.getIsDefault(),
                languageDto,
                command.getStatus()
        ));
    }
}
