package com.kynsoft.finamer.payment.application.command.manageClient.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.ManageClientDto;
import com.kynsoft.finamer.payment.domain.services.IManageClientService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageClientCommandHandler implements ICommandHandler<UpdateManageClientCommand> {

    private final IManageClientService service;

    public UpdateManageClientCommandHandler(IManageClientService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageClientCommand command) {

        ManageClientDto dto = this.service.findById(command.getId());
        dto.setName(command.getName());
        service.update(dto);
    }
}
