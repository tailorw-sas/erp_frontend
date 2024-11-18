package com.kynsoft.finamer.invoicing.application.command.manageRoomType.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomTypeDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageRoomTypeCommandHandler implements ICommandHandler<CreateManageRoomTypeCommand> {

    private final IManageRoomTypeService service;


    public CreateManageRoomTypeCommandHandler(IManageRoomTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageRoomTypeCommand command) {

        service.create(new ManageRoomTypeDto(
                command.getId(), command.getCode(),  command.getName(), command.getStatus()
        ));
    }
}
