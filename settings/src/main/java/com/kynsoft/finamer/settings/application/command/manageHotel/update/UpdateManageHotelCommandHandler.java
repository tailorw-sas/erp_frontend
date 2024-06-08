package com.kynsoft.finamer.settings.application.command.manageHotel.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.*;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.*;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Consumer;

@Component
public class UpdateManageHotelCommandHandler implements ICommandHandler<UpdateManageHotelCommand> {

    private final IManageHotelService service;

    private final IManagerCountryService countryService;

    private final IManageCityStateService cityStateService;

    private final IManagerCurrencyService currencyService;

    private final IManageTradingCompaniesService tradingCompaniesService;

    private final IManageRegionService regionService;


    public UpdateManageHotelCommandHandler(IManageHotelService service, IManagerCountryService countryService, IManageCityStateService cityStateService, IManagerCurrencyService currencyService, IManageTradingCompaniesService tradingCompaniesService, IManageRegionService regionService) {
        this.service = service;
        this.countryService = countryService;
        this.cityStateService = cityStateService;
        this.currencyService = currencyService;
        this.tradingCompaniesService = tradingCompaniesService;
        this.regionService = regionService;
    }

    @Override
    public void handle(UpdateManageHotelCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Hotel ID cannot be null."));

        ManageHotelDto dto = service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDescription, command.getDescription(), dto.getDescription(), update::setUpdate);
        updateStatus(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setBabelCode, command.getBabelCode(), dto.getBabelCode(), update::setUpdate);
        updateCountry(dto::setManageCountry, command.getManageCountry(), dto.getManageCountry().getId(), update::setUpdate);
        updateCityState(dto::setManageCityState, command.getManageCityState(), dto.getManageCityState().getId(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setCity, command.getCity(), dto.getCity(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setAddress, command.getAddress(), dto.getAddress(), update::setUpdate);
        updateCurrency(dto::setManageCurrency, command.getManageCurrency(), dto.getManageCurrency().getId(), update::setUpdate);
        updateRegion(dto::setManageRegion, command.getManageRegion(), dto.getManageRegion().getId(), update::setUpdate);
        updateTradingCompanies(dto::setManageTradingCompanies, command.getManageTradingCompanies(), dto.getManageTradingCompanies().getId(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setApplyByTradingCompany, command.getApplyByTradingCompany(), dto.getApplyByTradingCompany(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setPrefixToInvoice, command.getPrefixToInvoice(), dto.getPrefixToInvoice(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setIsVirtual, command.getIsVirtual(), dto.getIsVirtual(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setRequiresFlatRate, command.getRequiresFlatRate(), dto.getRequiresFlatRate(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setIsApplyByVCC, command.getIsApplyByVCC(), dto.getIsApplyByVCC(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(dto);
        }
    }

    private boolean updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

            return true;
        }
        return false;
    }

    private boolean updateCountry(Consumer<ManagerCountryDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManagerCountryDto countryDto = countryService.findById(newValue);
            setter.accept(countryDto);
            update.accept(1);

            return true;
        }
        return false;
    }

    private boolean updateCityState(Consumer<ManageCityStateDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManageCityStateDto cityStateDto = cityStateService.findById(newValue);
            setter.accept(cityStateDto);
            update.accept(1);

            return true;
        }
        return false;
    }

    private boolean updateCurrency(Consumer<ManagerCurrencyDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManagerCurrencyDto currencyDto = currencyService.findById(newValue);
            setter.accept(currencyDto);
            update.accept(1);

            return true;
        }
        return false;
    }

    private boolean updateRegion(Consumer<ManageRegionDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManageRegionDto regionDto = regionService.findById(newValue);
            setter.accept(regionDto);
            update.accept(1);

            return true;
        }
        return false;
    }

    private boolean updateTradingCompanies(Consumer<ManageTradingCompaniesDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManageTradingCompaniesDto tradingCompaniesDto = tradingCompaniesService.findById(newValue);
            setter.accept(tradingCompaniesDto);
            update.accept(1);

            return true;
        }
        return false;
    }
}
