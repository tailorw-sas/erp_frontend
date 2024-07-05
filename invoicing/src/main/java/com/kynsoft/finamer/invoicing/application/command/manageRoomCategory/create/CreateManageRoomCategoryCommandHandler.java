package com.kynsoft.finamer.invoicing.application.command.manageRoomCategory.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomCategoryDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomCategoryService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageRoomCategoryCommandHandler implements ICommandHandler<CreateManageRoomCategoryCommand> {

    private final IManageRoomCategoryService service;

    public CreateManageRoomCategoryCommandHandler(IManageRoomCategoryService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageRoomCategoryCommand command) {

        service.create(new ManageRoomCategoryDto(
                command.getId(),
                command.getCode(),

                command.getName()
        ));
    }
}
