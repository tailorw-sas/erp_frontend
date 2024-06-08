package com.kynsoft.notification.application.command.advertisingcontent.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteAdvertisingContentMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_ADVERTISING_CONTENT";

    public DeleteAdvertisingContentMessage(UUID id) {
        this.id = id;
    }

}
