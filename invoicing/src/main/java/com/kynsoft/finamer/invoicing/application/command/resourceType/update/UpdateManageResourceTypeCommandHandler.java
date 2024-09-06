package com.kynsoft.finamer.invoicing.application.command.resourceType.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;

import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.invoicing.domain.dto.ResourceTypeDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageResourceTypeService;

import org.springframework.stereotype.Component;

@Component
public class UpdateManageResourceTypeCommandHandler implements ICommandHandler<UpdateManageResourceTypeCommand> {

    private final IManageResourceTypeService service;

    public UpdateManageResourceTypeCommandHandler(IManageResourceTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageResourceTypeCommand command) {

        ResourceTypeDto resourceTypeDto = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(resourceTypeDto::setName, command.getName(),
                resourceTypeDto.getName(), update::setUpdate);
        resourceTypeDto.setInvoice(command.isInvoice());

        this.service.update(resourceTypeDto);

    }

}
