package com.kynsoft.finamer.payment.application.command.manageResourceType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.ManageResourceTypeDto;
import com.kynsoft.finamer.payment.domain.services.IManageResourceTypeService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageResourceTypeCommandHandler implements ICommandHandler<DeleteManageResourceTypeCommand> {

    private final IManageResourceTypeService service;

    public DeleteManageResourceTypeCommandHandler(IManageResourceTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageResourceTypeCommand command) {
        ManageResourceTypeDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}
