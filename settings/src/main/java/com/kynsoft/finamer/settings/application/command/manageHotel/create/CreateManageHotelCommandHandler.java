package com.kynsoft.finamer.settings.application.command.manageHotel.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.*;
import com.kynsoft.finamer.settings.domain.rules.manageHotel.ManageHotelCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageHotel.ManageHotelCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.manageHotel.ManageHotelNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.services.*;
import org.springframework.stereotype.Component;

@Component
public class CreateManageHotelCommandHandler implements ICommandHandler<CreateManageHotelCommand> {

    private final IManageHotelService service;

    private final IManagerCountryService countryService;

    private final IManageCityStateService cityStateService;

    private final IManagerCurrencyService currencyService;

    private final IManageTradingCompaniesService tradingCompaniesService;

    private final IManageRegionService manageRegionService;

    public CreateManageHotelCommandHandler(IManageHotelService service, IManagerCountryService countryService, IManageCityStateService cityStateService, IManagerCurrencyService currencyService, IManageTradingCompaniesService tradingCompaniesService, IManageRegionService manageRegionService) {
        this.service = service;
        this.countryService = countryService;
        this.cityStateService = cityStateService;
        this.currencyService = currencyService;
        this.tradingCompaniesService = tradingCompaniesService;
        this.manageRegionService = manageRegionService;
    }

    @Override
    public void handle(CreateManageHotelCommand command) {
        RulesChecker.checkRule(new ManageHotelCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageHotelNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManageHotelCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        ManagerCountryDto countryDto = countryService.findById(command.getManageCountry());
        ManageCityStateDto cityStateDto = cityStateService.findById(command.getManageCityState());
        ManagerCurrencyDto currencyDto = currencyService.findById(command.getManageCurrency());
        ManageTradingCompaniesDto tradingCompaniesDto = tradingCompaniesService.findById(command.getManageTradingCompanies());
        ManageRegionDto regionDto = manageRegionService.findById(command.getManageRegion());

        service.create(new ManageHotelDto(
                command.getId(), command.getCode(), command.getDescription(), command.getStatus(),
                command.getName(), command.getBabelCode(), countryDto, cityStateDto, command.getCity(),
                command.getAddress(), currencyDto, regionDto, tradingCompaniesDto, command.getApplyByTradingCompany(),
                command.getPrefixToInvoice(), command.getIsVirtual(), command.getRequiresFlatRate(),
                command.getIsApplyByVCC()
        ));
    }
}
