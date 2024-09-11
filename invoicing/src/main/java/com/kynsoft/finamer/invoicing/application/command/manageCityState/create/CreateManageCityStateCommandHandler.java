package com.kynsoft.finamer.invoicing.application.command.manageCityState.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.invoicing.domain.dto.ManageCityStateDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManagerCountryDto;
import com.kynsoft.finamer.invoicing.domain.rules.manageCityState.ManageCityStateCodeMustBeUniqueRule;
import com.kynsoft.finamer.invoicing.domain.rules.manageCityState.ManageCityStateCodeSizeRule;
import com.kynsoft.finamer.invoicing.domain.rules.manageCityState.ManageCityStateNameMustBeNullRule;
import com.kynsoft.finamer.invoicing.domain.services.IManageCityStateService;
import com.kynsoft.finamer.invoicing.domain.services.IManagerCountryService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageCityStateCommandHandler implements ICommandHandler<CreateManageCityStateCommand> {

    private final IManageCityStateService service;
    private final IManagerCountryService serviceCountry;

    public CreateManageCityStateCommandHandler(IManageCityStateService service,
                                               IManagerCountryService serviceCountry) {
        this.service = service;
        this.serviceCountry = serviceCountry;
    }

    @Override
    public void handle(CreateManageCityStateCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getCountry(), "id", "Manage Country ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getTimeZone(), "id", "Manage Time Zone ID cannot be null."));

        RulesChecker.checkRule(new ManageCityStateCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageCityStateNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManageCityStateCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        ManagerCountryDto country = this.serviceCountry.findById(command.getCountry());


        service.create(new ManageCityStateDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getDescription(),
                command.getStatus(),
                country
        ));
    }
}
