package com.kynsoft.finamer.settings.application.command.manageTradingCompanies.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageCityStateDto;
import com.kynsoft.finamer.settings.domain.dto.ManageTradingCompaniesDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerCountryDto;
import com.kynsoft.finamer.settings.domain.rules.manageTradingCompanies.ManageTradingCompaniesCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageTradingCompanies.ManageTradingCompaniesCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.manageTradingCompanies.ManageTradingCompaniesNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.services.IManageCityStateService;
import com.kynsoft.finamer.settings.domain.services.IManageTradingCompaniesService;
import com.kynsoft.finamer.settings.domain.services.IManagerCountryService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageTradingCompaniesCommandHandler implements ICommandHandler<CreateManageTradingCompaniesCommand> {

    private final IManageTradingCompaniesService service;

    private final IManagerCountryService countryService;

    private final IManageCityStateService cityStateService;

    public CreateManageTradingCompaniesCommandHandler(IManageTradingCompaniesService service, IManagerCountryService countryService, IManageCityStateService cityStateService) {
        this.service = service;
        this.countryService = countryService;
        this.cityStateService = cityStateService;
    }

    @Override
    public void handle(CreateManageTradingCompaniesCommand command) {
        RulesChecker.checkRule(new ManageTradingCompaniesCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageTradingCompaniesNameMustBeNullRule(command.getCompany()));
        RulesChecker.checkRule(new ManageTradingCompaniesCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        ManagerCountryDto countryDto = countryService.findById(command.getCountry());
        ManageCityStateDto cityStateDto = cityStateService.findById(command.getCityState());

        service.create(new ManageTradingCompaniesDto(
                command.getId(),
                command.getCode(),
                command.getDescription(),
                command.getStatus(),
                command.getCompany(),
                command.getCif(),
                command.getAddress(),
                countryDto,
                cityStateDto,
                command.getCity(),
                command.getZipCode(),
                command.getInnsistCode(),
                command.getIsApplyInvoice()
        ));
    }
}
