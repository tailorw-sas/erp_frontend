package com.kynsoft.finamer.settings.application.command.manageMerchantCurrency.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManagerCurrencyDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerMerchantCurrencyDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerMerchantDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.rules.managerMerchantCurrency.ManagerMerchantCurrencyMustBeUniqueByIdRule;
import com.kynsoft.finamer.settings.domain.services.IManagerCurrencyService;
import com.kynsoft.finamer.settings.domain.services.IManagerMerchantCurrencyService;
import com.kynsoft.finamer.settings.domain.services.IManagerMerchantService;
import java.util.UUID;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

@Component
public class UpdateManagerMerchantCurrencyCommandHandler implements ICommandHandler<UpdateManagerMerchantCurrencyCommand> {
    
    private final IManagerMerchantService serviceMerchantService;
    private final IManagerCurrencyService serviceCurrencyService;
    private final IManagerMerchantCurrencyService serviceMerchantCurrency;

    public UpdateManagerMerchantCurrencyCommandHandler(IManagerMerchantService serviceMerchantService,
                                                       IManagerCurrencyService serviceCurrencyService,
                                                       IManagerMerchantCurrencyService serviceMerchantCurrency) {
        this.serviceMerchantService = serviceMerchantService;
        this.serviceCurrencyService = serviceCurrencyService;
        this.serviceMerchantCurrency = serviceMerchantCurrency;
    }

    @Override
    public void handle(UpdateManagerMerchantCurrencyCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manager Merchant Currency ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManagerMerchant(), "manageMerchant", "Manager Merchant ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManagerCurrency(), "manageCurrency", "Manager Currency ID cannot be null."));

        ManagerMerchantCurrencyDto test = this.serviceMerchantCurrency.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();
        this.updateManagerCurrency(test::setManagerCurrency, command.getManagerCurrency(), test.getManagerCurrency().getId(), update::setUpdate);
        this.updateManagerMerchant(test::setManagerMerchant, command.getManagerMerchant(), test.getManagerMerchant().getId(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setDescription, command.getDescription(), test.getDescription(), update::setUpdate);

        RulesChecker.checkRule(new ManagerMerchantCurrencyMustBeUniqueByIdRule(this.serviceMerchantCurrency, command.getManagerMerchant(), command.getManagerCurrency(), command.getId()));
        UpdateIfNotNull.updateDouble(test::setValue, command.getValue(), test.getValue(), update::setUpdate);

        updateStatus(test::setStatus, command.getStatus(), test.getStatus(), update::setUpdate);
        if (update.getUpdate() > 0) {
            this.serviceMerchantCurrency.update(test);
        }

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

    private boolean updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

            return true;
        }
        return false;
    }

}
