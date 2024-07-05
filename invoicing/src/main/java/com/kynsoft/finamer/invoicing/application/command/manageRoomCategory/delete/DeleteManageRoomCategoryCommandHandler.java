package com.kynsoft.finamer.invoicing.application.command.manageRoomCategory.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomCategoryDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomCategoryService;
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
