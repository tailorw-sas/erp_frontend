package com.kynsoft.finamer.invoicing.application.command.manageClient.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageClientDto;
import com.kynsoft.finamer.invoicing.domain.services.IManagerClientService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageClientCommandHandler implements ICommandHandler<UpdateManageClientCommand> {

    private final IManagerClientService service;

    public UpdateManageClientCommandHandler(IManagerClientService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageClientCommand command) {
        ManageClientDto test = this.service.findById(command.getId());
        test.setName(command.getName());
        test.setIsNightType(command.getIsNightType());
        test.setStatus(command.getStatus());
        this.service.update(test);
    }

}
