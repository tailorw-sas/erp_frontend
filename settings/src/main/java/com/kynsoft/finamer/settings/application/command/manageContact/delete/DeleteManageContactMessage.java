package com.kynsoft.finamer.settings.application.command.manageContact.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteManageContactMessage implements ICommandMessage {
    private final UUID id;

    private final String command = "DELETE_MANAGE_CONTACT";

    public DeleteManageContactMessage(UUID id) {
        this.id = id;
    }
}
