package com.kynsoft.finamer.settings.application.command.managerTransactionStatus.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManageTransactionStatusDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.NavigateTransactionStatus;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManageTransactionStatusService;
import java.util.Set;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageTransactionStatusCommandHandler implements ICommandHandler<UpdateManageTransactionStatusCommand> {

    private final IManageTransactionStatusService service;

    public UpdateManageTransactionStatusCommandHandler(IManageTransactionStatusService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageTransactionStatusCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Transaction Status ID cannot be null."));

        ManageTransactionStatusDto test = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setDescription, command.getDescription(), test.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setName, command.getName(), test.getName(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(test::setEnablePayment, command.getEnablePayment(), test.getEnablePayment(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(test::setVisible, command.getVisible(), test.getVisible(), update::setUpdate);
        this.updateStatus(test::setStatus, command.getStatus(), test.getStatus(), update::setUpdate);
        this.updateSet(test::setNavigate, command.getNavigate(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(test);
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

    private boolean updateSet(Consumer<Set<NavigateTransactionStatus>> setter, Set<NavigateTransactionStatus> newValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.isEmpty()) {
            setter.accept(newValue);
            update.accept(1);

            return true;
        }
        return false;
    }

}
