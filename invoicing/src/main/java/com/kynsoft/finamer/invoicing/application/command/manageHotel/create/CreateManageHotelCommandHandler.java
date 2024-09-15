package com.kynsoft.finamer.invoicing.application.command.manageHotel.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageCityStateDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageTradingCompaniesDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManagerCountryDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageCityStateService;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import com.kynsoft.finamer.invoicing.domain.services.IManageTradingCompaniesService;
import com.kynsoft.finamer.invoicing.domain.services.IManagerCountryService;

import org.springframework.stereotype.Component;

@Component
public class CreateManageHotelCommandHandler implements ICommandHandler<CreateManageHotelCommand> {

    private final IManageHotelService service;
    private final IManageTradingCompaniesService manageTradingCompaniesService;
    private final IManageCityStateService cityStateService;
    private final IManagerCountryService countryService;

    public CreateManageHotelCommandHandler(IManageHotelService service,
                                           IManageTradingCompaniesService manageTradingCompaniesService,
                                           IManageCityStateService cityStateService,
                                           IManagerCountryService countryService) {
        this.service = service;
        this.manageTradingCompaniesService = manageTradingCompaniesService;
        this.cityStateService = cityStateService;
        this.countryService = countryService;

    }

    @Override
    public void handle(CreateManageHotelCommand command) {

        ManageTradingCompaniesDto manageTradingCompaniesDto = command.getManageTradingCompany() != null
                ? this.manageTradingCompaniesService
                        .findById(command.getManageTradingCompany())
                : null;
        ManageCityStateDto cityStateDto = command.getCityState() != null ? this.cityStateService.findById(command.getCityState()) : null;
        ManagerCountryDto countryDto = command.getCountry() != null ? this.countryService.findById(command.getCountry()) : null;

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
                cityStateDto
        ));
    }
}
