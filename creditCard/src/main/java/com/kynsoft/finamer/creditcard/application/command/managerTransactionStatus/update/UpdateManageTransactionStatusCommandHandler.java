package com.kynsoft.finamer.creditcard.application.command.managerTransactionStatus.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.creditcard.domain.dto.ManageTransactionStatusDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import com.kynsoft.finamer.creditcard.domain.rules.manageTransactionStatus.*;
import com.kynsoft.finamer.creditcard.domain.services.IManageTransactionStatusService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class UpdateManageTransactionStatusCommandHandler implements ICommandHandler<UpdateManageTransactionStatusCommand> {

    private final IManageTransactionStatusService service;

    public UpdateManageTransactionStatusCommandHandler(IManageTransactionStatusService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageTransactionStatusCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Transaction Status ID cannot be null."));
        if (command.isSentStatus()){
            RulesChecker.checkRule(new ManageTransactionSentStatusMustBeUniqueRule(this.service, command.getId()));
        }
        if (command.isRefundStatus()){
            RulesChecker.checkRule(new ManageTransactionRefundStatusMustBeUniqueRule(this.service, command.getId()));
        }
        if (command.isReceivedStatus()){
            RulesChecker.checkRule(new ManageTransactionReceivedStatusMustBeUniqueRule(this.service, command.getId()));
        }
        if (command.isCancelledStatus()){
            RulesChecker.checkRule(new ManageTransactionCancelledStatusMustBeUniqueRule(this.service, command.getId()));
        }
        if (command.isDeclinedStatus()){
            RulesChecker.checkRule(new ManageTransactionDeclinedStatusMustBeUniqueRule(this.service, command.getId()));
        }

        ManageTransactionStatusDto dto = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDescription, command.getDescription(), dto.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setEnablePayment, command.getEnablePayment(), dto.getEnablePayment(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setVisible, command.getVisible(), dto.getVisible(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setSentStatus, command.isSentStatus(), dto.isSentStatus(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setRefundStatus, command.isRefundStatus(), dto.isRefundStatus(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setReceivedStatus, command.isReceivedStatus(), dto.isReceivedStatus(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setCancelledStatus, command.isCancelledStatus(), dto.isCancelledStatus(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setDeclinedStatus, command.isDeclinedStatus(), dto.isDeclinedStatus(), update::setUpdate);
        updateStatus(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);
        updateNavigate(dto::setNavigate, command.getNavigate(), dto.getNavigate().stream().map(ManageTransactionStatusDto::getId).collect(Collectors.toList()), update::setUpdate);

        if(update.getUpdate() > 0){
            this.service.update(dto);
        }
    }

    private void updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);
        }
    }

    private void updateNavigate(Consumer<List<ManageTransactionStatusDto>> setter, List<UUID> newValue, List<UUID> oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            List<ManageTransactionStatusDto> dtoList = this.service.findByIds(newValue);
            setter.accept(dtoList);
            update.accept(1);

        }
    }
}
