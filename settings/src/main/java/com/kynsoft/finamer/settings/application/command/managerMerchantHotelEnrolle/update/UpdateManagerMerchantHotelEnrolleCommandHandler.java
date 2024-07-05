package com.kynsoft.finamer.settings.application.command.managerMerchantHotelEnrolle.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.settings.domain.dto.ManageMerchantHotelEnrolleDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerCurrencyDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerMerchantDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.rules.manageMechantHotelEnrolle.ManagerMerchantHotelEnrolleMustBeUniqueByIdRule;
import com.kynsoft.finamer.settings.domain.services.IManageHotelService;
import com.kynsoft.finamer.settings.domain.services.IManageMerchantHotelEnrolleService;
import com.kynsoft.finamer.settings.domain.services.IManagerCurrencyService;
import com.kynsoft.finamer.settings.domain.services.IManagerMerchantService;
import java.util.UUID;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

@Component
public class UpdateManagerMerchantHotelEnrolleCommandHandler implements ICommandHandler<UpdateManagerMerchantHotelEnrolleCommand> {
    
    private final IManagerMerchantService serviceMerchantService;
    private final IManagerCurrencyService serviceCurrencyService;
    private final IManageHotelService serviceHotel;
    private final IManageMerchantHotelEnrolleService service;

    public UpdateManagerMerchantHotelEnrolleCommandHandler(IManagerMerchantService serviceMerchantService,
                                                       IManagerCurrencyService serviceCurrencyService,
                                                       IManageHotelService serviceHotel,
                                                       IManageMerchantHotelEnrolleService service) {
        this.serviceMerchantService = serviceMerchantService;
        this.serviceCurrencyService = serviceCurrencyService;
        this.serviceHotel = serviceHotel;
        this.service = service;
    }

    @Override
    public void handle(UpdateManagerMerchantHotelEnrolleCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Merchant Hotel Enrolle ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManagerMerchant(), "manageMerchant", "Manage Merchant ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManagerCurrency(), "manageCurrency", "Manage Currency ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManagerHotel(), "managerHotel", "Manage Hotel ID cannot be null."));

        ManageMerchantHotelEnrolleDto test = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();
        this.updateManagerCurrency(test::setManagerCurrency, command.getManagerCurrency(), test.getManagerCurrency().getId(), update::setUpdate);
        this.updateManagerMerchant(test::setManagerMerchant, command.getManagerMerchant(), test.getManagerMerchant().getId(), update::setUpdate);
        this.updateManageHotel(test::setManagerHotel, command.getManagerHotel(), test.getManagerHotel().getId(), update::setUpdate);

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setEnrrolle, command.getEnrolle(), test.getEnrrolle(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setKey, command.getKey(), test.getKey(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setDescription, command.getDescription(), test.getDescription(), update::setUpdate);

        this.updateStatus(test::setStatus, command.getStatus(), test.getStatus(), update::setUpdate);
        RulesChecker.checkRule(new ManagerMerchantHotelEnrolleMustBeUniqueByIdRule(this.service, command.getManagerMerchant(), command.getManagerCurrency(), command.getManagerHotel(), command.getEnrolle(), command.getId()));

        if (update.getUpdate() > 0) {
            this.service.update(test);
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

    private boolean updateManageHotel(Consumer<ManageHotelDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManageHotelDto currencyDto = this.serviceHotel.findById(newValue);
            setter.accept(currencyDto);
            update.accept(1);

            return true;
        }
        return false;
    }

    private boolean updateManagerCurrency(Consumer<ManagerCurrencyDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManagerCurrencyDto currencyDto = this.serviceCurrencyService.findById(newValue);
            setter.accept(currencyDto);
            update.accept(1);

            return true;
        }
        return false;
    }

    private boolean updateManagerMerchant(Consumer<ManagerMerchantDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManagerMerchantDto merchantDto = this.serviceMerchantService.findById(newValue);
            setter.accept(merchantDto);
            update.accept(1);

            return true;
        }
        return false;
    }

}
