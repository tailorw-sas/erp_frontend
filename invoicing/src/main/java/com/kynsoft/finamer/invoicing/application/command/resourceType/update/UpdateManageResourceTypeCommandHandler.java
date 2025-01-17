package com.kynsoft.finamer.invoicing.application.command.resourceType.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;

import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateFields;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.invoicing.domain.dto.ResourceTypeDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.rules.resourceType.ResourceTypeDefaultsMustBeUniqueRule;
import com.kynsoft.finamer.invoicing.domain.services.IManageResourceTypeService;
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
        if (command.getDefaults() != null && command.getDefaults()) {
            RulesChecker.checkRule(new ResourceTypeDefaultsMustBeUniqueRule(this.service, command.getId()));
        }
        ResourceTypeDto resourceTypeDto = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(resourceTypeDto::setName, command.getName(), resourceTypeDto.getName(), update::setUpdate);
        UpdateFields.updateString(resourceTypeDto::setDescription, command.getDescription(), resourceTypeDto.getDescription(), update::setUpdate);
        this.updateStatus(resourceTypeDto::setStatus, command.getStatus(), resourceTypeDto.getStatus(), update::setUpdate);
        this.updateBooleam(resourceTypeDto::setDefaults, command.getDefaults(), resourceTypeDto.getDefaults(), update::setUpdate);
        this.updateBooleam(resourceTypeDto::setInvoice, command.isInvoice(), resourceTypeDto.isInvoice(), update::setUpdate);

        resourceTypeDto.setInvoice(command.isInvoice());

        this.service.update(resourceTypeDto);

    }

    private void updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);
        }
    }

    private void updateBooleam(Consumer<Boolean> setter, Boolean newValue, Boolean oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);
        }
    }

}
