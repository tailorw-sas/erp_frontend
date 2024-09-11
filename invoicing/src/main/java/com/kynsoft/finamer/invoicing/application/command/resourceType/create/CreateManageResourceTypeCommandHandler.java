package com.kynsoft.finamer.invoicing.application.command.resourceType.create;


import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ResourceTypeDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageResourceTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageResourceTypeCommandHandler implements ICommandHandler<CreateManageResourceTypeCommand> {

    private final IManageResourceTypeService service;

    public CreateManageResourceTypeCommandHandler(IManageResourceTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageResourceTypeCommand command) {

      
        service.create(new ResourceTypeDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.isInvoice()
        ));
    }
}
