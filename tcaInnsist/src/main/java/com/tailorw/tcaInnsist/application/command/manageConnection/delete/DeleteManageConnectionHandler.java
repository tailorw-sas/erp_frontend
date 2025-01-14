package com.tailorw.tcaInnsist.application.command.manageConnection.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.tailorw.tcaInnsist.domain.dto.ManageConnectionDto;
import com.tailorw.tcaInnsist.domain.services.IManageConnectionService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageConnectionHandler implements ICommandHandler<DeleteManageConnectionCommand> {

    private final IManageConnectionService service;

    public DeleteManageConnectionHandler(IManageConnectionService service){
        this.service = service;
    }

    @Override
    public void handle(DeleteManageConnectionCommand command) {
        service.delete(command.getId());
    }
}
