package com.kynsoft.finamer.settings.application.command.manageCollectionStatus.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageCollectionStatusMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_COLLECTION_STATUS";

    public UpdateManageCollectionStatusMessage(UUID id) {
        this.id = id;
    }
}
