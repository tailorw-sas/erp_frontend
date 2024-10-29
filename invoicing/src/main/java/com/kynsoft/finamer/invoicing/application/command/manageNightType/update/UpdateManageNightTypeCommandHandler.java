package com.kynsoft.finamer.invoicing.application.command.manageNightType.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageNightTypeDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageNightTypeService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageNightTypeCommandHandler implements ICommandHandler<UpdateManageNightTypeCommand> {

    private final IManageNightTypeService service;

    public UpdateManageNightTypeCommandHandler(IManageNightTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageNightTypeCommand command) {
        ManageNightTypeDto test = this.service.findById(command.getId());
        test.setName(command.getName());
        this.service.update(test);
    }


}
