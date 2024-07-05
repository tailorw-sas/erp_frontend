package com.kynsoft.finamer.invoicing.application.command.manageRoomCategory.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateManageRoomCategoryCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;


    @Override
    public ICommandMessage getMessage() {
        return new CreateManageRoomCategoryMessage(id);
    }
}
