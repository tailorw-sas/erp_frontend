package com.kynsoft.finamer.creditcard.application.command.manageCollectionStatus.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageCollectionStatusDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageCollectionStatusService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageCollectionStatusCommandHandler implements ICommandHandler<DeleteManageCollectionStatusCommand> {

    private final IManageCollectionStatusService service;

    public DeleteManageCollectionStatusCommandHandler(IManageCollectionStatusService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageCollectionStatusCommand command) {
        ManageCollectionStatusDto dto = service.findById(command.getId());

        service.delete(dto);
    }
}
