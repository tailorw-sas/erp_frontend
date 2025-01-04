package com.kynsoft.report.applications.command.dbconection.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateDBConectionMessage implements ICommandMessage {

    private final UUID id;
    private final String command = "UPDATE_DB_CONNECTION";

    public UpdateDBConectionMessage(UUID id) {
        this.id = id;
    }
}
