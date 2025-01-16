package com.kynsoft.finamer.invoicing.application.command.manageTradingCompanies.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;

import com.kynsoft.finamer.invoicing.domain.dto.ManageCityStateDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageTradingCompaniesDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManagerCountryDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageCityStateService;
import com.kynsoft.finamer.invoicing.domain.services.IManageTradingCompaniesService;

import com.kynsoft.finamer.invoicing.domain.services.IManagerCountryService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageTradingCompaniesCommandHandler
        implements ICommandHandler<CreateManageTradingCompaniesCommand> {

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
        ManagerCountryDto countryDto = this.countryService.findById(command.getCountry());
        ManageCityStateDto cityStateDto = this.cityStateService.findById(command.getCityState());

        service.create(new ManageTradingCompaniesDto(
                command.getId(),
                command.getCode(),
                command.getIsApplyInvoice(),
                null,
                command.getCif(),
                command.getAddress(),
                command.getCompany(),
                command.getStatus(),
                command.getDescription(),
                countryDto,
                cityStateDto,
                command.getCity(),
                command.getZipCode(),
                command.getInnsistCode()
        ));
    }
}
