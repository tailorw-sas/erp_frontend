package com.kynsoft.finamer.invoicing.application.command.manageClient.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageClientDto;
import com.kynsoft.finamer.invoicing.domain.services.IManagerClientService;
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
