package com.kynsoft.finamer.settings.application.command.manageRoomType.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UpdateManageRoomTypeCommand implements ICommand {

    private UUID id;
    private String description;
    private Status status;
    private String name;
    private UUID manageHotel;

    public static UpdateManageRoomTypeCommand fromRequest(UpdateManageRoomTypeRequest request, UUID id){
        return new UpdateManageRoomTypeCommand(
                id, request.getDescription(), request.getStatus(), request.getName(),
                request.getManageHotel()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageRoomTypeMessage(id);
    }
}
