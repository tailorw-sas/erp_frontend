package com.kynsoft.finamer.creditcard.application.command.manageCollectionStatus.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageCollectionStatusDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageCollectionStatusService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageCollectionStatusCommandHandler implements ICommandHandler<UpdateManageCollectionStatusCommand> {

    private final IManageCollectionStatusService service;


    public UpdateManageCollectionStatusCommandHandler(IManageCollectionStatusService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageCollectionStatusCommand command) {
        ManageCollectionStatusDto dto = service.findById(command.getId());
        dto.setName(command.getName());
        this.service.update(dto);
    }

}
