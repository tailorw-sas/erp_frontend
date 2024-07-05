package com.kynsoft.finamer.invoicing.application.command.manageClient.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageClientMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_CLIENT";

    public UpdateManageClientMessage(UUID id) {
        this.id = id;
    }

}
