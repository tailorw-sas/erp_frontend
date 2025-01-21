package com.kynsoft.finamer.insis.application.command.manageRoomCategory.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.insis.domain.dto.ManageRoomCategoryDto;
import com.kynsoft.finamer.insis.domain.services.IManageRoomCategoryService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageRoomCategoryCommandHandler implements ICommandHandler<UpdateManageRoomCategoryCommand> {

    private final IManageRoomCategoryService service;

    public UpdateManageRoomCategoryCommandHandler(IManageRoomCategoryService service){
        this.service = service;
    }

    @Override
    public void handle(UpdateManageRoomCategoryCommand command) {
        ManageRoomCategoryDto dto = service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, dto.getName(), command.getName(), update::setUpdate);

        service.create(dto);
    }
}
