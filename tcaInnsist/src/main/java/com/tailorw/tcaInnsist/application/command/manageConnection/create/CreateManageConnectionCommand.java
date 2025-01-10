package com.tailorw.tcaInnsist.application.command.manageConnection.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageConnectionCommand implements ICommand {

    private UUID id;
    private String server;
    private String port;
    private String dbName;
    private String userName;
    private String password;

    public CreateManageConnectionCommand(UUID id, String server, String port, String dbName, String userName, String password){
        this.id = id;
        this.server = server;
        this.port = port;
        this.dbName = dbName;
        this.userName = userName;
        this.password = password;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageConnectionMessage(id);
    }
}
