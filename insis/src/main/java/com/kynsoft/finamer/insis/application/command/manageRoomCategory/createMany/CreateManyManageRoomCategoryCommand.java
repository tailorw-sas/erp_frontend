package com.kynsoft.finamer.insis.application.command.manageRoomCategory.createMany;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.insis.application.command.manageRoomCategory.create.CreateManageRoomCategoryCommand;
import lombok.Getter;
import lombok.Setter;

import javax.validation.OverridesAttribute;
import java.util.List;

@Getter
@Setter
public class CreateManyManageRoomCategoryCommand implements ICommand {

    private List<CreateManageRoomCategoryCommand> roomCategoryCommands;

    public CreateManyManageRoomCategoryCommand(List<CreateManageRoomCategoryCommand> roomCategoryCommands) {
        this.roomCategoryCommands = roomCategoryCommands;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManyManageRoomCategoryMessage();
    }
}
