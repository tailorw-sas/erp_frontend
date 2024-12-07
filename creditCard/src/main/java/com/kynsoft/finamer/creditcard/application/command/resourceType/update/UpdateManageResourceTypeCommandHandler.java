package com.kynsoft.finamer.creditcard.application.command.resourceType.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ResourceTypeDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageResourceTypeService;
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

        resourceTypeDto.setName(command.getName());
        resourceTypeDto.setVcc(command.isVcc());
        resourceTypeDto.setStatus(command.getStatus());

        this.service.update(resourceTypeDto);
    }

}
