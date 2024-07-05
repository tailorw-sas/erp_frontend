package com.kynsoft.finamer.invoicing.application.command.manageRoomCategory.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateManageRoomCategoryCommand implements ICommand {

    private UUID id;
    private String name;


    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageRoomCategoryMessage(id);
    }
}
