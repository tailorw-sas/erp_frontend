package com.kynsoft.finamer.payment.application.command.resourceType.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.domain.dto.ResourceTypeDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IManageResourceTypeService;

import java.util.function.Consumer;

import org.springframework.stereotype.Component;

@Component
public class UpdateManageResourceTypeCommandHandler implements ICommandHandler<UpdateManageResourceTypeCommand> {

    private final IManageResourceTypeService service;

    public UpdateManageResourceTypeCommandHandler(IManageResourceTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageResourceTypeCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Resource Type ID cannot be null."));

        ResourceTypeDto resourceTypeDto = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(resourceTypeDto::setDescription, command.getDescription(), resourceTypeDto.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(resourceTypeDto::setName, command.getName(), resourceTypeDto.getName(), update::setUpdate);
        this.updateStatus(resourceTypeDto::setStatus, command.getStatus(), resourceTypeDto.getStatus(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(resourceTypeDto);
        }

    }

    private void updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

        }
    }

}
