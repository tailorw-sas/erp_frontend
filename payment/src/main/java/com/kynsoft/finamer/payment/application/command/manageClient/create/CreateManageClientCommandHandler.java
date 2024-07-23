package com.kynsoft.finamer.payment.application.command.manageClient.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.ManageClientDto;
import com.kynsoft.finamer.payment.domain.services.IManageClientService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageClientCommandHandler implements ICommandHandler<CreateManageClientCommand> {

    private final IManageClientService service;

    public CreateManageClientCommandHandler(IManageClientService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageClientCommand command) {
        service.create(new ManageClientDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getStatus()
        ));
    }
}
