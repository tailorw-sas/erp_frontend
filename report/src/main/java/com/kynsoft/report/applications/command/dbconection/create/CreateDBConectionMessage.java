package com.kynsoft.report.applications.command.dbconection.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateDBConectionMessage implements ICommandMessage {

    private final UUID id;
    private final String command = "CREATE_DB_CONNECTION";


    public CreateDBConectionMessage(UUID id) {
        this.id = id;
    }
}
