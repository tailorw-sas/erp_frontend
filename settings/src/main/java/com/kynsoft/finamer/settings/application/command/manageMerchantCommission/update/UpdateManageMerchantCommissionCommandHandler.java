package com.kynsoft.finamer.settings.application.command.manageMerchantCommission.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManageCreditCardTypeDto;
import com.kynsoft.finamer.settings.domain.dto.ManageMerchantCommissionDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerMerchantDto;
import com.kynsoft.finamer.settings.domain.rules.manageMerchantCommission.ManageMerchantCommissionFromDateAndToDateRule;
import com.kynsoft.finamer.settings.domain.rules.manageMerchantCommission.ManageMerchantCommissionMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.services.IManageCreditCardTypeService;
import com.kynsoft.finamer.settings.domain.services.IManageMerchantCommissionService;
import com.kynsoft.finamer.settings.domain.services.IManagerMerchantService;
import java.time.LocalDate;
import java.util.UUID;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageMerchantCommissionCommandHandler implements ICommandHandler<UpdateManageMerchantCommissionCommand> {
    
    private final IManagerMerchantService serviceMerchantService;
    private final IManageCreditCardTypeService serviceCurrencyService;
    private final IManageMerchantCommissionService service;

    public UpdateManageMerchantCommissionCommandHandler(IManagerMerchantService serviceMerchantService,
                                                       IManageCreditCardTypeService serviceCurrencyService,
                                                       IManageMerchantCommissionService service) {
        this.serviceMerchantService = serviceMerchantService;
        this.serviceCurrencyService = serviceCurrencyService;
        this.service = service;
    }

    @Override
    public void handle(UpdateManageMerchantCommissionCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManagerMerchant(), "managerMerchant", "Manage Merchant ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManageCreditCartType(), "manageCreditCartType", "Manage Credit Cart Type ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Merchant Commission Type ID cannot be null."));

        RulesChecker.checkRule(new ManageMerchantCommissionMustBeUniqueRule(this.service, command.getId(), command.getManagerMerchant(), command.getManageCreditCartType(), command.getFromDate(), command.getToDate()));

        ManageMerchantCommissionDto test = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setDescription, command.getDescription(), test.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setCalculationType, command.getCalculationType(), test.getCalculationType(), update::setUpdate);

        this.updateManageCreditCartType(test::setManageCreditCartType, command.getManageCreditCartType(), test.getManageCreditCartType().getId(), update::setUpdate);
        this.updateManagerMerchant(test::setManagerMerchant, command.getManagerMerchant(), test.getManagerMerchant().getId(), update::setUpdate);

        LocalDate toDate = command.getToDate() != null ? command.getToDate() : LocalDate.parse("4000-12-31");
        RulesChecker.checkRule(new ManageMerchantCommissionMustBeUniqueRule(this.service, command.getId(), command.getManagerMerchant(), command.getManageCreditCartType(), command.getFromDate(), toDate));
        RulesChecker.checkRule(new ManageMerchantCommissionFromDateAndToDateRule(command.getFromDate(), toDate));
        if (this.updateDate(test::setFromDate, command.getFromDate(), test.getFromDate(), update::setUpdate)) {
            RulesChecker.checkRule(new ManageMerchantCommissionFromDateAndToDateRule(command.getFromDate(), test.getToDate()));
        }
        if (this.updateDate(test::setToDate, toDate, test.getToDate(), update::setUpdate)) {
            RulesChecker.checkRule(new ManageMerchantCommissionFromDateAndToDateRule(test.getFromDate(), toDate));
        }

        UpdateIfNotNull.updateDouble(test::setCommission, command.getCommission(), test.getCommission(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(test);
        }

    }

    private boolean updateManageCreditCartType(Consumer<ManageCreditCardTypeDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManageCreditCardTypeDto creditCardTypeDto = this.serviceCurrencyService.findById(newValue);
            setter.accept(creditCardTypeDto);
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

    private boolean updateDate(Consumer<LocalDate> setter, LocalDate newValue, LocalDate oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

            return true;
        }
        return false;
    }

}
