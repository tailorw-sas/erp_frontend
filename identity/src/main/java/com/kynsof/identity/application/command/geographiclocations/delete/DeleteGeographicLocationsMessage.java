package com.kynsof.identity.application.command.geographiclocations.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteGeographicLocationsMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_GEOGRAPHIC_LOCATION";

    public DeleteGeographicLocationsMessage(UUID id) {
        this.id = id;
    }

}
