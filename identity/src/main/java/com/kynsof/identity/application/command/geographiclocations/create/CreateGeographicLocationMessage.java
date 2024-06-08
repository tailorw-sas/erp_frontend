package com.kynsof.identity.application.command.geographiclocations.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateGeographicLocationMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_GEOGRAPHIC_LOCATION";

    public CreateGeographicLocationMessage(UUID id) {
        this.id = id;
    }

}
