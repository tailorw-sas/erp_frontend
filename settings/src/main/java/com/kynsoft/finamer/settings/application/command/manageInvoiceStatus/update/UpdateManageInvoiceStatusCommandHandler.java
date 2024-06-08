package com.kynsoft.finamer.settings.application.command.manageInvoiceStatus.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManageInvoiceStatusDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Navigate;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManageInvoiceStatusService;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.function.Consumer;

@Component
public class UpdateManageInvoiceStatusCommandHandler implements ICommandHandler<UpdateManageInvoiceStatusCommand> {

    private final IManageInvoiceStatusService service;

    public UpdateManageInvoiceStatusCommandHandler(IManageInvoiceStatusService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageInvoiceStatusCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Invoice Status ID cannot be null."));

        ManageInvoiceStatusDto dto = service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDescription, command.getDescription(), dto.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        updateStatus(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setEnabledToPrint, command.getEnabledToPrint(), dto.getEnabledToPrint(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setEnabledToPropagate, command.getEnabledToPropagate(), dto.getEnabledToPropagate(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setEnabledToApply, command.getEnabledToApply(), dto.getEnabledToApply(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setEnabledToPolicy, command.getEnabledToPolicy(), dto.getEnabledToPolicy(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setProcessStatus, command.getProcessStatus(), dto.getProcessStatus(), update::setUpdate);
        updateNavigate(dto::setNavigate, command.getNavigate(), dto.getNavigate(), update::setUpdate);

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

    private boolean updateNavigate(Consumer<HashSet<Navigate>> setter, HashSet<Navigate>  newNavigate, HashSet<Navigate> oldNavigate, Consumer<Integer> update) {
        if(newNavigate != null && !newNavigate.equals(oldNavigate)){
            setter.accept(newNavigate);
            update.accept(1);

            return true;
        }
        return false;
    }
}
