package com.kynsoft.finamer.settings.application.command.managePaymentTransactionStatus.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentTransactionStatusDto;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentTransactionStatusDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.NavigatePaymentTransactionStatus;
import com.kynsoft.finamer.settings.domain.dtoEnum.NavigateTransactionStatus;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentTransactionStatusService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@Component
public class UpdateManagePaymentTransactionStatusCommandHandler implements ICommandHandler<UpdateManagePaymentTransactionStatusCommand> {

    private final IManagePaymentTransactionStatusService service;

    public UpdateManagePaymentTransactionStatusCommandHandler(IManagePaymentTransactionStatusService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManagePaymentTransactionStatusCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Module ID cannot be null."));

        ManagePaymentTransactionStatusDto dto = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

    
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDescription, command.getDescription(), dto.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setRequireValidation, command.getRequireValidation(), dto.getRequireValidation(), update::setUpdate);
        this.updateStatus(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);

        List<ManagePaymentTransactionStatusDto> managePaymentTransactionStatusDtoList = service.findByIds(command.getNavigate());


        this.updateRelatedStatus(dto::setRelatedStatuses, managePaymentTransactionStatusDtoList, update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(dto);
        }
    }



    private boolean updateRelatedStatus(Consumer<List<ManagePaymentTransactionStatusDto>> setter, List<ManagePaymentTransactionStatusDto> newValue, Consumer<Integer> update) {

        setter.accept(newValue);
        update.accept(1);
        return true;
    }


    private boolean updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

            return true;
        }
        return false;
    }

    private boolean updateSet(Consumer<Set<NavigatePaymentTransactionStatus>> setter, Set<NavigatePaymentTransactionStatus> newValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.isEmpty()) {
            setter.accept(newValue);
            update.accept(1);

            return true;
        }
        return false;
    }
}
