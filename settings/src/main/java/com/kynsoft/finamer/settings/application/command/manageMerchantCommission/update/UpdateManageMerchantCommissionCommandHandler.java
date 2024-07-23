package com.kynsoft.finamer.settings.application.command.manageMerchantCommission.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageMerchantCommissionKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManageCreditCardTypeDto;
import com.kynsoft.finamer.settings.domain.dto.ManageMerchantCommissionDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerMerchantDto;
import com.kynsoft.finamer.settings.domain.rules.manageMerchantCommission.ManageMerchantCommissionFromDateAndToDateRule;
import com.kynsoft.finamer.settings.domain.rules.manageMerchantCommission.ManageMerchantCommissionMustNotOverlapRule;
import com.kynsoft.finamer.settings.domain.services.IManageCreditCardTypeService;
import com.kynsoft.finamer.settings.domain.services.IManageMerchantCommissionService;
import com.kynsoft.finamer.settings.domain.services.IManagerMerchantService;

import java.time.LocalDate;
import java.util.UUID;
import java.util.function.Consumer;

import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageMerchantCommission.ProducerUpdateManageMerchantCommissionService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageMerchantCommissionCommandHandler implements ICommandHandler<UpdateManageMerchantCommissionCommand> {

    private final IManagerMerchantService serviceMerchantService;
    private final IManageCreditCardTypeService serviceCurrencyService;
    private final IManageMerchantCommissionService service;

    private final ProducerUpdateManageMerchantCommissionService producerService;

    public UpdateManageMerchantCommissionCommandHandler(IManagerMerchantService serviceMerchantService,
                                                        IManageCreditCardTypeService serviceCurrencyService,
                                                        IManageMerchantCommissionService service, ProducerUpdateManageMerchantCommissionService producerService) {
        this.serviceMerchantService = serviceMerchantService;
        this.serviceCurrencyService = serviceCurrencyService;
        this.service = service;
        this.producerService = producerService;
    }

    @Override
    public void handle(UpdateManageMerchantCommissionCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManagerMerchant(), "manageMerchant", "Manage Merchant ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManageCreditCartType(), "manageCreditCartType", "Manage Credit Cart Type ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Merchant Commission Type ID cannot be null."));

        ManageMerchantCommissionDto existingCommission = this.service.findById(command.getId());

        LocalDate toDate = command.getToDate() != null ? command.getToDate() : LocalDate.parse("4000-12-31");
        RulesChecker.checkRule(new ManageMerchantCommissionMustNotOverlapRule(this.service, command.getId(),
                command.getManagerMerchant(), command.getManageCreditCartType(), command.getFromDate(), toDate));
        ConsumerUpdate update = new ConsumerUpdate();

        updateFields(existingCommission, command, update);

        if (update.getUpdate() > 0) {
            this.service.update(existingCommission);
            this.producerService.update(new UpdateManageMerchantCommissionKafka(existingCommission.getId(), existingCommission.getManagerMerchant().getId(), existingCommission.getManageCreditCartType().getId(), existingCommission.getCommission(), existingCommission.getCalculationType(), existingCommission.getFromDate(), existingCommission.getToDate(), existingCommission.getStatus().name()));
        }
    }

    private void updateFields(ManageMerchantCommissionDto existingCommission, UpdateManageMerchantCommissionCommand command, ConsumerUpdate update) {
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(existingCommission::setDescription, command.getDescription(), existingCommission.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(existingCommission::setCalculationType, command.getCalculationType(), existingCommission.getCalculationType(), update::setUpdate);

        updateManageCreditCartType(existingCommission::setManageCreditCartType, command.getManageCreditCartType(), existingCommission.getManageCreditCartType().getId(), update::setUpdate);
        updateManagerMerchant(existingCommission::setManagerMerchant, command.getManagerMerchant(), existingCommission.getManagerMerchant().getId(), update::setUpdate);

//        if (updateDate(existingCommission::setFromDate, command.getFromDate(), existingCommission.getFromDate(), update::setUpdate)) {
//            RulesChecker.checkRule(new ManageMerchantCommissionFromDateAndToDateRule(command.getFromDate(), existingCommission.getToDate()));
//        }

        if (command.getFromDate() != null ) {
            existingCommission.setFromDate(command.getFromDate());
            update.setUpdate(1);
        }

        // Allow toDate to be updated to null
        if (command.getToDate() == null) {
            LocalDate toDate = LocalDate.parse("4000-12-31");
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

    private void updateManagerMerchant(Consumer<ManagerMerchantDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManagerMerchantDto merchantDto = this.serviceMerchantService.findById(newValue);
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
}