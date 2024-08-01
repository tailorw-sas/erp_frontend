package com.kynsoft.finamer.invoicing.application.command.resourceType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteManageResourceTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MANAGE_RESOURCE_TYPE";

    public DeleteManageResourceTypeMessage(UUID id) {
        this.id = id;
    }

}
