package com.kynsoft.finamer.creditcard.application.command.manageMerchantCommission.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageMerchantCommissionKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateFields;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.creditcard.domain.dto.ManageCreditCardTypeDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantCommissionDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.CalculationType;
import com.kynsoft.finamer.creditcard.domain.rules.manageMerchantCommission.ManageMerchantCommissionFromDateAndToDateRule;
import com.kynsoft.finamer.creditcard.domain.rules.manageMerchantCommission.ManageMerchantCommissionMustNotOverlapRule;
import com.kynsoft.finamer.creditcard.domain.services.IManageCreditCardTypeService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantCommissionService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;
import java.util.function.Consumer;

@Component
public class UpdateManageMerchantCommissionCommandHandler implements ICommandHandler<UpdateManageMerchantCommissionCommand> {

    private final IManageMerchantService serviceMerchantService;
    private final IManageCreditCardTypeService serviceCurrencyService;
    private final IManageMerchantCommissionService service;

    public UpdateManageMerchantCommissionCommandHandler(IManageMerchantService serviceMerchantService,
                                                        IManageCreditCardTypeService serviceCurrencyService,
                                                        IManageMerchantCommissionService service) {
        this.serviceMerchantService = serviceMerchantService;
        this.serviceCurrencyService = serviceCurrencyService;
        this.service = service;
    }

    @Override
    public void handle(UpdateManageMerchantCommissionCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManagerMerchant(), "manageMerchant", "Manage Merchant ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManageCreditCartType(), "manageCreditCartType", "Manage Credit Cart Type ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Merchant Commission Type ID cannot be null."));

        ManageMerchantCommissionDto existingCommission = this.service.findById(command.getId());

        //LocalDate toDate = command.getToDate() != null ? command.getToDate() : LocalDate.parse("4000-12-31");
        LocalDate toDate = command.getToDate() != null ? command.getToDate() : null;
        RulesChecker.checkRule(new ManageMerchantCommissionMustNotOverlapRule(this.service, command.getId(),
                command.getManagerMerchant(), command.getManageCreditCartType(), 
                command.getFromDate(), toDate, command.getCommission(), command.getCalculationType()
        ));
        ConsumerUpdate update = new ConsumerUpdate();

        updateFields(existingCommission, command, update);

        if (update.getUpdate() > 0) {
            this.service.update(existingCommission);
        }
    }

    private void updateFields(ManageMerchantCommissionDto existingCommission, UpdateManageMerchantCommissionCommand command, ConsumerUpdate update) {
        UpdateFields.updateString(existingCommission::setDescription, command.getDescription(), existingCommission.getDescription(), update::setUpdate);
        updateManageCreditCartType(existingCommission::setManageCreditCartType, command.getManageCreditCartType(), existingCommission.getManageCreditCartType().getId(), update::setUpdate);
        updateManagerMerchant(existingCommission::setManageMerchant, command.getManagerMerchant(), existingCommission.getManageMerchant().getId(), update::setUpdate);
        updateCalculationType(existingCommission::setCalculationType, command.getCalculationType(), existingCommission.getCalculationType(), update::setUpdate);
//        if (updateDate(existingCommission::setFromDate, command.getFromDate(), existingCommission.getFromDate(), update::setUpdate)) {
//            RulesChecker.checkRule(new ManageMerchantCommissionFromDateAndToDateRule(command.getFromDate(), existingCommission.getToDate()));
//        }

        if (command.getFromDate() != null ) {
            existingCommission.setFromDate(command.getFromDate());
            update.setUpdate(1);
        }

        // Allow toDate to be updated to null
        if (command.getToDate() == null) {
            //LocalDate toDate = LocalDate.parse("4000-12-31");
            LocalDate toDate = null;
            existingCommission.setToDate(toDate);
            update.setUpdate(1);
        } else if (updateDate(existingCommission::setToDate, command.getToDate(), existingCommission.getToDate(), update::setUpdate)) {
            RulesChecker.checkRule(new ManageMerchantCommissionFromDateAndToDateRule(existingCommission.getFromDate(), command.getToDate()));
        }

        UpdateIfNotNull.updateDouble(existingCommission::setCommission, command.getCommission(), existingCommission.getCommission(), update::setUpdate);
        existingCommission.setStatus(command.getStatus());
    }

    private void updateManageCreditCartType(Consumer<ManageCreditCardTypeDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManageCreditCardTypeDto creditCardTypeDto = this.serviceCurrencyService.findById(newValue);
            setter.accept(creditCardTypeDto);
            update.accept(1);
        }
    }

    private void updateManagerMerchant(Consumer<ManageMerchantDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManageMerchantDto merchantDto = this.serviceMerchantService.findById(newValue);
            setter.accept(merchantDto);
            update.accept(1);
        }
    }

    private boolean updateDate(Consumer<LocalDate> setter, LocalDate newValue, LocalDate oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);
            return true;
        }
        return false;
    }

    private boolean updateCalculationType(Consumer<CalculationType> setter, CalculationType newValue, CalculationType oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);
            return true;
        }
        return false;
    }

}