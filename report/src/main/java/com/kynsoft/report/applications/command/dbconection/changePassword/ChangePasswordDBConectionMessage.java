package com.kynsoft.report.applications.command.dbconection.changePassword;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ChangePasswordDBConectionMessage implements ICommandMessage {

    private final UUID id;
    private final String command = "UPDATE_DB_CONNECTION_PASSWORD";

    public ChangePasswordDBConectionMessage(UUID id) {
        this.id = id;
    }
}
