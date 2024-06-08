package com.kynsoft.finamer.settings.application.command.manageContact.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageContactMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_CONTACT";

    public CreateManageContactMessage(UUID id) {
        this.id = id;
    }
}
