package com.tailorw.finamer.scheduler.application.command.businessProcess.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateBusinessProcessCommand implements ICommand {

    private UUID id;
    private String processName;
    private String description;
    private String status;

    public UpdateBusinessProcessCommand(UUID id, String processName, String description, String status){
        this.id = id;
        this.processName = processName;
        this.description = description;
        this.status = status;
    }

    public static UpdateBusinessProcessCommand fromRequest(UUID id, UpdateBusinessProcessRequest request){
        return new UpdateBusinessProcessCommand(id,
                request.getProcessName(),
                request.getDescription(),
                request.getStatus());
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateBusinessProcessMessage(id);
    }
}
