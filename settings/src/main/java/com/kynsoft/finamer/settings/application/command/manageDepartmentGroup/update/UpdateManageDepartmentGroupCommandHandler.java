package com.kynsoft.finamer.settings.application.command.manageDepartmentGroup.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManageDepartmentGroupDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManageDepartmentGroupService;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class UpdateManageDepartmentGroupCommandHandler implements ICommandHandler<UpdateManageDepartmentGroupCommand> {

    private final IManageDepartmentGroupService service;

    public UpdateManageDepartmentGroupCommandHandler(IManageDepartmentGroupService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageDepartmentGroupCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Module ID cannot be null."));

        ManageDepartmentGroupDto dto = this.service.findById(command.getId());

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
