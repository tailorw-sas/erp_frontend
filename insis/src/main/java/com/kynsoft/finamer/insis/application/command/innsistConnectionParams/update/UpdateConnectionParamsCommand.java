package com.kynsoft.finamer.insis.application.command.innsistConnectionParams.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class UpdateConnectionParamsCommand implements ICommand {

    private UUID id;
    private String hostName;
    private int portNumber;
    private String dataBaseName;
    private String userName;
    private String password;
    private String description;
    private String status;
    private LocalDateTime updatedAt;

    public UpdateConnectionParamsCommand(UUID id, String hostName, int portNumber, String dataBaseName, String userName, String password, String description, String status){
        this.id = Objects.nonNull(id) ? id : UUID.randomUUID();
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.dataBaseName = dataBaseName;
        this.userName = userName;
        this.password = password;
        this.description = description;
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateConnectionParamsMessage(id);
    }
}
