package com.tailorw.tcaInnsist.application.command.manageConnection.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.tailorw.tcaInnsist.domain.dto.ManageConnectionDto;
import com.tailorw.tcaInnsist.domain.services.IManageConnectionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CreateManageConnectionHandler implements ICommandHandler<CreateManageConnectionCommand> {

    private final IManageConnectionService service;

    @Override
    public void handle(CreateManageConnectionCommand command) {
        ManageConnectionDto dto = new ManageConnectionDto(
                command.getId(),
                command.getServer(),
                command.getPort(),
                command.getDbName(),
                command.getUserName(),
                command.getPassword()
        );

        service.create(dto);
    }
}
