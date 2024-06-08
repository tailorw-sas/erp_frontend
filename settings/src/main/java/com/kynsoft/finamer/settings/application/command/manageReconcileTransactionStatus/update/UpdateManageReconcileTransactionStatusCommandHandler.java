package com.kynsoft.finamer.settings.application.command.manageReconcileTransactionStatus.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManageReconcileTransactionStatusDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManageReconcileTransactionStatusService;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class UpdateManageReconcileTransactionStatusCommandHandler implements ICommandHandler<UpdateManageReconcileTransactionStatusCommand> {

    private final IManageReconcileTransactionStatusService service;

    public UpdateManageReconcileTransactionStatusCommandHandler(IManageReconcileTransactionStatusService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageReconcileTransactionStatusCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Module ID cannot be null."));

        ManageReconcileTransactionStatusDto dto = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

    
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDescription, command.getDescription(), dto.getDescription(), update::setUpdate);

        this.updateStatus(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);

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
