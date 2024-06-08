package com.kynsoft.finamer.settings.application.command.manageClient.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageClientDto;
import com.kynsoft.finamer.settings.domain.services.IManagerClientService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageClientCommandHandler implements ICommandHandler<DeleteManageClientCommand> {

    private final IManagerClientService service;

    public DeleteManageClientCommandHandler(IManagerClientService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageClientCommand command) {
        ManageClientDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}
