package com.kynsoft.finamer.creditcard.application.command.manageCollectionStatus.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageCollectionStatusDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageCollectionStatusService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageCollectionStatusCommandHandler implements ICommandHandler<CreateManageCollectionStatusCommand> {

    private final IManageCollectionStatusService service;


    public CreateManageCollectionStatusCommandHandler(IManageCollectionStatusService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageCollectionStatusCommand command) {
        this.service.create(new ManageCollectionStatusDto(
                command.getId(), command.getCode(), command.getName() ));
    }
}
