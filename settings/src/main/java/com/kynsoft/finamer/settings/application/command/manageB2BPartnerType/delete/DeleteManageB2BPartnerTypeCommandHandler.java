package com.kynsoft.finamer.settings.application.command.manageB2BPartnerType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageB2BPartnerTypeDto;
import com.kynsoft.finamer.settings.domain.services.IManageB2BPartnerTypeService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageB2BPartnerTypeCommandHandler implements ICommandHandler<DeleteManageB2BPartnerTypeCommand> {

    private final IManageB2BPartnerTypeService service;

    public DeleteManageB2BPartnerTypeCommandHandler(IManageB2BPartnerTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageB2BPartnerTypeCommand command) {
        ManageB2BPartnerTypeDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}
