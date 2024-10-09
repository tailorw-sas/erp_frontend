package com.kynsoft.finamer.creditcard.application.command.managerTransactionStatus.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.creditcard.domain.dto.ManageTransactionStatusDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import com.kynsoft.finamer.creditcard.domain.services.IManageTransactionStatusService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

@Component
public class UpdateManageTransactionStatusCommandHandler implements ICommandHandler<UpdateManageTransactionStatusCommand> {

    private final IManageTransactionStatusService service;

    public UpdateManageTransactionStatusCommandHandler(IManageTransactionStatusService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageTransactionStatusCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Transaction Status ID cannot be null."));

        ManageTransactionStatusDto dto = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDescription, command.getDescription(), dto.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setEnablePayment, command.getEnablePayment(), dto.getEnablePayment(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setVisible, command.getVisible(), dto.getVisible(), update::setUpdate);
        this.updateStatus(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);
        List<ManageTransactionStatusDto> navigate = this.service.findByIds(command.getNavigate().stream().toList());
        dto.setNavigate(navigate);
        this.service.update(dto);
    }

    private void updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

        }
    }
}
