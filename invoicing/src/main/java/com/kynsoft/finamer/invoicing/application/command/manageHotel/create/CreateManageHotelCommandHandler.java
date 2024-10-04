package com.kynsoft.finamer.invoicing.application.command.manageHotel.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.services.*;

import org.springframework.stereotype.Component;

@Component
public class CreateManageHotelCommandHandler implements ICommandHandler<CreateManageHotelCommand> {

    private final IManageHotelService service;
    private final IManageTradingCompaniesService manageTradingCompaniesService;
    private final IManageCityStateService cityStateService;
    private final IManagerCountryService countryService;
    private final IManageCurrencyService currencyService;

    public CreateManageHotelCommandHandler(IManageHotelService service,
                                           IManageTradingCompaniesService manageTradingCompaniesService,
                                           IManageCityStateService cityStateService,
                                           IManagerCountryService countryService, IManageCurrencyService currencyService) {
        this.service = service;
        this.manageTradingCompaniesService = manageTradingCompaniesService;
        this.cityStateService = cityStateService;
        this.countryService = countryService;

        this.currencyService = currencyService;
    }

    @Override
    public void handle(CreateManageHotelCommand command) {

        ManageTradingCompaniesDto manageTradingCompaniesDto = command.getManageTradingCompany() != null
                ? this.manageTradingCompaniesService
                        .findById(command.getManageTradingCompany())
                : null;
        ManageCityStateDto cityStateDto = command.getCityState() != null ? this.cityStateService.findById(command.getCityState()) : null;
        ManagerCountryDto countryDto = command.getCountry() != null ? this.countryService.findById(command.getCountry()) : null;
        ManageCurrencyDto currencyDto = command.getManageCurrency() != null ? this.currencyService.findById(command.getManageCurrency()) : null;

        service.create(new ManageHotelDto(
                command.getId(), 
                command.getCode(), 
                command.getName(), 
                manageTradingCompaniesDto, 
                null, 
                command.isVirtual(), 
                command.getStatus(), 
                command.isRequiresFlatRate(), 
                command.getAutoApplyCredit(),
                command.getAddress(),
                command.getBabelCode(),
                countryDto,
                cityStateDto,
                command.getCity(),
                currencyDto
        ));
    }
}
