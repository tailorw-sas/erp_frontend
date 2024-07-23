package com.kynsoft.finamer.creditcard.application.command.manageCollectionStatus.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteManageCollectionStatusMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MANAGE_COLLECTION_STATUS";

    public DeleteManageCollectionStatusMessage(UUID id) {
        this.id = id;
    }
}
