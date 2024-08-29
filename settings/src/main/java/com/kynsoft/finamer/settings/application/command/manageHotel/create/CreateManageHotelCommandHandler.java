package com.kynsoft.finamer.settings.application.command.manageHotel.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageHotelKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.settings.domain.dto.*;
import com.kynsoft.finamer.settings.domain.rules.manageHotel.ManageHotelCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageHotel.ManageHotelCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.manageHotel.ManageHotelNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.services.*;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageHotel.ProducerReplicateManageHotelService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageHotelCommandHandler implements ICommandHandler<CreateManageHotelCommand> {

    private final IManageHotelService service;

    private final IManagerCountryService countryService;

    private final IManageCityStateService cityStateService;

    private final IManagerCurrencyService currencyService;

    private final IManageTradingCompaniesService tradingCompaniesService;

    private final IManageRegionService manageRegionService;

    private final ProducerReplicateManageHotelService producerReplicateManageHotelService;

    public CreateManageHotelCommandHandler(IManageHotelService service, IManagerCountryService countryService,
            IManageCityStateService cityStateService, IManagerCurrencyService currencyService,
            IManageTradingCompaniesService tradingCompaniesService, IManageRegionService manageRegionService,
            ProducerReplicateManageHotelService producerReplicateManageHotelService) {
        this.service = service;
        this.countryService = countryService;
        this.cityStateService = cityStateService;
        this.currencyService = currencyService;
        this.tradingCompaniesService = tradingCompaniesService;
        this.manageRegionService = manageRegionService;
        this.producerReplicateManageHotelService = producerReplicateManageHotelService;
    }

    @Override
    public void handle(CreateManageHotelCommand command) {
        RulesChecker.checkRule(new ManageHotelCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageHotelNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManageHotelCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManageCountry(), "manageCountry",
                "Manage Country cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManageCityState(), "manageCityState",
                "Manage City State cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManageCurrency(), "manageCurrency",
                "Manage Currency State cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManageRegion(), "manageRegion",
                "Manage Region cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManageTradingCompanies(), "manageTradingCompanies",
                "Manage Trading Company cannot be null."));

        ManagerCountryDto countryDto = countryService.findById(command.getManageCountry());
        ManageCityStateDto cityStateDto = cityStateService.findById(command.getManageCityState());
        ManagerCurrencyDto currencyDto = currencyService.findById(command.getManageCurrency());
        ManageTradingCompaniesDto tradingCompaniesDto = command.getManageTradingCompanies() != null
                ? tradingCompaniesService.findById(command.getManageTradingCompanies())
                : null;
        ManageRegionDto regionDto = manageRegionService.findById(command.getManageRegion());

        service.create(new ManageHotelDto(
                command.getId(), command.getCode(), command.getDescription(), command.getStatus(),
                command.getName(), command.getBabelCode(), countryDto, cityStateDto, command.getCity(),
                command.getAddress(), currencyDto, regionDto, tradingCompaniesDto, command.getApplyByTradingCompany(),
                command.getPrefixToInvoice(), command.getIsVirtual(), command.getRequiresFlatRate(),
                command.getIsApplyByVCC(), command.getAutoApplyCredit()));

        this.producerReplicateManageHotelService.create(new ReplicateManageHotelKafka(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getIsApplyByVCC(),
                command.getManageTradingCompanies(),
                command.getStatus().name(),
                command.getRequiresFlatRate(),
                command.getIsVirtual(),
                command.getApplyByTradingCompany(),
                command.getAutoApplyCredit()
        ));
    }
}
