package com.kynsoft.finamer.invoicing.application.command.manageClient.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageClientKafka;
import com.kynsoft.finamer.invoicing.domain.dto.ManageClientDto;

import com.kynsoft.finamer.invoicing.domain.services.IManagerClientService;

import org.springframework.stereotype.Component;

@Component
public class CreateManageClientCommandHandler implements ICommandHandler<CreateManageClientCommand> {

    private final IManagerClientService service;

    public CreateManageClientCommandHandler(IManagerClientService service) {
        this.service = service;

    }

    @Override
    public void handle(CreateManageClientCommand command) {

        service.create(new ManageClientDto(
                command.getId(),
                command.getCode(),
                command.getName()

        ));

    }
}
