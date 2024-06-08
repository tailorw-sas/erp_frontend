package com.kynsoft.notification.application.command.advertisingcontent.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateAdvertisingContentMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_ADVERTISING_CONTENT";

    public CreateAdvertisingContentMessage(UUID id) {
        this.id = id;
    }

}
