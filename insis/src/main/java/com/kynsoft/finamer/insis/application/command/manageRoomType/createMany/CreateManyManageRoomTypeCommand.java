package com.kynsoft.finamer.insis.application.command.manageRoomType.createMany;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.insis.application.command.manageRoomType.create.CreateRoomTypeCommand;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
import java.util.List;

@Getter
@Setter
public class CreateManyManageRoomTypeCommand implements ICommand {

    private UUID hotel;
    private List<CreateRoomTypeCommand> roomTypeCommandList;

    public CreateManyManageRoomTypeCommand(UUID hotel, List<CreateRoomTypeCommand> roomTypeCommandList){
        this.hotel = hotel;
        this.roomTypeCommandList = roomTypeCommandList;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManyManageRoomTypeMessage();
    }
}
