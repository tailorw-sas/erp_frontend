package com.kynsoft.finamer.invoicing.application.command.manageRoomType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CreateManageRoomTypeCommand implements ICommand {

    private final UUID id;
    private final String code;
    private final String name;
    private final String status;

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageRoomTypeMessage(id);
    }
}
