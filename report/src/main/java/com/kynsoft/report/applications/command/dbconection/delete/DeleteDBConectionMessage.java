package com.kynsoft.report.applications.command.dbconection.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteDBConectionMessage implements ICommandMessage {

    private final UUID id;
    private final String command = "DELETE_DB_CONNECTION";

    public DeleteDBConectionMessage(UUID id) {
        this.id = id;
    }
}
