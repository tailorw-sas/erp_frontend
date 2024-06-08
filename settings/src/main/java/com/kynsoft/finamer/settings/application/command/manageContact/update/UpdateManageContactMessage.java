package com.kynsoft.finamer.settings.application.command.manageContact.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageContactMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_CONTACT";

    public UpdateManageContactMessage(UUID id) {
        this.id = id;
    }
}
