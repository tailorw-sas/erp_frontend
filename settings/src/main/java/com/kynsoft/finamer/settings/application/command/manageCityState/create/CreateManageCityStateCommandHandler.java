package com.kynsoft.finamer.settings.application.command.manageCityState.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.settings.domain.dto.ManageCityStateDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerCountryDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerTimeZoneDto;
import com.kynsoft.finamer.settings.domain.rules.manageCityState.ManageCityStateCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageCityState.ManageCityStateCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.manageCityState.ManageCityStateNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.services.IManageCityStateService;
import com.kynsoft.finamer.settings.domain.services.IManagerCountryService;
import com.kynsoft.finamer.settings.domain.services.IManagerTimeZoneService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageCityStateCommandHandler implements ICommandHandler<CreateManageCityStateCommand> {

    private final IManageCityStateService service;
    private final IManagerCountryService serviceCountry;
    private final IManagerTimeZoneService serviceTimeZone;

    public CreateManageCityStateCommandHandler(IManageCityStateService service,
                                               IManagerCountryService serviceCountry,
                                               IManagerTimeZoneService serviceTimeZone) {
        this.service = service;
        this.serviceCountry = serviceCountry;
        this.serviceTimeZone = serviceTimeZone;
    }

    @Override
    public void handle(CreateManageCityStateCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getCountry(), "id", "Manage Country ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getTimeZone(), "id", "Manage Time Zone ID cannot be null."));

        RulesChecker.checkRule(new ManageCityStateCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageCityStateNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManageCityStateCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        ManagerCountryDto country = this.serviceCountry.findById(command.getCountry());
        ManagerTimeZoneDto timeZone = this.serviceTimeZone.findById(command.getTimeZone());

        service.create(new ManageCityStateDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getDescription(),
                command.getStatus(),
                country,
                timeZone
        ));
    }
}
