package com.kynsoft.finamer.creditcard.application.command.manageHotelPaymentStatus.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.creditcard.domain.dto.ManageHotelPaymentStatusDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import com.kynsoft.finamer.creditcard.domain.rules.manageHotelPaymentStatus.*;
import com.kynsoft.finamer.creditcard.domain.services.IManageHotelPaymentStatusService;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class UpdateManageHotelPaymentStatusCommandHandler implements ICommandHandler<UpdateManageHotelPaymentStatusCommand> {

    private final IManageHotelPaymentStatusService service;

    public UpdateManageHotelPaymentStatusCommandHandler(IManageHotelPaymentStatusService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageHotelPaymentStatusCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "ID cannot be null."));
        RulesChecker.checkRule(new ManageHotelPaymentStatusNameMustNotBeNullRule(command.getName()));

        if (command.isInProgress()){
            RulesChecker.checkRule(new ManageHotelPaymentStatusInProgressMustBeUniqueRule(this.service, command.getId()));
        }
        if (command.isCompleted()){
            RulesChecker.checkRule(new ManageHotelPaymentStatusCompletedMustBeUniqueRule(this.service, command.getId()));
        }
        if (command.isCancelled()){
            RulesChecker.checkRule(new ManageHotelPaymentStatusCancelledMustBeUniqueRule(this.service, command.getId()));
        }
        if (command.isApplied()){
            RulesChecker.checkRule(new ManageHotelPaymentStatusAppliedMustBeUniqueRule(this.service, command.getId()));
        }

        ManageHotelPaymentStatusDto dto = this.service.findById(command.getId());
        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        updateStatus(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDescription, command.getDescription(), dto.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setInProgress, command.isInProgress(), dto.isInProgress(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setCompleted, command.isCompleted(), dto.isCompleted(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setCancelled, command.isCancelled(), dto.isCancelled(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setApplied, command.isApplied(), dto.isApplied(), update::setUpdate);

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
}
