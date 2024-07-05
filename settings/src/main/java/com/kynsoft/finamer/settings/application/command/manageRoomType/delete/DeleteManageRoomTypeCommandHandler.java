package com.kynsoft.finamer.settings.application.command.manageRoomType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageRoomTypeDto;
import com.kynsoft.finamer.settings.domain.services.IManageRoomTypeService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageRoomTypeCommandHandler implements ICommandHandler<DeleteManageRoomTypeCommand> {

    private final IManageRoomTypeService service;

    public DeleteManageRoomTypeCommandHandler(IManageRoomTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageRoomTypeCommand command) {
        ManageRoomTypeDto dto = service.findById(command.getId());

        service.delete(dto);
    }
}
