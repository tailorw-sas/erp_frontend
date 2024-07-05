package com.kynsoft.finamer.settings.application.command.manageRoomCategory.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateManageRoomCategoryCommand implements ICommand {

    private UUID id;
    private Status status;
    private String name;
    private final String description;

    public static UpdateManageRoomCategoryCommand fromRequest(UpdateManageRoomCategoryRequest request, UUID id){
        return new UpdateManageRoomCategoryCommand(
                id, request.getStatus(), request.getName(),
                request.getDescription()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageRoomCategoryMessage(id);
    }
}
