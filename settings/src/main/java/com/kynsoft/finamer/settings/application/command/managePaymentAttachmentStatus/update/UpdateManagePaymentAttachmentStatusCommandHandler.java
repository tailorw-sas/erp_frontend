package com.kynsoft.finamer.settings.application.command.managePaymentAttachmentStatus.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentAttachmentStatusDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentAttachmentStatusService;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class UpdateManagePaymentAttachmentStatusCommandHandler implements ICommandHandler<UpdateManagePaymentAttachmentStatusCommand> {
    
    private final IManagePaymentAttachmentStatusService service;
    
    public UpdateManagePaymentAttachmentStatusCommandHandler(final IManagePaymentAttachmentStatusService service) {
        this.service = service;
    }
    
    @Override
    public void handle(UpdateManagePaymentAttachmentStatusCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Module ID cannot be null."));
        ManagePaymentAttachmentStatusDto dto = service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDescription, command.getDescription(), dto.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setNavigate, command.getNavigate(), dto.getNavigate(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setModule, command.getModule(), dto.getModule(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setPermissionCode, command.getPermissionCode(), dto.getPermissionCode(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setShow, command.getShow(), dto.getShow(), update::setUpdate);

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
