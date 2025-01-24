package com.kynsoft.finamer.insis.application.command.manageRoomCategory.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.insis.domain.dto.ManageRoomCategoryDto;
import com.kynsoft.finamer.insis.domain.services.IManageRoomCategoryService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageRoomCategoryCommandHandler implements ICommandHandler<CreateManageRoomCategoryCommand> {

    private final IManageRoomCategoryService service;

    public CreateManageRoomCategoryCommandHandler(IManageRoomCategoryService service){
        this.service = service;
    }

    @Override
    public void handle(CreateManageRoomCategoryCommand command) {
        ManageRoomCategoryDto dto = new ManageRoomCategoryDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getStatus(),
                command.getUpdatedAt()
        );
        service.create(dto);
    }
}
