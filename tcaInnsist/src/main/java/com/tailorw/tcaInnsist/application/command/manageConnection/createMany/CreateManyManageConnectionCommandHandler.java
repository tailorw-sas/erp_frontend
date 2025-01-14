package com.tailorw.tcaInnsist.application.command.manageConnection.createMany;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.tailorw.tcaInnsist.domain.dto.ManageConnectionDto;
import com.tailorw.tcaInnsist.domain.services.IManageConnectionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class CreateManyManageConnectionCommandHandler implements ICommandHandler<CreateManyManageConnectionCommand> {

    private final IManageConnectionService service;

    @Override
    public void handle(CreateManyManageConnectionCommand command) {
        List<ManageConnectionDto> manageConnectionDtos = command.getCreateManageConnectionCommands()
                        .stream()
                                .map(connectionCommand -> {
                                    return new ManageConnectionDto(
                                            connectionCommand.getId(),
                                            connectionCommand.getServer(),
                                            connectionCommand.getPort(),
                                            connectionCommand.getDbName(),
                                            connectionCommand.getUserName(),
                                            connectionCommand.getPassword()
                                    );
                                }).toList();
        service.createMany(manageConnectionDtos);
    }
}
