package com.kynsoft.finamer.creditcard.application.command.manageCollectionStatus.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageCollectionStatusMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_COLLECTION_STATUS";

    public CreateManageCollectionStatusMessage(UUID id) {
        this.id = id;
    }
}
