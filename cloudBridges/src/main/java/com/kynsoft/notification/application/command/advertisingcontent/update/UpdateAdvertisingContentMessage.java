package com.kynsoft.notification.application.command.advertisingcontent.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateAdvertisingContentMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_ADVERTISING_CONTENT";

    public UpdateAdvertisingContentMessage(UUID id) {
        this.id = id;
    }

}
