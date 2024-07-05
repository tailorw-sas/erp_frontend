package com.kynsoft.finamer.settings.application.command.manageRoomCategory.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageAgencyTypeDto;
import com.kynsoft.finamer.settings.domain.dto.ManageRoomCategoryDto;
import com.kynsoft.finamer.settings.domain.services.IManageAgencyTypeService;
import com.kynsoft.finamer.settings.domain.services.IManageRoomCategoryService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageRoomCategoryCommandHandler implements ICommandHandler<DeleteManageRoomCategoryCommand> {

    private final IManageRoomCategoryService service;

    public DeleteManageRoomCategoryCommandHandler(IManageRoomCategoryService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageRoomCategoryCommand command) {
        ManageRoomCategoryDto dto = service.findById(command.getId());

        service.delete(dto);
    }
}
