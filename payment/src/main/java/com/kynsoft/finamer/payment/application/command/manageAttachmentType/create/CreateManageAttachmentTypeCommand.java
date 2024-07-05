package com.kynsoft.finamer.payment.application.command.manageAttachmentType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageAttachmentTypeCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;

    public CreateManageAttachmentTypeCommand(UUID id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageAttachmentTypeMessage(id);
    }
}
