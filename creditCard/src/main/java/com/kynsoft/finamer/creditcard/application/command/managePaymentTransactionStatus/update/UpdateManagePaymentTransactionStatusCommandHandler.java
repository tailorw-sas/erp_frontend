package com.kynsoft.finamer.creditcard.application.command.managePaymentTransactionStatus.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.creditcard.domain.dto.ManagePaymentTransactionStatusDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import com.kynsoft.finamer.creditcard.domain.rules.managePaymentTransactionStatus.*;
import com.kynsoft.finamer.creditcard.domain.services.IManagePaymentTransactionStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class UpdateManagePaymentTransactionStatusCommandHandler implements ICommandHandler<UpdateManagePaymentTransactionStatusCommand> {

    private final IManagePaymentTransactionStatus service;

    public UpdateManagePaymentTransactionStatusCommandHandler(IManagePaymentTransactionStatus service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManagePaymentTransactionStatusCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "ID cannot be null."));
        RulesChecker.checkRule(new ManagePaymentTransactionStatusNameMustNotBeNullRule(command.getName()));

        if (command.isInProgress()){
            RulesChecker.checkRule(new ManagePaymentTransactionStatusInProgressMustBeUniqueRule(this.service, command.getId()));
        }
        if (command.isCompleted()){
            RulesChecker.checkRule(new ManagePaymentTransactionStatusCompletedMustBeUniqueRule(this.service, command.getId()));
        }
        if (command.isCancelled()){
            RulesChecker.checkRule(new ManagePaymentTransactionStatusCancelledMustBeUniqueRule(this.service, command.getId()));
        }
        if (command.isApplied()){
            RulesChecker.checkRule(new ManagePaymentTransactionStatusAppliedMustBeUniqueRule(this.service, command.getId()));
        }

        ManagePaymentTransactionStatusDto dto = this.service.findById(command.getId());
        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        updateStatus(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDescription, command.getDescription(), dto.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setInProgress, command.isInProgress(), dto.isInProgress(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setCompleted, command.isCompleted(), dto.isCompleted(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setCancelled, command.isCancelled(), dto.isCancelled(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setApplied, command.isApplied(), dto.isApplied(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setRequireValidation, command.isRequireValidation(), dto.isRequireValidation(), update::setUpdate);
        updateNavigate(dto::setNavigate, command.getNavigate(), dto.getNavigate().stream().map(ManagePaymentTransactionStatusDto::getId).collect(Collectors.toList()), update::setUpdate);

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

    private void updateNavigate(Consumer<List<ManagePaymentTransactionStatusDto>> setter, List<UUID> newValue, List<UUID> oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            List<ManagePaymentTransactionStatusDto> dtoList = this.service.findByIds(newValue);
            setter.accept(dtoList);
            update.accept(1);

        }
    }
}
