package com.kynsoft.finamer.creditcard.application.command.manageMerchantCurrency.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerCurrencyDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerMerchantCurrencyDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import com.kynsoft.finamer.creditcard.domain.services.IManagerCurrencyService;
import com.kynsoft.finamer.creditcard.domain.services.IManagerMerchantCurrencyService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantService;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Consumer;

@Component
public class UpdateManagerMerchantCurrencyCommandHandler implements ICommandHandler<UpdateManagerMerchantCurrencyCommand> {
    
    private final IManageMerchantService serviceMerchantService;
    private final IManagerCurrencyService serviceCurrencyService;
    private final IManagerMerchantCurrencyService serviceMerchantCurrency;

    public UpdateManagerMerchantCurrencyCommandHandler(IManageMerchantService serviceMerchantService,
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
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setValue, command.getValue(), test.getValue(), update::setUpdate);

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

    private boolean updateManagerMerchant(Consumer<ManageMerchantDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManageMerchantDto merchantDto = this.serviceMerchantService.findById(newValue);
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
