package com.kynsoft.finamer.invoicing.application.command.manageCountry.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.invoicing.domain.dto.ManagerCountryDto;
import com.kynsoft.finamer.invoicing.domain.rules.managerCountry.ManagerCountryCodeMustBeUniqueRule;
import com.kynsoft.finamer.invoicing.domain.rules.managerCountry.ManagerCountryCodeSizeRule;
import com.kynsoft.finamer.invoicing.domain.rules.managerCountry.ManagerCountryNameMustBeNullRule;
import com.kynsoft.finamer.invoicing.domain.rules.managerCountry.ManagerCountryNameMustBeUniqueRule;
import com.kynsoft.finamer.invoicing.domain.services.IManagerCountryService;
import org.springframework.stereotype.Component;

@Component
public class CreateManagerCountryCommandHandler implements ICommandHandler<CreateManagerCountryCommand> {

    private final IManagerCountryService service;

    public CreateManagerCountryCommandHandler(IManagerCountryService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManagerCountryCommand command) {
        RulesChecker.checkRule(new ManagerCountryCodeSizeRule(command.getCode()));
        //RulesChecker.checkRule(new ManagerCountryDialCodeSizeRule(command.getDialCode()));
        RulesChecker.checkRule(new ManagerCountryNameMustBeNullRule(command.getName()));
        //RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManagerLanguage(), "id", "Manager Language ID cannot be null."));
        RulesChecker.checkRule(new ManagerCountryCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));
        RulesChecker.checkRule(new ManagerCountryNameMustBeUniqueRule(this.service, command.getName(), command.getId()));


        service.create(new ManagerCountryDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getDescription(),
                command.getIsDefault(),
                command.getStatus()
        ));
    }
}
