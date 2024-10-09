package com.kynsoft.finamer.invoicing.application.command.manageRegion.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteManageRegionMessage implements ICommandMessage {
    private final UUID id;

    private final String command = "DELETE_MANAGE_REGION";

    public DeleteManageRegionMessage(UUID id) {
        this.id = id;
    }
}
