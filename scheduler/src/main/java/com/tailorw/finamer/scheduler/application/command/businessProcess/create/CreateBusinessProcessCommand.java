package com.tailorw.finamer.scheduler.application.command.businessProcess.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateBusinessProcessCommand implements ICommand {

    private UUID id;
    private String code;
    private String processName;
    private String description;
    private String status;

    public CreateBusinessProcessCommand(String code, String processName, String description, String status){
        this.id = UUID.randomUUID();
        this.code = code;
        this.processName = processName;
        this.description = description;
        this.status = status;
    }

    public static CreateBusinessProcessCommand fromRequest(CreateBusinessProcessRequest request){
        return new CreateBusinessProcessCommand(
                request.getCode(),
                request.getProcessName(),
                request.getDescription(),
                request.getStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateBusinessProcessMessage(id);
    }
}
